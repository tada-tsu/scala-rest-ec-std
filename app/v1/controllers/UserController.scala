package v1.controllers

import javax.inject.Inject
import models.User
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}


class UserController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {

  private val logger = Logger(getClass)

  def index: Action[AnyContent] = ECAction.async {
    implicit request =>
      logger.info("index: ")
      Future {
        Ok(Json.toJson(
          User.all()
        ))
      }
  }

}

