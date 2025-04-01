package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.ComputerRoutes
import api.service.ModelRequestHttp
import common.config.ServiceConfig.{COMPUTER_BASE_URL, COMPUTER_HOST, COMPUTER_PORT}
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ComputerHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt(COMPUTER_HOST, COMPUTER_PORT)
      .bind(routes(new ComputerRoutes))

    CoordinatedShutdown(system).addJvmShutdownHook(shutdown(serverBinding))

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Computer Service -- Http Server is running at $COMPUTER_BASE_URL\n")
      case Failure(exception) => logger.error(s"Computer Service -- Http Server failed to start", exception)
    }
    serverBinding

  private def routes(computerRoutes: ComputerRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("computer") {
          concat(
            computerRoutes.computerRoutes
          )
        }
      )
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Unit =
    serverBinding
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("Computer Service -- Shutting Down Http Server...")
        system.terminate()
      }
    ModelRequestHttp.shutdown
