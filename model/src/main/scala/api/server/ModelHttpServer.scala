package api.server

import akka.actor.ActorSystem
import akka.event.slf4j.Logger
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.module.FieldModule.given_FieldInterface
import api.routes.FieldRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object ModelHttpServer:
  private val MODEL_HOST = "localhost"
  private val MODEL_PORT = 8080

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(MODEL_HOST, MODEL_PORT)
      .bind(routes(FieldRoutes(given_FieldInterface)))
    logger.info(s"Model sever is running at http://$MODEL_HOST:$MODEL_PORT/api\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutDown(server)

  private def routes(fieldRoutes: FieldRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("field") {
          concat(
            fieldRoutes.fieldRoutes
          )
        }
      )
    }

  private def shutDown(server: Future[ServerBinding]): Unit = server
    .flatMap(_.unbind())
    .onComplete { _ =>
      logger.info("Shutting down ModelHttpServer...")
      system.terminate()
    }
