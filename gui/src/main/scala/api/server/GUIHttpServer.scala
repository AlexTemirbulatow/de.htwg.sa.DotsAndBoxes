package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.GUIRoutes
import guiComponent.GUI
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext

object GUIHttpServer:
  private val GUI_HOST = "localhost"
  private val GUI_PORT = 8085
  private val GUI_OBSERVER_URL = s"http://$GUI_HOST:$GUI_PORT/api/gui/update"
  private val CORE_BASE_URL = "http://localhost:8082/api/core/"

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    registerObserverHttp
    val gui = GUI()
    val server = Http()
      .newServerAt(GUI_HOST, GUI_PORT)
      .bind(routes(GUIRoutes(gui)))
    CoordinatedShutdown(system).addJvmShutdownHook(deregisterObserverHttp)
    gui.run

  private def routes(guiRoutes: GUIRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("gui") {
          concat(
            guiRoutes.guiRoutes
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
          Json.obj("url" -> GUI_OBSERVER_URL).toString
        )
      )
    )
