package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.ComputerRoutes
import api.service.ModelRequestHttp
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object ComputerHttpServer:
  private val COMPUTER_HOST = "localhost"
  private val COMPUTER_PORT = 8082

  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(COMPUTER_HOST, COMPUTER_PORT)
      .bind(routes(new ComputerRoutes))
    logger.info(s"Computer Service -- Http Server is running at http://$COMPUTER_HOST:$COMPUTER_PORT/\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

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

  private def shutdown(server: Future[ServerBinding]): Unit =
    server
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("Computer Service -- Shutting Down Http Server...")
        system.terminate()
      }
    ModelRequestHttp.shutdown
