package v1.controllers

import javax.inject.Inject
import models.{User, UserProfile}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import scalikejdbc.DB
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


class AuthController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {


  def index: Action[AnyContent] = ECAction.async {
    implicit request =>
      Future {
        request.body.asJson match {
          case Some(b) => {
            val body = request.body.asJson.get
            val login = (body \ "login").as[String]
            val pass = (body \ "pass").as[String]

            User.findByCredential(login) match {
              case Some(u: User) => {
                Logger.debug(s"Auth: ${u.toString}")

                User.auth(u, pass) match {
                  case (token: String, token_expired_at: String) => {
                    Ok(Json.toJson(Json.obj(
                      "token" -> token,
                      "token_expired_at" -> token_expired_at,
                      "user" -> u,
                    )))
                  }
                  case _ => {
                    BadRequest("can not authenticate")
                  }
                }
              }
              case None => {
                BadRequest("can not authenticate")
              }
            }

          }
          case None => {
            BadRequest("has not request body")
          }
          case _ => {
            BadGateway("Uncaught error")
          }
        }
      }
  }

  def me = ECActionWithAuth.async {
    implicit request => {
      Future {
        request.headers.get("Authorization-User-Screen") match {
          case Some(screen_name) => {
            User.findByScreenName(screen_name) match {
              case Some(user) => {
                Ok(Json.toJson(user))
              }
              case None => {
                BadRequest("can not authenticate")
              }
            }
          }
          case None => {
            BadRequest("can not authenticate")
          }
        }
      }
    }
  }

  def setThumbnail = ECActionWithAuth.async {
    implicit request =>
    Future {
      Ok("ok")
    }
  }

  def changeProfile = ECActionWithAuth.async {
    implicit request =>
    Future {
//      わからん
      Ok("ok")
    }
  }

}

