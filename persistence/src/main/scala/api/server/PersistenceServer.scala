package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.routes.FileIORoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object PersistenceServer:
  private val PERSISTENCE_HOST = "localhost"
  private val PERSISTENCE_PORT = 8081

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(PERSISTENCE_HOST, PERSISTENCE_PORT)
      .bind(routes(FileIORoutes(logger)))
    logger.info(s"Persistence Service -- Http Server is running at http://$PERSISTENCE_HOST:$PERSISTENCE_PORT/api/persistence\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

  private def routes(fileIORoutes: FileIORoutes): Route =
    pathPrefix("api") {
      pathPrefix("persistence") {
        concat(
          pathPrefix("fileIO") {
            fileIORoutes.fileIORoutes
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
