package models

import play.api.data.Form
import play.api.data.Forms.{mapping, nonEmptyText}
import play.api.data.validation.Constraints
import play.api.libs.json.{JsValue, Json, Writes}
import scalikejdbc._


case class Product(
                    id: Int,
                    path: String,
                    title: String,
                    description: String,
                    stock: Int,
                    price: Int,
                    sale_price: Option[Int]
                  )

object Product extends SQLSyntaxSupport[Product] {
  override val tableName = "products"
  val p = Product.syntax("p")

  implicit val writes = new Writes[Product] {
    def writes(product: Product): JsValue = {
      Json.obj(
        "id" -> product.id,
        "path" -> product.path,
        "title" -> product.title,
        "description" -> product.description,
        "stock" -> product.stock,
        "price" -> product.price,
        "sale_price" -> product.sale_price,
      )
    }
  }

  def apply(p: ResultName[Product])(rs: WrappedResultSet): Product = Product(
    rs.int(p.id),
    rs.string(p.path),
    rs.string(p.title),
    rs.string(p.description),
    rs.int(p.stock),
    rs.int(p.price),
    rs.intOpt(p.sale_price),
  )

  def all(): List[Product] = {
    DB readOnly { implicit session =>
      withSQL {
        select.from(Product as p)
      }.map(Product(p.resultName)).list.apply()
    }
  }

  def count() = {
    DB readOnly { implicit session: DBSession =>
      withSQL {
        select(sqls.count)
          .from(Product as p)
      }.map(rs => rs.long(1)).single.apply().get
    }
  }

  def create(
              path: String,
              title: String,
              description: String,
              stock: Int,
              price: Int,
              sale_price: Option[Int],
            ) = {

    DB localTx { implicit session =>
      val p = Product.column
      applyUpdate {
        insert.into(Product).columns(
          p.path,
          p.title,
          p.description,
          p.stock,
          p.price,
          p.sale_price,
        ).values(
          path,
          title,
          description,
          stock,
          price,
          sale_price,
        )
      }
    }
  }

  def getByPath(path: String): Option[Product] = {
    DB readOnly {
      implicit session =>
        withSQL {
          select
            .from(Product as p)
            .where
            .eq(p.path, path)
            .limit(1)
        }.map(Product(p.resultName)).single.apply()
    }
  }

  def find(id: Int): Option[Product] = {
    DB readOnly {
      implicit session =>
        withSQL {
          select
            .from(Product as p)
            .where
            .eq(p.id, id)
            .limit(1)
        }.map(Product(p.resultName)).single.apply()
    }
  }
}