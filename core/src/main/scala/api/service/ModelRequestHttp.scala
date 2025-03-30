package api.service

import api.client.ModelClient
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

object ModelRequestHttp:
  def putRow(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/model/field/put/row", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def putCol(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/model/field/put/col", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def newGame(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType, field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/model/field/newField", Json.obj(
      "field"      -> fieldJsonString(field),
      "boardSize"  -> boardSize.toString,
      "status"     -> status.toString,
      "playerSize" -> playerSize.toString,
      "playerType" -> playerType.toString
    )), 5.seconds)

  def gameData(data: String, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/model/field/get/$data", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds)

  def fieldData(computerDifficulty: ComputerDifficulty, field: FieldInterface): FieldData =
    decode[FieldData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/fieldData", Json.obj(
        "field" -> fieldJsonString(field),
        "computerDifficulty" -> computerDifficulty.toString
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding FieldData: ${error.getMessage}")

  def gameBoardData(field: FieldInterface): GameBoardData =
    decode[GameBoardData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/gameBoardData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding GameBoardData: ${error.getMessage}")

  def playerGameData(field: FieldInterface): PlayerGameData =
    decode[PlayerGameData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/playerGameData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding PlayerGameData: ${error.getMessage}")

  def fieldSizeData(field: FieldInterface): FieldSizeData =
    decode[FieldSizeData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/fieldSizeData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding FieldSizeData: ${error.getMessage}")

  def currentStatus(field: FieldInterface): Vector[Vector[Status]] =
    decode[Vector[Vector[Status]]](
      Await.result(ModelClient.postRequest("api/model/field/get/currentStatus", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(status) => status
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[Vector[Status]]: ${error.getMessage}")

  def maxPosX(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/maxPosX", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def maxPosY(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/maxPosY", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def isEdge(move: Move, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest("api/model/field/get/isEdge", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> move.value,
      "vec"   -> move.vec,
      "x"     -> move.x,
      "y"     -> move.y
    )), 5.seconds).toBoolean

  def currentPlayer(field: FieldInterface): Player =
    decode[Player](
      Await.result(ModelClient.postRequest("api/model/field/get/currentPlayer", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(player) => player
      case Left(error)   => throw new RuntimeException(s"Error decoding Player: ${error.getMessage}")

  def addPlayerPoints(points: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/model/field/player/add", Json.obj(
      "field"       -> fieldJsonString(field),
      "points"      -> points
    )), 5.seconds)

  def nextPlayer(field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/model/field/player/next", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds)

  def squareCase(squareCase: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/model/field/checkSquare/edgeState/$squareCase", Json.obj(
      "field" -> fieldJsonString(field),
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def squareState(squareState: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/model/field/checkSquare/midState/$squareState", Json.obj(
      "field" -> fieldJsonString(field),
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def shutdown: Future[Unit] = ModelClient.shutdown

  private def fieldJsonString(field: FieldInterface): String = FieldConverter.toJson(field).toString
