package persistence.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.FileFormat
import org.slf4j.LoggerFactory
import persistence.fileIOComponent.jsonImpl.FileIO
import persistence.fileIOComponent.{FileIOInterface, jsonImpl, xmlImpl}
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

class FileIORoutes:
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def fileIORoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleSaveRequest,
      handleLoadRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handleSaveRequest: Route = post {
    path("save") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fileFormat: FileFormat = Try(FileFormat.valueOf((jsonValue \ "fileFormat").as[String]))
          .getOrElse(throw new IllegalArgumentException(s"Invalid File Format."))
        val filename: String = ((jsonValue \ "filename")).as[String]
        val fileIO: FileIOInterface = fileIOFactory(fileFormat)
        val fieldValue: String = (jsonValue \ "field").as[String]
        fileIO.save(fieldValue, filename) match
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
          .getOrElse(throw new IllegalArgumentException(s"Invalid File Format."))
        val filename: String = ((jsonValue \ "filename")).as[String]
        val fileIO: FileIOInterface = fileIOFactory(fileFormat)
        fileIO.load(filename) match
          case Right((fieldValue, filename)) =>
            logger.info(s"Persistence Service [FileIO] -- Field successfully loaded as $fileFormat from $filename")
            complete(fieldValue)
          case Left((errMsg, filename)) =>
            logger.error(s"Persistence Service [FileIO] -- Failed to load field as $fileFormat from $filename: $errMsg")
            complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def fileIOFactory(fileFormat: FileFormat): FileIOInterface = fileFormat match
    case FileFormat.JSON => new jsonImpl.FileIO
    case FileFormat.XML  => new xmlImpl.FileIO

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
