package tui.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{TUI_BASE_URL, TUI_HOST, TUI_OBSERVER_URL, TUI_PORT}
import org.slf4j.LoggerFactory
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}
import tui.api.routes.TUIRoutes
import tui.api.service.CoreRequestHttp
import tui.tuiComponent.TUI

object TUIHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: Future[ServerBinding] =
    CoreRequestHttp.registerTUIObserver(TUI_OBSERVER_URL)
    val tui = new TUI
    val serverBinding = Http()
      .newServerAt(TUI_HOST, TUI_PORT)
      .bind(routes(TUIRoutes(tui)))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"TUI Service -- Http Server is running at $TUI_BASE_URL\n")
      case Failure(exception) => logger.error(s"TUI Service -- Http Server failed to start", exception)
    }

    tui.run
    serverBinding

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

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    Await.result(CoreRequestHttp.deregisterTUIObserver(TUI_OBSERVER_URL), 5.seconds)
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("TUI Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }
