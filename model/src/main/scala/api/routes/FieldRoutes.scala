package api.routes

import akka.http.scaladsl.model.StatusCodes.{BadRequest, Conflict, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib.{BoardSize, Move, PlayerSize, PlayerType, SquareCase, Status}
import fieldComponent.fieldImpl.FieldParser
import io.circe.generic.auto._
import io.circe.syntax._
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import scala.util.Try

class FieldRoutes:
  def fieldRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePlaceRequests,
      handleNewFieldRequest,
      handleGameDataRequests,
      handleCheckAllCells,
      handleCellsToCheck,
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
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val updatedField: FieldInterface = parsedField(fieldResult).putRow(x, y, value)
          complete(fieldToJsonString(updatedField))
        }
      } ~
      path("col") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val value: Boolean = (jsonValue \ "value").as[Boolean]
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val updatedField: FieldInterface = parsedField(fieldResult).putCol(x, y, value)
          complete(fieldToJsonString(updatedField))
        }
      }
    }
  }

  private def handleNewFieldRequest: Route = post {
    path("newField") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val boardSize: BoardSize   = Try(BoardSize.valueOf((jsonValue \ "boardSize").as[String])).getOrElse(throw new RuntimeException("Invalid Board Size."))
        val status: Status         = Status.values.find(_.toString == (jsonValue \ "status").as[String]).getOrElse(throw new RuntimeException("Invalid Status."))
        val playerSize: PlayerSize = Try(PlayerSize.valueOf((jsonValue \ "playerSize").as[String])).getOrElse(throw new RuntimeException("Invalid Player Size."))
        val playerType: PlayerType = Try(PlayerType.valueOf((jsonValue \ "playerType").as[String])).getOrElse(throw new RuntimeException("Invalid Player Type."))
        val fieldResult: JsLookupResult = (jsonValue \ "field")
        complete(fieldToJsonString(parsedField(fieldResult).newField(boardSize, status, playerSize, playerType)))
      }
    }
  }

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("asString") {
      entity(as[String]) { json =>
        complete(parsedField(json).toString)
      }
    } ~
    path("allAvailableCoords") {
      entity(as[String]) { json =>
        val field: FieldInterface = parsedField(json)
        val allAvailableCoords: Vector[(Int, Int, Int)] =
          field.getUnoccupiedRowCoord() ++ field.getUnoccupiedColCoord()
        complete(allAvailableCoords.asJson.toString)
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

  private def handleCheckAllCells: Route = post {
    path("checkAllCells") {
      entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val squareCase: SquareCase = Try(SquareCase.valueOf((jsonValue \ "squareCase").as[String])).get
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          complete(parsedField(fieldResult).checkAllCells(squareCase, x, y).asJson.toString)
      }
    }
  }

  private def handleCellsToCheck: Route = post {
    path("cellsToCheck") {
      entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val squareCase: SquareCase = Try(SquareCase.valueOf((jsonValue \ "squareCase").as[String])).get
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          complete(parsedField(fieldResult).cellsToCheck(squareCase, x, y).asJson.toString)
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
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          complete(parsedField(fieldResult).isEdge(Move(vec, x, y, value)).toString)
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
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult)
              .checkSquare(SquareCase.DownCase, x, y)
              .checkSquare(SquareCase.UpCase, x, y)
            complete(fieldToJsonString(updatedField))
          }
        } ~
        path("vertical") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult)
              .checkSquare(SquareCase.RightCase, x, y)
              .checkSquare(SquareCase.LeftCase, x, y)
            complete(fieldToJsonString(updatedField))
          }
        }
      } ~
      pathPrefix("edgeState") {
        path("downCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult).checkSquare(SquareCase.DownCase, x, y)
            complete(fieldToJsonString(updatedField))
          }
        } ~
        path("upCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult).checkSquare(SquareCase.UpCase, x, y)
            complete(fieldToJsonString(updatedField))
          }
        } ~
        path("rightCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult).checkSquare(SquareCase.RightCase, x, y)
            complete(fieldToJsonString(updatedField))
          }
        } ~
        path("leftCase") {
          entity(as[String]) { json =>
            val jsonValue: JsValue = Json.parse(json)
            val x: Int = (jsonValue \ "x").as[Int]
            val y: Int = (jsonValue \ "y").as[Int]
            val fieldResult: JsLookupResult = (jsonValue \ "field")
            val updatedField: FieldInterface = parsedField(fieldResult).checkSquare(SquareCase.LeftCase, x, y)
            complete(fieldToJsonString(updatedField))
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
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val updatedField: FieldInterface = parsedField(fieldResult)
            .addPoints(playerIndex, points)
            .updatePlayer(playerIndex)
          complete(fieldToJsonString(updatedField))
        }
      }
    }
  }

  private def handlePlayerNextRequests: Route = post {
    pathPrefix("player") {
      path("next") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val updatedField: FieldInterface = parsedField(fieldResult).nextPlayer
          complete(fieldToJsonString(updatedField))
        }
      }
    }
  }

  private def parsedField(json: String): FieldInterface =
    val jsonValue: JsValue = Json.parse(json)
    val fieldValue: String = (jsonValue \ "field").as[String]
    FieldParser.fromJson(fieldValue)

  private def parsedField(fieldResult: JsLookupResult): FieldInterface =
    val fieldValue: String = fieldResult.as[String]
    FieldParser.fromJson(fieldValue)

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

private val exceptionHandler = ExceptionHandler {
  case e: NoSuchElementException =>
    complete(NotFound -> e.getMessage)
  case e: IllegalArgumentException =>
    complete(Conflict -> e.getMessage)
  case e: Throwable =>
    complete(InternalServerError -> Option(e.getMessage).getOrElse("Unknown error"))
}
