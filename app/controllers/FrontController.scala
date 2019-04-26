package controllers

import javax.inject.Inject
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class FrontController  @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc) {
  def index() = ECAction.async{
    implicit session=>
      Future{
        Ok(views.html.index())
      }
  }
}
