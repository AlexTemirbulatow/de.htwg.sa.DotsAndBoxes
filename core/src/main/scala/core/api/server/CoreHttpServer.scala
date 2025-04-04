package core.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{COMPUTER_BASE_URL, CORE_HOST, CORE_PORT}
import core.api.client.{ComputerClient, ModelClient, PersistenceClient}
import core.api.module.CoreModule.given_ControllerInterface
import core.api.routes.CoreRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object CoreHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: (Future[ServerBinding], ActorSystem) =
    val serverBinding = Http()
      .newServerAt(CORE_HOST, CORE_PORT)
      .bind(routes(CoreRoutes(given_ControllerInterface)))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding).map(_ => Done)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Core Service -- Http Server is running at $COMPUTER_BASE_URL\n")
      case Failure(exception) => logger.error(s"Core Service -- Http Server failed to start", exception)
    }
    (serverBinding, system)

  private def routes(coreRoutes: CoreRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("core") {
          concat(
            coreRoutes.coreRoutes
          )
        }
      )
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Core Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }
