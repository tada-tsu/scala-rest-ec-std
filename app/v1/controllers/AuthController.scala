package v1.controllers

import javax.inject.Inject
import models.{RegisterUser, User, UserProfile}
import play.api.Logger
import play.api.libs.json._
import play.api.mvc._
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


class AuthController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {


  def login: Action[AnyContent] = ECAction.async {
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

  def me: Action[AnyContent] = ECActionWithAuth.async {
    implicit request => {
      Future {
        User.me(request) match {
          case Some(user)=>{
            Ok(Json.toJson(user))
          }
          case None => {
            BadRequest
          }
        }
      }
    }
  }

  def register: Action[AnyContent] = ECAction.async {
    implicit request =>

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

  //  def setThumbnail = ECActionWithAuth.async {
  //    implicit request =>
  //      Future {
  //        Ok("ok")
  //      }
  //  }

  //  def changeProfile = ECActionWithAuth.async {
  //    implicit request =>
  //      Future {
  //        //      println(request.body)
  //        println(request.body.asJson.get.validate[UserProfile])
  //        request.body.asJson match {
  //          case Some(json) => {
  //            val profile = json.validate[UserProfile] match {
  //              case p: JsSuccess[UserProfile] => p.get
  //            }
  //            request.headers.get("Authorization-User-Screen") match {
  //              case Some(screen_name) => {
  //                User.findByScreenName(screen_name) match {
  //                  case Some(user) => {
  //                    println(user)
  //                    User.setProfile(user, profile, user.id)
  //                    println(user)
  //                  }
  //                }
  //              }
  //            }
  //
  //            Ok("ok")
  //          }
  //          case None => {
  //            BadRequest
  //          }
  //        }
  //      }
  //  }

}

