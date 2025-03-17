package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import controllerComponent.controllerImpl.observer.Event

class TUIRoutes(val tui: tuiComponent.TUI):
  
  private def handleEvents: Route = get {
    parameter("event") {
      case "abort" =>
        tui.update(Event.Abort)
        complete(OK)
      case "end" =>
        tui.update(Event.End)
        complete(OK)
      case "move" =>
        tui.update(Event.Move)
        complete(OK)
      case _ =>
        complete(BadRequest, "Invalid event")
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