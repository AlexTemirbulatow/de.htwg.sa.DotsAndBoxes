package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.FileIORoutes
import common.config.ServiceConfig.{PERSISTENCE_BASE_URL, PERSISTENCE_HOST, PERSISTENCE_PORT}
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object PersistenceServer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(PERSISTENCE_HOST, PERSISTENCE_PORT)
      .bind(routes(new FileIORoutes))
    logger.info(s"Persistence Service -- Http Server is running at $PERSISTENCE_BASE_URL\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

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

  private def shutdown(server: Future[ServerBinding]): Unit = server
    .flatMap(_.unbind())
    .onComplete { _ =>
      logger.info("Persistence Service -- Shutting Down Http Server...")
      system.terminate()
    }
