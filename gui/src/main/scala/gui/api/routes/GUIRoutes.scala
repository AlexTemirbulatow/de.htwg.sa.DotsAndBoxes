package gui.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.Event
import gui.guiComponent.GUI

class GUIRoutes(val gui: GUI):
  def guiRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleEventRequests
    )
  }

  private def handleEventRequests: Route = get {
    pathPrefix("update") {
      parameter("event") {
        case "abort" =>
          gui.update(Event.Abort)
          complete(StatusCodes.OK)
        case "end" =>
          gui.update(Event.End)
          complete(StatusCodes.OK)
        case "move" =>
          gui.update(Event.Move)
          complete(StatusCodes.OK)
        case _ =>
          complete(BadRequest, "Invalid event")
      }
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(NotFound -> e.getMessage)
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
