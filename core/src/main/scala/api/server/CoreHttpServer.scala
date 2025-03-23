package api.server

import akka.actor.ActorSystem
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.modules.CoreModule.given_ControllerInterface
import api.routes.CoreRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object CoreHttpServer:
  private val CORE_HOST = "localhost"
  private val CORE_PORT = 8082

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(CORE_HOST, CORE_PORT)
      .bind(routes(CoreRoutes(given_ControllerInterface)))
    logger.info(s"Core sever is running at http://$CORE_HOST:$CORE_PORT/api/\nPress RETURN to terminate...")
    StdIn.readLine()
    shutDown(server)

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

  private def shutDown(server: Future[ServerBinding]): Unit = server
    .flatMap(_.unbind())
    .onComplete { _ =>
      logger.info("Shutting down CoreHttpServer...")
      system.terminate()
    }
