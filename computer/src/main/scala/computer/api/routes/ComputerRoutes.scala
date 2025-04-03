package computer.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import computer.api.service.ModelRequestHttp
import computer.computerComponent.ComputerInterface
import computer.computerComponent.computerEasyImpl.ComputerEasy
import computer.computerComponent.computerHardImpl.ComputerHard
import computer.computerComponent.computerMediumImpl.ComputerMedium
import de.github.dotsandboxes.lib.ComputerDifficulty
import io.circe.generic.auto._
import io.circe.syntax._
import play.api.libs.json.{JsValue, Json}
import scala.util.Try

class ComputerRoutes:
  def computerRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleComputerMoveRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      ModelRequestHttp.preConnect
      complete(StatusCodes.OK)
    }
  }

  private def handleComputerMoveRequest: Route = post {
    pathPrefix("get") {
      path("move") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val fieldValue: String = (jsonValue \ "field").as[String]
          val difficulty: ComputerDifficulty = Try(ComputerDifficulty.valueOf((jsonValue \ "difficulty").as[String]))
            .getOrElse(throw new IllegalArgumentException("Invalid Computer Difficulty."))
          val computer: ComputerInterface = computerFactory(difficulty)
          complete(computer.calculateMove(fieldValue).get.asJson.toString)
        }
      }
    }
  }

  private def computerFactory(difficulty: ComputerDifficulty): ComputerInterface = difficulty match
    case ComputerDifficulty.Easy   => new ComputerEasy()
    case ComputerDifficulty.Medium => new ComputerMedium()
    case ComputerDifficulty.Hard   => new ComputerHard()

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> Option(e.getMessage).getOrElse("Unknown error"))
  }
