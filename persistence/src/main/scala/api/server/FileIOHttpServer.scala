package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import api.module.FileIOModule.given_FileIOInterface
import api.routes.FileIORoutes
import org.slf4j.LoggerFactory
import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object FileIOHttpServer:
  private val FILEIO_HOST = "localhost"
  private val FILEIO_PORT = 8081

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)

  def run: Unit =
    val server = Http()
      .newServerAt(FILEIO_HOST, FILEIO_PORT)
      .bind(routes(FileIORoutes(given_FileIOInterface)))
    logger.info(s"FileIO Service -- is running at http://$FILEIO_HOST:$FILEIO_PORT/api/fileIO\n\nPress RETURN to terminate...\n")
    StdIn.readLine()
    shutdown(server)

  private def routes(fileIORoutes: FileIORoutes): Route =
    pathPrefix("api") {
      concat(
        pathPrefix("fileIO") {
          concat(
            fileIORoutes.fileIORoutes
          )
        }
      )
    }

  private def shutdown(server: Future[ServerBinding]): Unit = server
    .flatMap(_.unbind())
    .onComplete { _ =>
      logger.info("FileIO Service -- Shutting Down Http Server...")
      system.terminate()
    }
