package api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Conflict, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.observer.ObserverHttp
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, Move, Player, PlayerSize, PlayerType, Status}
import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

class CoreRoutes(val controller: ControllerInterface):
  private val logger = LoggerFactory.getLogger(getClass)

  def coreRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handleControllerToStringRequest,
      handleGameDataRequests,
      handleRestartGameRequest,
      handleInitGameRequest,
      handlePublishRequests,
      handleRegisterObserverRequest,
      handleDeregisterObserverRequest
    )
  }

  private def handleControllerToStringRequest: Route = get {
    pathEndOrSingleSlash {
      complete(controller.toString)
    }
  }

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("cellData") {
      complete(controller.getCellData.asJson.toString)
    } ~
    path("boardSize") {
      complete(controller.boardSize.toString)
    } ~
    path("playerSize") {
      complete(controller.playerSize.toString)
    } ~
    path("playerType") {
      complete(controller.playerType.toString)
    } ~
    path("computerDifficulty") {
      complete(controller.getComputerDifficulty.toString)
    } ~
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
    path("playerList") {
      complete(controller.playerList.asJson.noSpaces)
    } ~
    path("currentPlayer") {
      complete(controller.currentPlayer)
    } ~
    path("currentPoints") {
      complete(controller.currentPoints.toString)
    } ~
    path("winner") {
      complete(controller.winner)
    } ~
    path("stats") {
      complete(controller.stats)
    }
  }

  private def handleRestartGameRequest: Route = get {
    path("restart") {
      controller.restart
      complete(StatusCodes.OK)
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
        complete(StatusCodes.OK)
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
            complete(StatusCodes.OK)
          case "undo" =>
            controller.publish(controller.undo)
            complete(StatusCodes.OK)
          case "redo" =>
            controller.publish(controller.redo)
            complete(StatusCodes.OK)
          case "save" =>
            controller.publish(controller.save)
            complete(StatusCodes.OK)
          case "load" =>
            controller.publish(controller.load)
            complete(StatusCodes.OK)
          case _ =>
            complete(BadRequest, "Invalid method")
        }
      }
    }
  }

  private def handleRegisterObserverRequest: Route = post {
    path("registerObserver") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val observerUrl: String = (jsonValue \ "url").as[String]
        controller.add(new ObserverHttp(observerUrl))
        logger.info(s"Observer registered at: $observerUrl")
        complete(StatusCodes.OK)
      }
    }
  }

  private def handleDeregisterObserverRequest: Route = post {
    path("deregisterObserver") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val observerUrl: String = (jsonValue \ "url").as[String]
        controller.remove(observerUrl)
        logger.info(s"Observer deregistered from: $observerUrl")
        complete(StatusCodes.OK)
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
