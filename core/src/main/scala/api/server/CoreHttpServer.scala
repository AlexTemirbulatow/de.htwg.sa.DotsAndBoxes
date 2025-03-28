package api.server

import akka.actor.ActorSystem
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.module.CoreModule.given_ControllerInterface
import api.routes.CoreRoutes
import api.service.{ComputerRequestHttp, ModelRequestHttp, PersistenceRequestHttp}
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object CoreHttpServer:
  private val CORE_HOST = "localhost"
  private val CORE_PORT = 8083

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(CORE_HOST, CORE_PORT)
      .bind(routes(CoreRoutes(given_ControllerInterface)))
    logger.info(s"Core Service -- Http Server is running at http://$CORE_HOST:$CORE_PORT/api/core\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

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

  private def shutdown(server: Future[ServerBinding]): Unit =
    server
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("Core Service -- Shutting Down Http Server...")
        system.terminate()
      }
    ModelRequestHttp.shutdown
    ComputerRequestHttp.shutdown
    PersistenceRequestHttp.shutdown
