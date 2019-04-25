package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.StoreOverviewController


class StoreOverviewRouter @Inject()(controller: StoreOverviewController) extends SimpleRouter {
  val prefix = "/v1/store"

  override def routes: Routes = {
    case GET(p"/overview") =>
      controller.index
  }
}
