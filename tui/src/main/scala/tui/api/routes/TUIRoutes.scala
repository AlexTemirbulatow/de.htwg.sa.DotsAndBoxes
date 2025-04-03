package tui.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.Event
import tui.tuiComponent.TUI

class TUIRoutes(val tui: TUI):
  def tuiRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleEventRequests
    )
  }

  private def handleEventRequests: Route = get {
    pathPrefix("update") {
      parameter("event") {
        case "abort" =>
          tui.update(Event.Abort)
          complete(StatusCodes.OK)
        case "end" =>
          tui.update(Event.End)
          complete(StatusCodes.OK)
        case "move" =>
          tui.update(Event.Move)
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
