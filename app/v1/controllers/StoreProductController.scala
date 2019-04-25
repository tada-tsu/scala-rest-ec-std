package v1.controllers

import javax.inject.Inject
import models.Product
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class StoreProductController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {
  def list = ECAction.async {
    implicit request =>
      Future {
        Ok(Json.toJson(Product.all()))
      }
  }

  def detail(path: String): Action[AnyContent] = ECAction.async {
    implicit request =>
      Future {
        Product.getByPath(path) match {
          case Some(p) => {
            Logger.debug(p.toString)
            Ok(Json.toJson(p))
          }
          case _ => {
            NotFound(Json.toJson(Json.obj("error" -> "商品が存在しません")))
          }
        }

      }
  }
}