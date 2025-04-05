package persistence.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{PERSISTENCE_BASE_URL, PERSISTENCE_HOST, PERSISTENCE_PORT}
import org.slf4j.LoggerFactory
import persistence.api.routes.FileIORoutes
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PersistenceHttpServer:
  private[server] implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt(PERSISTENCE_HOST, PERSISTENCE_PORT)
      .bind(routes(new FileIORoutes))

    CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-server") { () =>
      shutdown(serverBinding)
    }

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Persistence Service -- Http Server is running at $PERSISTENCE_BASE_URL\n")
      case Failure(exception) => logger.error(s"Persistence Service -- Http Server failed to start", exception)
    }
    return serverBinding

  private def routes(fileIORoutes: FileIORoutes): Route =
    pathPrefix("api") {
      pathPrefix("persistence") {
        concat(
          pathPrefix("fileIO") {
            concat(
              fileIORoutes.fileIORoutes
            )
          }
        )
      }
    }

  private def shutdown(serverBinding: Future[ServerBinding]): Future[Done] =
    serverBinding.flatMap { binding =>
      binding.unbind().map { _ =>
        logger.info("Persistence Service -- Shutting Down Http Server...")
        system.terminate()
        Done
      }
    }
