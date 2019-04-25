package v1.controllers

import javax.inject.Inject
import models.Setting
import play.api.libs.json.Json
import v1.{ECBaseController, ECControllerComponents}

import scala.concurrent.{ExecutionContext, Future}

class StoreOverviewController @Inject()(ecc: ECControllerComponents)(implicit ec: ExecutionContext) extends ECBaseController(ecc)  {
  def index = ECAction.async{
    implicit request=>
      Future {
        val overview = Setting.overview
        Ok(Json.toJson(overview))
      }
  }
}
