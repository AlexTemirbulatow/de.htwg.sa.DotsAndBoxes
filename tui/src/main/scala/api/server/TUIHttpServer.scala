package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.TUIRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext
import tuiComponent.TUI
import play.api.libs.json.Json

object TUIHttpServer:
  private val TUI_HOST = "localhost"
  private val TUI_PORT = 8084
  private val TUI_OBSERVER_URL = s"http://$TUI_HOST:$TUI_PORT/api/tui/update"
  private val CORE_BASE_URL = "http://localhost:8082/api/core/"

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    registerObserverHttp
    val tui = new TUI
    val server = Http()
      .newServerAt(TUI_HOST, TUI_PORT)
      .bind(routes(TUIRoutes(tui)))
    CoordinatedShutdown(system).addJvmShutdownHook(deregisterObserverHttp)
    tui.run

  private def routes(tuiRoutes: TUIRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("tui") {
          concat(
            tuiRoutes.tuiRoutes
          )
        }
      )
    }

  private def registerObserverHttp: Unit =
    sendObserverRequest("registerObserver")

  private def deregisterObserverHttp: Unit =
    sendObserverRequest("deregisterObserver")

  private def sendObserverRequest(endpoint: String): Unit =
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = CORE_BASE_URL.concat(endpoint),
        entity = HttpEntity(
          ContentTypes.`application/json`,
          Json.obj("url" -> TUI_OBSERVER_URL).toString
        )
      )
    )
