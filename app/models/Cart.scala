package models

import play.api.Logger
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.AnyContent
import scalikejdbc._
import v1.ECRequest
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation._
import play.api.data.validation.Constraints._


case class Cart(
                 id: Int,
                 user_id: Int,
                 product_id: Int,
                 quantity: Int,
               )

object Cart extends SQLSyntaxSupport[Cart] {
  override val tableName = "carts"
  val c = Cart.syntax("c")

  implicit val writes = new Writes[Cart] {
    def writes(cart: Cart): JsValue = {
      Json.obj(
        "id" -> cart.id,
        "user_id" -> cart.user_id,
        "product_id" -> cart.product_id,
        "quantity" -> cart.quantity,
      )
    }
  }

  def apply(p: ResultName[Cart])(rs: WrappedResultSet): Cart = Cart(
    rs.int(p.id),
    rs.int(p.user_id),
    rs.int(p.product_id),
    rs.int(p.quantity),
  )

  def getMyCart(request: ECRequest[AnyContent]) = {
    Logger.debug(s"Cart: ${(Cart.column.id, User.me(request).get.id).toString()}")
    val cart = DB readOnly { implicit session =>
      withSQL {
        select
          .from(Cart as c)
          .where
          .eq(Cart.column.user_id, User.me(request).get.id)
      }.map(Cart(c.resultName)).list.apply()
    }
    println(cart)

    val amount: Int = cart.foldLeft(0)((a: Int, b: Cart) => {
      a + Product.find(b.product_id).get.price * b.quantity
    })

    println(amount)

    new MyCart(cart, amount)
  }

  def addProduct(product_id: Int, quantity: Int = 1, request: ECRequest[AnyContent]): MyCart = {
    val cn = Cart.column
    DB localTx {
      implicit session =>

        withSQL {
          select
            .from(Cart as c)
            .where
            .eq(c.product_id, product_id)
            .and
            .eq(c.user_id, User.me(request).get.id)
            .limit(1)
        }.map(Cart(c.resultName)).single.apply() match {
          case Some(cart) => {
            cart.quantity match {
              case q if q > 1 || quantity > 0 => {
                Logger.debug("DB: Update (Cart)")
                applyUpdate {
                  update(Cart)
                    .set(
                      cn.quantity -> (cart.quantity + quantity)
                    )
                    .where
                    .eq(cn.product_id, product_id)
                    .and
                    .eq(cn.user_id, User.me(request).get.id)
                }
              }
              case _ => {
                Logger.debug("DB: Drop (Cart)")
                applyUpdate {
                  deleteFrom(Cart)
                    .where
                    .eq(cn.product_id, product_id)
                    .and
                    .eq(cn.user_id, User.me(request).get.id)
                }
              }
            }
          }
          case _ => {
            Logger.debug("DB: Insert (Cart)")
            applyUpdate {
              insert
                .into(Cart)
                .columns(cn.user_id, cn.product_id, cn.quantity)
                .values(User.me(request).get.id, product_id, quantity)
            }
          }
        }
    }
    getMyCart(request)
  }

  def removeProduct(user_id: Int, product_id: Int) = {
    DB localTx { implicit session =>
      applyUpdate {
        delete
          .from(Cart)
          .where
          .eq(Cart.column.user_id, user_id)
          .and
          .eq(Cart.column.product_id, product_id)
      }
    }
  }
}

case class MyCart(cart: List[Cart], amount: Int)

object MyCart {
  implicit val writes: Writes[MyCart] = (myCart: MyCart) => {
    Json.obj(
      "items" -> myCart.cart,
      "amount" -> myCart.amount,
    )
  }
}

case class AddCartForm(quantity: Int)

object AddCartForm {
  val form = Form(
    mapping(
      "quantity" -> number.verifying(min(1), max(99))
    )(AddCartForm.apply)(AddCartForm.unapply)
  )
}

