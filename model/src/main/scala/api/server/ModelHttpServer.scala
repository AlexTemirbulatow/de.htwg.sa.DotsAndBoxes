package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.FieldRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object ModelHttpServer:
  private val MODEL_HOST = "localhost"
  private val MODEL_PORT = 8080

  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(MODEL_HOST, MODEL_PORT)
      .bind(routes(new FieldRoutes))
    logger.info(s"Model Service -- Http Server is running at http://$MODEL_HOST:$MODEL_PORT/api/model\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

  private def routes(fieldRoutes: FieldRoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("model") {
          concat(
            pathPrefix("field") {
              concat(
                fieldRoutes.fieldRoutes
              )
            }
          )
        }
      )
    }

  private def shutdown(server: Future[ServerBinding]): Unit = server
    .flatMap(_.unbind())
    .onComplete { _ =>
      logger.info("Model Service -- Shutting Down Http Server...")
      system.terminate()
    }
