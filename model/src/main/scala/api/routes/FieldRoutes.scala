package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import fieldComponent.FieldInterface

class FieldRoutes(var field: FieldInterface):
  def fieldRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleGetFieldRequest
    )
  }

  private def handleGetFieldRequest: Route = get {
    pathEndOrSingleSlash {
      complete(field.toJson.toString)
    }
  }

val exceptionHandler = ExceptionHandler {
  case e: NoSuchElementException =>
    complete(NotFound -> e.getMessage)
  case e: IllegalArgumentException =>
    complete(Conflict -> e.getMessage)
  case e: Throwable =>
    complete(InternalServerError -> Option(e.getMessage).getOrElse("Unknown error"))
}
