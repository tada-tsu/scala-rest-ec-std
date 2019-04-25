package v1.controllers

import javax.inject.Inject
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class AdminOverviewController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {
  def index = ECActionWithAdminAuth.async {
    implicit request =>
      Future {
        Ok("")
      }
  }

  def put = ECActionWithAdminAuth.async {
    implicit request =>
      Future {
        Ok("")
      }

  }
}