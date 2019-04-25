package v1.router

import javax.inject.Inject
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import v1.controllers.StoreCartController


class StoreCartRouter @Inject()(controller: StoreCartController) extends SimpleRouter {
  val prefix = "/v1/store"

  override def routes: Routes = {
    case GET(p"/cart") =>
      controller.index
    case POST(p"/cart/${p_i_s @ int(product_id)}*") =>
      controller.store(product_id)
    case DELETE(p"/cart/${p_i_s @ int(product_id)}*") =>
      controller.delete(product_id)

  }
}
