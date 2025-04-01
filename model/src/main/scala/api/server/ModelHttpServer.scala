package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.FieldRoutes
import common.config.ServiceConfig.{MODEL_BASE_URL, MODEL_HOST, MODEL_PORT}
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ModelHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt(MODEL_HOST, MODEL_PORT)
      .bind(routes(new FieldRoutes))

    CoordinatedShutdown(system).addJvmShutdownHook(shutdown(serverBinding))

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Model Service -- Http Server is running at $MODEL_BASE_URL\n")
      case Failure(exception) => logger.error(s"Model Service -- Http Server failed to start", exception)
    }
    serverBinding

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

  private def shutdown(serverBinding: Future[ServerBinding]): Unit =
    serverBinding
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("Model Service -- Shutting Down Http Server...")
        system.terminate()
      }
