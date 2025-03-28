package api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Conflict, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.FileFormat
import fileIoComponent.{FileIOInterface, jsonImpl, xmlImpl}
import org.slf4j.Logger
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

class FileIORoutes(val logger: Logger):
  def fileIORoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleSaveRequest,
      handleLoadRequest
    )
  }

  private def handleSaveRequest: Route = post {
    path("save") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fileFormat: FileFormat = Try(FileFormat.valueOf((jsonValue \ "fileFormat").as[String]))
          .getOrElse(throw new RuntimeException(s"Invalid File Format."))
        val fileIO: FileIOInterface = fileIOFactory(fileFormat)
        fileIO.save((jsonValue \ "field").as[String]) match
          case Right(filename) =>
            logger.info(s"Persistence Service [FileIO] -- Field successfully saved as $fileFormat to $filename")
            complete(StatusCodes.OK)
          case Left((errMsg, filename)) =>
            logger.error(s"Persistence Service [FileIO] -- Failed to save Field as $fileFormat to $filename: $errMsg")
            complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def handleLoadRequest: Route = post {
    path("load") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fileFormat: FileFormat = Try(FileFormat.valueOf((jsonValue \ "fileFormat").as[String]))
          .getOrElse(throw new RuntimeException(s"Invalid File Format."))
        val fileIO: FileIOInterface = fileIOFactory(fileFormat)
        fileIO.load match
          case Right((fieldValue, filename)) =>
            logger.info(s"Persistence Service [FileIO] -- Field successfully loaded as $fileFormat from $filename")
            complete(fieldValue)
          case Left((errMsg, filename)) =>
            logger.info(s"Persistence Service [FileIO] -- Failed to load field as $fileFormat from $filename: $errMsg")
            complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def fileIOFactory(fileFormat: FileFormat): FileIOInterface = fileFormat match
    case FileFormat.JSON => new jsonImpl.FileIO
    case FileFormat.XML  => new xmlImpl.FileIO

private val exceptionHandler = ExceptionHandler {
  case e: NoSuchElementException =>
    complete(NotFound -> e.getMessage)
  case e: IllegalArgumentException =>
    complete(Conflict -> e.getMessage)
  case e: Throwable =>
    complete(InternalServerError -> Option(e.getMessage).getOrElse("Unknown error"))
}
