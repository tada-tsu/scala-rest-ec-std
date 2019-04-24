package v1

import enums._
import javax.inject.Inject
import models.User
import net.logstash.logback.marker.LogstashMarker
import play.api.{Logger, MarkerContext}
import play.api.http.{FileMimeTypes, HttpVerbs}
import play.api.i18n.{Langs, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

/**
  * A wrapped request for ec resources.
  *
  * This is commonly used to hold request-specific information like
  * security credentials, and useful shortcut methods.
  */
trait ECRequestHeader extends MessagesRequestHeader with PreferredMessagesProvider

class ECRequest[A](request: Request[A], val messagesApi: MessagesApi) extends WrappedRequest(request) with ECRequestHeader

/**
  * Provides an implicit marker that will show the request in all logger statements.
  */
trait RequestMarkerContext {

  import net.logstash.logback.marker.Markers

  private def marker(tuple: (String, Any)) = Markers.append(tuple._1, tuple._2)

  private implicit class RichLogstashMarker(marker1: LogstashMarker) {
    def &&(marker2: LogstashMarker): LogstashMarker = marker1.and(marker2)
  }

  implicit def requestHeaderToMarkerContext(implicit request: RequestHeader): MarkerContext = {
    MarkerContext {
      marker("id" -> request.id) && marker("host" -> request.host) && marker("remoteAddress" -> request.remoteAddress)
    }
  }

}

/**
  * The action builder for the EC resource.
  *
  * This is the place to put logging, metrics, to augment
  * the request with contextual data, and manipulate the
  * result.
  */
class ECActionBuilder @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                               (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[ECRequest, AnyContent]
    with RequestMarkerContext
    with HttpVerbs {

  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent

  type ECRequestBlock[A] = ECRequest[A] => Future[Result]

  private val logger = Logger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: ECRequestBlock[A]): Future[Result] = {
    // Convert to marker context and use request in block
    implicit val markerContext: MarkerContext = requestHeaderToMarkerContext(request)
    logger.trace(s"invokeBlock: ")

    val future = block(new ECRequest(request, messagesApi))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case other =>
          result
      }
    }
  }
}

/**
  * With auth
  */

class ECActionBuilderWithAuth @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                                       (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[ECRequest, AnyContent]
    with RequestMarkerContext
    with HttpVerbs {
  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent
  type ECRequestBlock[A] = ECRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A],
                              block: ECRequestBlock[A]): Future[Result] = {
    Logger.info("Auth: Required auth.")

    val future = block(new ECRequest(request, messagesApi))
    val failed = Results.Status(403).apply(Json.obj(
      "auth" -> "failed"
    ))

    future.map {
      result =>
        request.headers.get("Authorization") match {
          case Some(authHeader) => {
            Logger.trace(s"Auth: Header ${authHeader}")

            val Pattern = """Bearer (.*)""".r
            request.headers.get("Authorization").get match {

              case Pattern(token) => {

                Logger.debug(s"Auth: Bearer: ${token}")
                request.headers.get("Authorization-User-Screen") match {

                  case Some(screen_name) => {
                    Logger.debug(s"Auth: screen_name ${screen_name}")

                    User.tokenAuth(screen_name, token) match {
                      case Valid() => {
                        result
                      }
                      case Invalid() => {
                        failed
                      }
                      case Expired() => {
                        failed
                      }
                    }
                  }
                  case _ => {
                    failed
                  }

                }

              }

              case _ => {
                failed
              }

            }

          }

          case None => {
            failed
          }
        }
    }
  }
}

/**
  * With admin auth
  */

class ECActionBuilderWithAdminAuth @Inject()(messagesApi: MessagesApi, playBodyParsers: PlayBodyParsers)
                                       (implicit val executionContext: ExecutionContext)
  extends ActionBuilder[ECRequest, AnyContent]
    with RequestMarkerContext
    with HttpVerbs {
  override val parser: BodyParser[AnyContent] = playBodyParsers.anyContent
  type ECRequestBlock[A] = ECRequest[A] => Future[Result]

  override def invokeBlock[A](request: Request[A],
                              block: ECRequestBlock[A]): Future[Result] = {
    Logger.info("Auth: Required auth.")

    val future = block(new ECRequest(request, messagesApi))
    val failed = Results.Status(403).apply(Json.obj(
      "auth" -> "failed"
    ))

    future.map {
      result =>
        request.headers.get("Authorization") match {
          case Some(authHeader) => {
            Logger.trace(s"Auth: Header ${authHeader}")

            val Pattern = """Bearer (.*)""".r
            request.headers.get("Authorization").get match {

              case Pattern(token) => {

                Logger.debug(s"Auth: Bearer: ${token}")
                request.headers.get("Authorization-User-Screen") match {

                  case Some(screen_name) => {
                    Logger.debug(s"Auth: screen_name ${screen_name}")

                    User.tokenAuth(screen_name, token, true) match {
                      case ValidAdmin() => {
                        result
                      }
                      case _ => {
                        failed
                      }
                    }
                  }
                  case _ => {
                    failed
                  }

                }

              }

              case _ => {
                failed
              }

            }

          }

          case None => {
            failed
          }
        }
    }
  }
}

/**
  * Packages up the component dependencies for the ec controller.
  *
  * This is a good way to minimize the surface area exposed to the controller, so the
  * controller only has to have one thing injected.
  */
case class ECControllerComponents @Inject()(ecActionBuilderWithAuth: ECActionBuilderWithAuth,
                                            ecActionBuilderWithAdminAuth: ECActionBuilderWithAdminAuth,
                                            ecActionBuilder: ECActionBuilder,
                                            actionBuilder: DefaultActionBuilder,
                                            parsers: PlayBodyParsers,
                                            messagesApi: MessagesApi,
                                            langs: Langs,
                                            fileMimeTypes: FileMimeTypes,
                                            executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents

/**
  * Exposes actions and handler to the ECController by wiring the injected state into the base class.
  */
class ECBaseController @Inject()(ecc: ECControllerComponents) extends BaseController with RequestMarkerContext {
  override protected def controllerComponents: ControllerComponents = ecc

  def ECAction: ECActionBuilder = ecc.ecActionBuilder

  def ECActionWithAuth: ECActionBuilderWithAuth = ecc.ecActionBuilderWithAuth

  def ECActionWithAdminAuth: ECActionBuilderWithAdminAuth = ecc.ecActionBuilderWithAdminAuth
}