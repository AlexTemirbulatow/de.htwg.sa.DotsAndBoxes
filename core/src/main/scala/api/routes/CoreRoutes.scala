package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import controllerComponent.ControllerInterface
import play.api.libs.json.{JsValue, Json}
import de.github.dotsandboxes.lib.{PlayerType, BoardSize, PlayerSize, Move, ComputerDifficulty, Status}
import scala.util.Try

class CoreRoutes(val controller: ControllerInterface):
  def coreRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleControllerToStringRequest,
      handleGameDataRequests,
      handleInitGameRequest,
      handleRestartRequest,
      handlePublishRequests
    )
  }

  private def handleControllerToStringRequest: Route = get {
    pathEndOrSingleSlash {
      complete(controller.toString)
    }
  }

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("statusCell" / IntNumber / IntNumber) { (row, col) =>
      val status: Status = controller.getStatusCell(row, col)
      complete(status.toString)
    } ~
    path("rowCell" / IntNumber / IntNumber) { (row, col) =>
      val value: Boolean = controller.getRowCell(row, col)
      complete(value.toString)
    } ~
    path("colCell" / IntNumber / IntNumber) { (row, col) =>
      val value: Boolean = controller.getColCell(row, col)
      complete(value.toString)
    } ~
    path("rowSize") {
      complete(controller.rowSize().toString)
    } ~
    path("colSize") {
      complete(controller.colSize().toString)
    } ~
    path("gameEnded") {
      complete(controller.gameEnded.toString)
    } ~
    path("winner") {
      complete(controller.winner)
    } ~
    path("stats") {
      complete(controller.stats)
    }
  }

  private def handleRestartRequest: Route = get {
    path("restart") {
      controller.restart
      complete(OK)
    }
  }

  private def handleInitGameRequest: Route = post {
    path("initGame") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val boardSize: BoardSize   = Try(BoardSize.valueOf((jsonValue \ "boardSize").as[String])).getOrElse(BoardSize.Medium)
        val playerSize: PlayerSize = Try(PlayerSize.valueOf((jsonValue \ "playerSize").as[String])).getOrElse(PlayerSize.Two)
        val playerType: PlayerType = Try(PlayerType.valueOf((jsonValue \ "playerType").as[String])).getOrElse(PlayerType.Human)
        val computerDifficulty: ComputerDifficulty = Try(ComputerDifficulty.valueOf((jsonValue \ "computerDifficulty").as[String])).getOrElse(ComputerDifficulty.Medium)
        controller.initGame(boardSize, playerSize, playerType, computerDifficulty)
        complete(OK)
      }
    }
  }

  private def handlePublishRequests: Route = post {
    path("publish") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val method: String = (jsonValue \ "method").as[String]

        method match {
          case "put" =>
            val vec: Int = (jsonValue \ "vec").as[Int]
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val value: Boolean = (jsonValue \ "value").as[Boolean]
            controller.publish(controller.put, Move(vec, x, y, value))
            complete(OK)
          case "undo" =>
            controller.publish(controller.undo)
            complete(OK)
          case "redo" =>
            controller.publish(controller.redo)
            complete(OK)
          case "save" =>
            controller.publish(controller.save)
            complete(OK)
          case "load" =>
            controller.publish(controller.load)
            complete(OK)
          case "restart" =>
            controller.restart
            complete(OK)
          case _ =>
            complete(BadRequest, "Invalid method")
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
