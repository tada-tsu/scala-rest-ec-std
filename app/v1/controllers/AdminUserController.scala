package v1.controllers

import javax.inject.Inject
import models.{RegisterUser, User}
import play.api.libs.json.Json
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class AdminUserController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {
  def list = ECActionWithAdminAuth.async {
    implicit request =>

      Future {
        Ok(Json.toJson(User.all))
      }
  }

  def create = ECActionWithAdminAuth.async { implicit request =>
    Future {
      val json = request.body.asJson
      json.map(
        json =>
          RegisterUser.form.bind(json).fold(
            errors => {
              BadRequest(Json.obj(
                "register" -> "failed",
                "errors" -> errors.errorsAsJson,
              ))
            },
            form => {
              User.register(form) match {
                case Some(user) => {
                  Ok(Json.toJson(user))
                }
                case None => {
                  BadRequest
                }
              }

            }
          )
      )
        .getOrElse(
          BadRequest
        )
    }
  }
}
