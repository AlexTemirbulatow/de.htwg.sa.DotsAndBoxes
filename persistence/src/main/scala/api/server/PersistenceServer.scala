package api.server

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.FileIORoutes
import common.config.ServiceConfig.{PERSISTENCE_BASE_URL, PERSISTENCE_HOST, PERSISTENCE_PORT}
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object PersistenceServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Future[ServerBinding] =
    val serverBinding = Http()
      .newServerAt(PERSISTENCE_HOST, PERSISTENCE_PORT)
      .bind(routes(new FileIORoutes))

    CoordinatedShutdown(system).addJvmShutdownHook(shutdown(serverBinding))

    serverBinding.onComplete {
      case Success(binding)   => logger.info(s"Persistence Service -- Http Server is running at $PERSISTENCE_BASE_URL\n")
      case Failure(exception) => logger.error(s"Persistence Service -- Http Server failed to start", exception)
    }
    serverBinding

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

  private def shutdown(serverBinding: Future[ServerBinding]): Unit =
    serverBinding
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("Persistence Service -- Shutting Down Http Server...")
        system.terminate()
      }
