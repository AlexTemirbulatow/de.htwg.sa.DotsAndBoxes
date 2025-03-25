package api.routes

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.{Move, SquareCase}
import fieldComponent.FieldInterface
import io.circe.generic.auto._
import io.circe.syntax._
import play.api.libs.json.{JsValue, Json}

class FieldRoutes(val field: FieldInterface):
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
    pathPrefix("put") {
      path("row") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          val fieldValue: String = (jsonValue \ "field").as[String]
          val updatedField: FieldInterface = field.fromJson(fieldValue).putRow(x, y, value)
          complete(updatedField.toJson.toString)
        }
      } ~
      path("col") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          val fieldValue: String = (jsonValue \ "field").as[String]
          val updatedField: FieldInterface = field.fromJson(fieldValue).putCol(x, y, value)
          complete(updatedField.toJson.toString)
        }
      }
    }
  }

  private def parsedField(json: String): FieldInterface =
    val jsonValue: JsValue = Json.parse(json)
    val fieldValue: String = (jsonValue \ "field").as[String]
    field.fromJson(fieldValue)

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("asString") {
      entity(as[String]) { json =>
        complete(parsedField(json).toString)
      }
    } ~
    path("cellData") {
      entity(as[String]) { json =>
        complete(parsedField(json).toCellData.asJson.toString)
      }
    } ~
    path("statusCell" / IntNumber / IntNumber) { (row, col) =>
      entity(as[String]) { json =>
        complete(parsedField(json).getStatusCell(row, col).toString)
      }
    } ~
    path("rowCell" / IntNumber / IntNumber) { (row, col) =>
      entity(as[String]) { json =>
        complete(parsedField(json).getRowCell(row, col).toString)
      }
    } ~
    path("colCell" / IntNumber / IntNumber) { (row, col) =>
      entity(as[String]) { json =>
        complete(parsedField(json).getColCell(row, col).toString)
      }
    } ~
    path("colSize") {
      entity(as[String]) { json =>
        complete(parsedField(json).colSize().toString)
      }
    } ~
    path("rowSize") {
      entity(as[String]) { json =>
        complete(parsedField(json).rowSize().toString)
      }
    } ~
    path("maxPosX") {
      entity(as[String]) { json =>
        complete(parsedField(json).maxPosX.toString)
      }
    } ~
    path("maxPosY") {
      entity(as[String]) { json =>
        complete(parsedField(json).maxPosY.toString)
      }
    } ~
    path("boardSize") {
      entity(as[String]) { json =>
        complete(parsedField(json).boardSize.toString)
      }
    } ~
    path("playerSize") {
      entity(as[String]) { json =>
        complete(parsedField(json).playerSize.toString)
      }
    } ~
    path("playerType") {
      entity(as[String]) { json =>
        complete(parsedField(json).playerType.toString)
      }
    } ~
    path("playerList") {
      entity(as[String]) { json =>
        complete(parsedField(json).playerList.asJson.noSpaces)
      }
    } ~
    path("playerIndex") {
      entity(as[String]) { json =>
        complete(parsedField(json).playerIndex.toString)
      }
    } ~
    path("currentPlayer") {
      entity(as[String]) { json =>
        complete(parsedField(json).currentPlayerId)
      }
    } ~
    path("currentPlayerType") {
      entity(as[String]) { json =>
        complete(parsedField(json).currentPlayer.playerType.toString)
      }
    } ~
    path("currentPoints") {
      entity(as[String]) { json =>
        complete(parsedField(json).currentPoints.toString)
      }
    } ~
    path("currentStatus") {
      entity(as[String]) { json =>
        complete(parsedField(json).currentStatus.asJson.noSpaces)
      }
    } ~
    path("gameEnded") {
      entity(as[String]) { json =>
        complete(parsedField(json).isFinished.toString)
      }
    } ~
    path("winner") {
      entity(as[String]) { json =>
        complete(parsedField(json).winner)
      }
    } ~
    path("stats") {
      entity(as[String]) { json =>
        complete(parsedField(json).stats)
      }
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
          val fieldValue: String = (jsonValue \ "field").as[String]
          complete(field.fromJson(fieldValue).isEdge(Move(vec, x, y, value)).toString)
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
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue)
              .checkSquare(SquareCase.DownCase, x, y)
              .checkSquare(SquareCase.UpCase, x, y)
            complete(updatedField.toJson.toString)
          }
        } ~
        path("vertical") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue)
              .checkSquare(SquareCase.RightCase, x, y)
              .checkSquare(SquareCase.LeftCase, x, y)
            complete(updatedField.toJson.toString)
          }
        }
      } ~
      pathPrefix("edgeState") {
        path("downCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue).checkSquare(SquareCase.DownCase, x, y)
            complete(updatedField.toJson.toString)
          }
        } ~
        path("upCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue).checkSquare(SquareCase.UpCase, x, y)
            complete(updatedField.toJson.toString)
          }
        } ~
        path("rightCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue).checkSquare(SquareCase.RightCase, x, y)
            complete(updatedField.toJson.toString)
          }
        } ~
        path("leftCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldValue: String = (jsonValue \ "field").as[String]
            val updatedField: FieldInterface = field.fromJson(fieldValue).checkSquare(SquareCase.LeftCase, x, y)
            complete(updatedField.toJson.toString)
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
          val fieldValue: String = (jsonValue \ "field").as[String]
          val updatedField: FieldInterface = field.fromJson(fieldValue)
            .addPoints(playerIndex, points)
            .updatePlayer(playerIndex)
          complete(updatedField.toJson.toString)
        }
      }
    }
  }

  private def handlePlayerNextRequests: Route = post {
    pathPrefix("player") {
      path("next") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val fieldValue: String = (jsonValue \ "field").as[String]
          val updatedField: FieldInterface = field.fromJson(fieldValue).nextPlayer
          complete(updatedField.toJson.toString)
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
