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

  def cellData(field: FieldInterface): CellData =
    decode[CellData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/cellData", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(cellData) => cellData
      case Left(error)     => throw new RuntimeException(s"Error decoding CellData: ${error.getMessage}")

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

  def playerTurnData(field: FieldInterface): PlayerTurnData =
    decode[PlayerTurnData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/playerTurnData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding PlayerTurnData: ${error.getMessage}")

  def playerResultData(field: FieldInterface): PlayerResultData =
    decode[PlayerResultData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/playerResultData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding PlayerResultData: ${error.getMessage}")

  def fieldSizeData(field: FieldInterface): FieldSizeData =
    decode[FieldSizeData](
      Await.result(ModelClient.postRequest(s"api/model/field/get/fieldSizeData", Json.obj(
        "field" -> fieldJsonString(field),
      )), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding FieldSizeData: ${error.getMessage}")

  def playerList(field: FieldInterface): Vector[Player] =
    decode[Vector[Player]](
      Await.result(ModelClient.postRequest("api/model/field/get/playerList", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(players) => players
      case Left(error)    => throw new RuntimeException(s"Error decoding Vector[Player]: ${error.getMessage}")

  def currentStatus(field: FieldInterface): Vector[Vector[Status]] =
    decode[Vector[Vector[Status]]](
      Await.result(ModelClient.postRequest("api/model/field/get/currentStatus", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(status) => status
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[Vector[Status]]: ${error.getMessage}")

  def boardSize(field: FieldInterface): BoardSize =
    Try(BoardSize.valueOf(
      Await.result(ModelClient.postRequest("api/model/field/get/boardSize", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Board Size."))

  def playerSize(field: FieldInterface): PlayerSize =
    Try(PlayerSize.valueOf(
      Await.result(ModelClient.postRequest("api/model/field/get/playerSize", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Size."))

  def playerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/model/field/get/playerType", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Type."))

  def currentPlayerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/model/field/get/currentPlayerType", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Type."))

  def statusCell(row: Int, col: Int, field: FieldInterface): Status =
    Try(Await.result(ModelClient.postRequest(s"api/model/field/get/statusCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds))
      .toOption
      .flatMap(response => Status.values.find(_.toString == response))
      .getOrElse(throw new RuntimeException("Invalid Status."))

  def rowCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/model/field/get/rowCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toBoolean

  def colCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/model/field/get/colCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toBoolean

  def rowSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/rowSize", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def colSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/colSize", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

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

  def playerIndex(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest("api/model/field/get/playerIndex", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def addPlayerPoints(points: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/model/field/player/add", Json.obj(
      "field"       -> fieldJsonString(field),
      "playerIndex" -> playerIndex(field),
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
