package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.AuthController

class AuthRouter @Inject()(controller: AuthController) extends SimpleRouter {

    val prefix = "/v1"

  override def routes: Routes = {
    case POST(p"/login") =>
      controller.index
    case GET(p"/me") =>
      controller.me
    case PUT(p"/me/thumbnail") =>
      controller.setThumbnail
    case PATCH(p"/me/profile") =>
      controller.changeProfile
  }
}