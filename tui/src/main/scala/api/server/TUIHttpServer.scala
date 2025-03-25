package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.TUIRoutes
import api.utils.TUICoreRequestHttp
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import tuiComponent.TUI

object TUIHttpServer:
  private val TUI_HOST = "localhost"
  private val TUI_PORT = 8084
  private val TUI_OBSERVER_URL = s"http://$TUI_HOST:$TUI_PORT/api/tui/update"

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    TUICoreRequestHttp.registerTUIObserver(TUI_OBSERVER_URL)
    val tui = new TUI
    val server = Http()
      .newServerAt(TUI_HOST, TUI_PORT)
      .bind(routes(TUIRoutes(tui)))
    CoordinatedShutdown(system).addJvmShutdownHook(shutdown)
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

  private def shutdown: Future[Unit] =
    logger.info("Shutting down TUIHttpServer...")
    TUICoreRequestHttp.deregisterTUIObserver(TUI_OBSERVER_URL)
