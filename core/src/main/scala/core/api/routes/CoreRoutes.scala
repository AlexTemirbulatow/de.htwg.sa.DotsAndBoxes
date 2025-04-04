package core.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import core.controllerComponent.ControllerInterface
import core.controllerComponent.utils.observer.ObserverHttp
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.syntax._
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json}
import scala.util.{Failure, Success, Try}

class CoreRoutes(val controller: ControllerInterface):
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def coreRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleControllerToStringRequest,
      handleGameDataRequests,
      handlePublishRequests,
      handleRestartGameRequest,
      handleInitGameRequest,
      handleRegisterObserverRequest,
      handleDeregisterObserverRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handleControllerToStringRequest: Route = get {
    pathEndOrSingleSlash {
      complete(controller.toString)
    }
  }

  private def handleGameDataRequests: Route = get {
    pathPrefix("get") {
      path("fieldData") { 
        complete(controller.fieldData.asJson.toString)
      } ~
      path("gameBoardData") { 
        complete(controller.gameBoardData.asJson.toString)
      } ~
      path("playerGameData") { 
        complete(controller.playerGameData.asJson.toString)
      } ~
      path("fieldSizeData") { 
        complete(controller.fieldSizeData.asJson.toString)
      } ~
      path("gameEnded") { 
        complete(controller.gameEnded.toString)
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
            controller.publish(controller.put, Move(vec, x, y, value)) match
              case Failure(exception) => complete((StatusCodes.Forbidden, exception.getMessage))
              case Success(_)         => complete(StatusCodes.OK)
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
        val boardSize: BoardSize   = Try(BoardSize.valueOf((jsonValue \ "boardSize").as[String])).getOrElse(throw new IllegalArgumentException("Invalid Board Size."))
        val playerSize: PlayerSize = Try(PlayerSize.valueOf((jsonValue \ "playerSize").as[String])).getOrElse(throw new IllegalArgumentException("Invalid Player Size."))
        val playerType: PlayerType = Try(PlayerType.valueOf((jsonValue \ "playerType").as[String])).getOrElse(throw new IllegalArgumentException("Invalid Player Type."))
        val computerDifficulty: ComputerDifficulty = Try(ComputerDifficulty.valueOf((jsonValue \ "computerDifficulty").as[String])).getOrElse(throw new IllegalArgumentException("Invalid Computer Difficulty."))
        controller.initGame(boardSize, playerSize, playerType, computerDifficulty)
        complete(StatusCodes.OK)
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
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
