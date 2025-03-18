package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.TUIRoutes
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future, Await}
import tuiComponent.TUI

object TUIHttpServer:
  private val TUI_HOST = "localhost"
  private val TUI_PORT = 8084

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val tui = new TUI
    tui.run
    registerObserverHttp(s"http://$TUI_HOST:$TUI_PORT/api/tui/update")
    println("\n############HERE WE GO")
    val server = Http()
      .newServerAt(TUI_HOST, TUI_PORT)
      .bind(routes(TUIRoutes(tui)))
    Await.result(Future.never, Duration.Inf)

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

  private def registerObserverHttp(tuiObserverUrl: String): Unit =
    val requestUrl = "http://localhost:8082/api/core/registerObserver"
    Http().singleRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = requestUrl,
        entity = HttpEntity(ContentTypes.`application/json`, tuiObserverUrl)
      )
    )
