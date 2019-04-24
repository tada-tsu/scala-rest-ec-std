package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.AdminUserController

class AdminUserRouter @Inject()(controller: AdminUserController) extends SimpleRouter {
  val prefix = "/v1/admin"

  override def routes: Routes = {
    case GET(p"/user") =>
      controller.list
    case POST(p"/user") =>
      controller.create
  }

}
