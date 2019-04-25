package v1.controllers

import javax.inject.Inject
import models.{AddCartForm, Cart, User}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent._

class StoreCartController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {
  def index: Action[AnyContent] = ECActionWithAuth.async {
    implicit request =>
      Future {
        Logger.debug("StoreCartController: index")
        User.me(request) match {
          case Some(me) => {
            Ok(Json.toJson(Cart.getMyCart(request)))
          }
          case None => {
            NotFound
          }
        }
      }
  }

  def store(product_id: Int) = ECActionWithAuth.async {
    implicit request =>
      Future {
        Logger.debug("StoreCartController: store")

        val json = request.body.asJson
        json.map(
          json =>
            AddCartForm.form.bind(json).fold(
              errors => BadRequest,
              form =>
                User.me(request) match {
                  case Some(me) => {
                    Ok(Json.toJson(Cart.addProduct(product_id, form.quantity, request)))
                  }
                  case _ => {
                    Unauthorized
                  }
                },

            )
        )
          .getOrElse(
            BadRequest
          )
      }
  }

  def delete(product_id: Int) = ECActionWithAuth.async {
    implicit request =>
      Future {
        Logger.debug("StoreCartController: delete")

        Cart.removeProduct(User.me(request).get.id, product_id)
        Ok(Json.toJson(Cart.getMyCart(request)))
      }
  }
}