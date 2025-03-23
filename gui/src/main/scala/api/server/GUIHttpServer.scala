package api.server

import akka.actor.ActorSystem
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
  private val CORE_OBSERVER_URL = "http://localhost:8082/api/core/registerObserver"

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    registerObserverHttp(GUI_OBSERVER_URL)
    //val gui = GUI()
    val server = Http()
      .newServerAt(GUI_HOST, GUI_PORT)

  private def routes(guiRoutes: GUIRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("tui") {
          concat(
            guiRoutes.guiRoutes
          )
        }
      )
    }

  private def registerObserverHttp(guiObserverUrl: String): Unit =
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = CORE_OBSERVER_URL,
        entity = HttpEntity(
          ContentTypes.`application/json`,
          Json.obj("url" -> guiObserverUrl).toString
        )
      )
    )
