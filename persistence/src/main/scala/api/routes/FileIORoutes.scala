package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import fileIoComponent.FileIOInterface
import play.api.libs.json.{JsValue, Json}

class FileIORoutes(val fileIO: FileIOInterface):
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
        complete("")
      }
    }
  }

  private def handleLoadRequest: Route = post {
    path("load") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        complete("")
      }
    }
  }

private val exceptionHandler = ExceptionHandler {
  case e: NoSuchElementException =>
    complete(NotFound -> e.getMessage)
  case e: IllegalArgumentException =>
    complete(Conflict -> e.getMessage)
  case e: Throwable =>
    complete(InternalServerError -> Option(e.getMessage).getOrElse("Unknown error"))
}
