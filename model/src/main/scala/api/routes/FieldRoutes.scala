package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import fieldComponent.FieldInterface
import io.circe.generic.auto._
import io.circe.syntax._
import de.github.dotsandboxes.lib.{Status, Move}
import play.api.libs.json.{Json, JsValue}
import de.github.dotsandboxes.lib.SquareCase

class FieldRoutes(var field: FieldInterface):
  def fieldRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePlaceRequests,
      handleGameDataRequests,
      handleIsEdgeRequest,
      handleCheckSquareRequests,
      handlePlayerPointsRequests,
      handlePlayerNextRequests
    )
  }

  private def handlePlaceRequests: Route = post {
    pathPrefix("place") {
      path("row") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          field = field.putRow(x, y, value)
          complete(field.toJson.toString)
        }
      } ~
      path("col") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          field = field.putCol(x, y, value)
          complete(field.toJson.toString)
        }
      }
    }
  }

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("asString") {
      complete(field.toString)
    } ~
    path("statusCell" / IntNumber / IntNumber) { (row, col) =>
      val status: Status = field.getStatusCell(row, col)
      complete(status.toString)
    } ~
    path("rowCell" / IntNumber / IntNumber) { (row, col) =>
      val value: Boolean = field.getRowCell(row, col)
      complete(value.toString)
    } ~
    path("colCell" / IntNumber / IntNumber) { (row, col) =>
      val value: Boolean = field.getColCell(row, col)
      complete(value.toString)
    } ~
    path("colSize") {
      complete(field.colSize().toString)
    } ~
    path("rowSize") {
      complete(field.rowSize().toString)
    } ~
    path("maxPosX") {
      complete(field.maxPosX.toString)
    } ~
    path("maxPosY") {
      complete(field.maxPosY.toString)
    } ~
    path("boardSize") {
      complete(field.boardSize.toString)
    } ~
    path("playerSize") {
      complete(field.playerSize.toString)
    } ~
    path("playerType") {
      complete(field.playerType.toString)
    } ~
    path("playerList") {
      complete(field.playerList.asJson.noSpaces)
    } ~
    path("playerIndex") {
      complete(field.playerIndex.toString)
    } ~
    path("currentPlayer") {
      complete(field.currentPlayerId)
    } ~
    path("currentPlayerType") {
      complete(field.currentPlayer.playerType.toString)
    } ~
    path("currentPoints") {
      complete(field.currentPoints.toString)
    } ~
    path("currentStatus") {
      complete(field.currentStatus.asJson.noSpaces)
    } ~
    path("gameEnded") {
      complete(field.isFinished.toString)
    } ~
    path("winner") {
      complete(field.winner)
    } ~
    path("stats") {
      complete(field.stats)
    }
  }

  private def handleIsEdgeRequest: Route = post {
    pathPrefix("get") {
      path("isEdge") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val vec: Int = (jsonValue \ "vec").as[Int]
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          complete(field.isEdge(Move(vec, x, y, value)).toString)
        }
      }
    }
  }

  private def handleCheckSquareRequests: Route = post {
    pathPrefix("checkSquare") {
      pathPrefix("midState") {
        path("horizontal") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field
              .checkSquare(SquareCase.DownCase, x, y)
              .checkSquare(SquareCase.UpCase, x, y)
            complete(field.toJson.toString)
          }
        } ~
        path("vertical") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field
              .checkSquare(SquareCase.RightCase, x, y)
              .checkSquare(SquareCase.LeftCase, x, y)
            complete(field.toJson.toString)
          }
        }
      } ~
      pathPrefix("edgeState") {
        path("downCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field.checkSquare(SquareCase.DownCase, x, y)
            complete(field.toJson.toString)
          }
        } ~
        path("upCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field.checkSquare(SquareCase.UpCase, x, y)
            complete(field.toJson.toString)
          }
        } ~
        path("rightCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field.checkSquare(SquareCase.RightCase, x, y)
            complete(field.toJson.toString)
          }
        } ~
        path("leftCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            field = field.checkSquare(SquareCase.LeftCase, x, y)
            complete(field.toJson.toString)
          }
        }
      }
    }
  }

  private def handlePlayerPointsRequests: Route = post {
    pathPrefix("player") {
      path("add") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val playerIndex: Int = (jsonValue \ "playerIndex").as[Int]
          val points: Int = (jsonValue \ "points").as[Int]
          field = field.addPoints(playerIndex, points).updatePlayer(playerIndex)
          complete(field.toJson.toString)
        }
      }
    }
  }

  private def handlePlayerNextRequests: Route = get {
    pathPrefix("player") {
      path("next") {
        field = field.nextPlayer
        complete(field.toJson.toString)
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
