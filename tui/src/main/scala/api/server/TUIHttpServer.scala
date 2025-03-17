package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.TUIRoutes
import org.slf4j.{Logger, LoggerFactory}
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
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
