package model.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{MODEL_BASE_URL, MODEL_HOST, MODEL_PORT}
import model.api.routes.FieldRoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ModelHttpServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: (Future[ServerBinding], ActorSystem) =
    val serverBinding = Http()
      .newServerAt(MODEL_HOST, MODEL_PORT)
      .bind(routes(new FieldRoutes))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding).map(_ => Done)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Model Service -- Http Server is running at $MODEL_BASE_URL\n")
      case Failure(exception) => logger.error(s"Model Service -- Http Server failed to start", exception)
    }
    (serverBinding, system)

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

  private[server] def shutdown(serverBinding: Future[ServerBinding]): Future[Boolean] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Model Service -- Shutting Down Http Server...")
        system.terminate()
        true
      }
    }
