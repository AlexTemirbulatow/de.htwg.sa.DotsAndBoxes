package api.service

import api.client.ModelClient
import de.github.dotsandboxes.lib.{BoardSize, Move, Player, PlayerSize, PlayerType, Status}
import fieldComponent.FieldInterface
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

object ModelRequestHttp:
  def putRow(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/put/row", Json.obj(
      "x"     -> Json.toJson(x),
      "y"     -> Json.toJson(y),
      "value" -> Json.toJson(value),
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def putCol(x: Int, y: Int, value: Boolean, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/put/col", Json.obj(
      "x"     -> Json.toJson(x),
      "y"     -> Json.toJson(y),
      "value" -> Json.toJson(value),
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def gameData(data: String, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/get/$data", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def playerList(field: FieldInterface): Vector[Player] =
    decode[Vector[Player]](
      Await.result(ModelClient.postRequest("api/field/get/playerList", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    ).getOrElse(Vector.empty)

  def currentStatus(field: FieldInterface): Vector[Vector[Status]] =
    decode[Vector[Vector[Status]]](
      Await.result(ModelClient.postRequest("api/field/get/currentStatus", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    ).getOrElse(Vector.empty)

  def boardSize(field: FieldInterface): BoardSize =
    Try(BoardSize.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/boardSize", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    )).getOrElse(BoardSize.Medium)

  def playerSize(field: FieldInterface): PlayerSize =
    Try(PlayerSize.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/playerSize", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    )).getOrElse(PlayerSize.Two)

  def playerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/playerType", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    )).getOrElse(PlayerType.Human)

  def currentPlayerType(field: FieldInterface): PlayerType =
    Try(PlayerType.valueOf(
      Await.result(ModelClient.postRequest("api/field/get/currentPlayerType", Json.obj(
        "field" -> Json.toJson(fieldJsonString(field))
      )), 5.seconds)
    )).getOrElse(PlayerType.Human)

  def statusCell(row: Int, col: Int, field: FieldInterface): Status =
    Try(Await.result(ModelClient.postRequest(s"api/field/get/statusCell/$row/$col", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds))
      .toOption
      .flatMap(response => Status.values.find(_.toString == response))
      .getOrElse(Status.Empty)

  def rowCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/field/get/rowCell/$row/$col", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toBoolean

  def colCell(row: Int, col: Int, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest(s"api/field/get/colCell/$row/$col", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toBoolean

  def rowSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/rowSize", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toInt

  def colSize(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/colSize", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toInt

  def maxPosX(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/maxPosX", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toInt

  def maxPosY(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest(s"api/field/get/maxPosY", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toInt

  def isEdge(move: Move, field: FieldInterface): Boolean =
    Await.result(ModelClient.postRequest("api/field/get/isEdge", Json.obj(
      "vec"   -> Json.toJson(move.vec),
      "x"     -> Json.toJson(move.x),
      "y"     -> Json.toJson(move.y),
      "value" -> Json.toJson(move.value),
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toBoolean

  def playerIndex(field: FieldInterface): Int =
    Await.result(ModelClient.postRequest("api/field/get/playerIndex", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds).toInt

  def addPlayerPoints(points: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/field/player/add", Json.obj(
      "points"      -> Json.toJson(points),
      "playerIndex" -> Json.toJson(playerIndex(field)),
      "field"       -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def nextPlayer(field: FieldInterface): String =
    Await.result(ModelClient.postRequest("api/field/player/next", Json.obj(
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def squareCase(squareCase: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/checkSquare/edgeState/$squareCase", Json.obj(
      "x"     -> Json.toJson(x),
      "y"     -> Json.toJson(y),
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def squareState(squareState: String, x: Int, y: Int, field: FieldInterface): String =
    Await.result(ModelClient.postRequest(s"api/field/checkSquare/midState/$squareState", Json.obj(
      "x"     -> Json.toJson(x),
      "y"     -> Json.toJson(y),
      "field" -> Json.toJson(fieldJsonString(field))
    )), 5.seconds)

  def shutdown: Future[Unit] = ModelClient.shutdown

  private def fieldJsonString(field: FieldInterface): String = field.toJson.toString
