package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.AdminOverviewController

class AdminOverviewRouter @Inject()(controller: AdminOverviewController) extends SimpleRouter {
  val prefix = "/v1/admin"

  override def routes: Routes = {
    case GET(p"/overview") =>
      controller.index
    case PUT(p"/overview") =>
      controller.put
  }

}
