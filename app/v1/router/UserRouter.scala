package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.UserController

/**
  * Routes and URLs to the PostResource controller.
  */
class UserRouter @Inject()(controller: UserController) extends SimpleRouter {
  val prefix = "/v1/user"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.index

    //    case POST(p"/") =>
    //      controller.process

    //    case GET(p"/$id") =>
    //      controller.show(id)
  }

}
