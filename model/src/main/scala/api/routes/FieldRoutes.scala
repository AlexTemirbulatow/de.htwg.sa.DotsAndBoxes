package api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Conflict, InternalServerError, NotFound}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import fieldComponent.parser.FieldParser
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import scala.util.Try

class FieldRoutes:
  def fieldRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleNewFieldRequest,
      handlePlaceRequests,
      handlePlayerPointsRequests,
      handlePlayerNextRequests,
      handleGameDataRequests,
      handleIsEdgeRequest,
      handleGetWinningMovesRequest,
      handleGetSaveMovesRequest,
      handleGetMissingMovesRequest,
      handleIsCircularSequenceRequest,
      handleChainsWithPointsOutcomeRequest,
      handleCheckSquareRequests
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
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

  private def handlePlayerPointsRequests: Route = post {
    pathPrefix("player") {
      path("add") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val points: Int = (jsonValue \ "points").as[Int]
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val field: FieldInterface = parsedField(fieldResult)
          val playerIndex: Int = field.currentPlayerIndex
          val updatedField: FieldInterface = field
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

  private def handleGameDataRequests: Route = pathPrefix("get") {
    path("asString") {
      entity(as[String]) { json =>
        complete(parsedField(json).toString)
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
    path("allAvailableCoords") {
      entity(as[String]) { json =>
        val field: FieldInterface = parsedField(json)
        val allAvailableCoords: Vector[(Int, Int, Int)] =
          field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords
        complete(allAvailableCoords.asJson.toString)
      }
    } ~
    path("fieldData") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val computerDifficulty: ComputerDifficulty =
          Try(ComputerDifficulty.valueOf((jsonValue \ "computerDifficulty").as[String])).getOrElse(throw new RuntimeException("Invalid Computer Difficulty"))
        complete(parsedField(json).fieldData(computerDifficulty).asJson.toString)
      }
    } ~
    path("gameBoardData") {
      entity(as[String]) { json =>
        complete(parsedField(json).gameBoardData.asJson.toString)
      }
    } ~
    path("playerGameData") {
      entity(as[String]) { json =>
        complete(parsedField(json).playerGameData.asJson.toString)
      }
    } ~
    path("fieldSizeData") {
      entity(as[String]) { json =>
        complete(parsedField(json).fieldSizeData.asJson.toString)
      }
    } ~
    path("currentPlayer") {
      entity(as[String]) { json =>
        complete(parsedField(json).currentPlayer.asJson.toString)
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
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          complete(parsedField(fieldResult).isEdge(Move(vec, x, y, value)).toString)
        }
      }
    }
  }

  private def handleGetWinningMovesRequest: Route = post {
    pathPrefix("get") {
      path("winningMoves") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val coords: Vector[(Int, Int, Int)] =
            decode[Vector[(Int, Int, Int)]]((jsonValue \ "coords").as[String]) match
              case Right(coords) => coords
              case Left(error)   => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val field: FieldInterface = parsedField(fieldResult)
          complete(field.getWinningMoves(coords, field).asJson.toString)
        }
      }
    }
  }

  private def handleGetSaveMovesRequest: Route = post {
    pathPrefix("get") {
      path("saveMoves") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val coords: Vector[(Int, Int, Int)] =
            decode[Vector[(Int, Int, Int)]]((jsonValue \ "coords").as[String]) match
              case Right(coords) => coords
              case Left(error)   => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val field: FieldInterface = parsedField(fieldResult)
          complete(field.getSaveMoves(coords, field).asJson.toString)
        }
      }
    }
  }

  private def handleGetMissingMovesRequest: Route = post {
    pathPrefix("get") {
      path("missingMoves") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val vec: Int = (jsonValue \ "vec").as[Int]
          val x: Int = (jsonValue \ "x").as[Int]
          val y: Int = (jsonValue \ "y").as[Int]
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val field: FieldInterface = parsedField(fieldResult)
          complete(field.getMissingMoves(vec, x, y, field).asJson.toString)
        }
      }
    }
  }

  private def handleIsCircularSequenceRequest: Route = post {
    pathPrefix("get") {
      path("isCircularSequence") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val moveSeq1: (Int, Vector[(Int, Int, Int)]) =
            decode[(Int, Vector[(Int, Int, Int)])]((jsonValue \ "moveSeq1").as[String]) match
              case Right(moveSeq) => moveSeq
              case Left(error)    => throw new RuntimeException(s"Error decoding (Int, Vector[(Int, Int, Int)]): ${error.getMessage}")
          val moveSeq2: (Int, Vector[(Int, Int, Int)]) =
            decode[(Int, Vector[(Int, Int, Int)])]((jsonValue \ "moveSeq2").as[String]) match
              case Right(moveSeq) => moveSeq
              case Left(error)    => throw new RuntimeException(s"Error decoding (Int, Vector[(Int, Int, Int)]): ${error.getMessage}")
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          complete(parsedField(fieldResult).isCircularSequence(moveSeq1, moveSeq2).asJson.toString)
        }
      }
    }
  }

  private def handleChainsWithPointsOutcomeRequest: Route = post {
    pathPrefix("get") {
      path("chainsWithPointsOutcome") {
        entity(as[String]) { json =>
          val jsonValue: JsValue = Json.parse(json)
          val coords: Vector[(Int, Int, Int)] =
            decode[Vector[(Int, Int, Int)]]((jsonValue \ "coords").as[String]) match
              case Right(coords) => coords
              case Left(error)   => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")
          val fieldResult: JsLookupResult = (jsonValue \ "field")
          val field: FieldInterface = parsedField(fieldResult)
          complete(field.chainsWithPointsOutcome(coords, field).asJson.toString)
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
