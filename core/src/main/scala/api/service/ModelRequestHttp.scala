package api.service

import api.client.ModelClient
import common.model.fieldService.FieldInterface
import common.model.fieldService.fieldJson.FieldJsonConverter
import de.github.dotsandboxes.lib.{BoardSize, CellData, Move, Player, PlayerSize, PlayerType, Status}
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

object ModelRequestHttp:
  def putRow(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/put/row", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def putCol(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/put/col", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def newGame(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): String =
    Await.result(ModelClient.postRequest("api/field/newField", Json.obj(
      "boardSize"  -> boardSize.toString,
      "status"     -> status.toString,
      "playerSize" -> playerSize.toString,
      "playerType" -> playerType.toString
    )), 5.seconds)

  def gameData(data: String, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/get/$data", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds)

  def cellData(field: FieldInterface): CellData =
    decode[CellData](
      Await.result(ModelClient.postRequest(s"api/field/get/cellData", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(cellData) => cellData
      case Left(error)     => throw new RuntimeException(s"Error decoding CellData: ${error.getMessage}")

  def playerList(field: FieldInterface): Vector[Player] =
    decode[Vector[Player]](
      Await.result(ModelClient.postRequest("api/field/get/playerList", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(players) => players
      case Left(error)    => throw new RuntimeException(s"Error decoding Vector[Player]: ${error.getMessage}")

  def currentStatus(field: FieldInterface): Vector[Vector[Status]] =
    decode[Vector[Vector[Status]]](
      Await.result(ModelClient.postRequest("api/field/get/currentStatus", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    ) match
      case Right(status) => status
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[Vector[Status]]: ${error.getMessage}")

  def boardSize(field: FieldInterface): BoardSize =
    Try(BoardSize.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/boardSize", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Board Size."))

  def playerSize(field: FieldInterface): PlayerSize =
    Try(PlayerSize.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/playerSize", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Size."))

  def playerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/playerType", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Type."))

  def currentPlayerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/currentPlayerType", Json.obj(
        "field" -> fieldJsonString(field)
      )), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Type."))

  def statusCell(row: Int, col: Int, field: FieldInterface): Status =
    Try(Await.result(ModelClient.postRequest(s"api/field/get/statusCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds))
      .toOption
      .flatMap(response => Status.values.find(_.toString == response))
      .getOrElse(throw new RuntimeException("Invalid Status."))

  def rowCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/field/get/rowCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toBoolean

  def colCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/field/get/colCell/$row/$col", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toBoolean

  def rowSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/rowSize", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def colSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/colSize", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def maxPosX(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/maxPosX", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def maxPosY(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/maxPosY", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def isEdge(move: Move, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest("api/field/get/isEdge", Json.obj(
      "field" -> fieldJsonString(field),
      "value" -> move.value,
      "vec"   -> move.vec,
      "x"     -> move.x,
      "y"     -> move.y
    )), 5.seconds).toBoolean

  def playerIndex(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest("api/field/get/playerIndex", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds).toInt

  def addPlayerPoints(points: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/field/player/add", Json.obj(
      "field"       -> fieldJsonString(field),
      "playerIndex" -> playerIndex(field),
      "points"      -> points
    )), 5.seconds)

  def nextPlayer(field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/field/player/next", Json.obj(
      "field" -> fieldJsonString(field)
    )), 5.seconds)

  def squareCase(squareCase: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/checkSquare/edgeState/$squareCase", Json.obj(
      "field" -> fieldJsonString(field),
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def squareState(squareState: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/checkSquare/midState/$squareState", Json.obj(
      "field" -> fieldJsonString(field),
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def shutdown: Future[Unit] = ModelClient.shutdown

  private def fieldJsonString(field: FieldInterface): String = FieldJsonConverter.toJson(field).toString
