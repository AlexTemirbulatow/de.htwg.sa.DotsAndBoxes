package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import computerComponent.ComputerInterface
import play.api.libs.json.{JsValue, Json}

class ComputerRoutes(val computer: ComputerInterface):
  def computerRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleComputerMoveRequest
    )
  }

  private def handleComputerMoveRequest: Route = post {
    pathPrefix("get") {
      path("move") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json) 
          complete("")
        }
      }
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
