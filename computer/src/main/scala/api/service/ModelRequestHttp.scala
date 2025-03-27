package api.service

import api.client.ModelClient
import de.github.dotsandboxes.lib.SquareCase
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object ModelRequestHttp:
  def allAvailableCoords(fieldValue: String): Vector[(Int, Int, Int)] =
    decode[Vector[(Int, Int, Int)]](
      Await.result(ModelClient.postRequest("api/model/field/get/allAvailableCoords", Json.obj(
        "field" -> fieldValue
      )), 5.seconds)
    ) match
      case Right(coords) => coords
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")

  def maxPosX(fieldValue: String): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/maxPosX", Json.obj(
      "field" -> fieldValue
    )), 5.seconds).toInt

  def maxPosY(fieldValue: String): Int =
    Await.result(ModelClient.postRequest(s"api/model/field/get/maxPosY", Json.obj(
      "field" -> fieldValue
    )), 5.seconds).toInt

  def squareCases(fieldValue: String, vec: Int, x: Int, y: Int): Vector[SquareCase] = vec match
    case 1 =>
      Vector(
        Option.when(x >= 0 && x < maxPosX(fieldValue))(SquareCase.DownCase),
        Option.when(x > 0 && x <= maxPosX(fieldValue))(SquareCase.UpCase)
      ).flatten
    case 2 =>
      Vector(
        Option.when(y >= 0 && y < maxPosY(fieldValue))(SquareCase.RightCase),
        Option.when(y > 0 && y <= maxPosY(fieldValue))(SquareCase.LeftCase)
      ).flatten

  def checkAllCells(fieldValue: String, squareCase: SquareCase, x: Int, y: Int): Vector[Boolean] =
    decode[Vector[Boolean]](
      Await.result(ModelClient.postRequest("api/model/field/checkAllCells", Json.obj(
        "field"      -> fieldValue,
        "squareCase" -> squareCase.toString,
        "x"          -> x,
        "y"          -> y
      )), 5.seconds)
    ) match
      case Right(values) => values
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[Boolean]: ${error.getMessage}")

  def cellsToCheck(fieldValue: String, squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)] =
    decode[Vector[(Int, Int, Int)]](
      Await.result(ModelClient.postRequest("api/model/field/cellsToCheck", Json.obj(
        "field"      -> fieldValue,
        "squareCase" -> squareCase.toString,
        "x"          -> x,
        "y"          -> y
      )), 5.seconds)
    ) match
      case Right(cells) => cells
      case Left(error)  => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")

  def putRow(fieldValue: String, x: Int, y: Int, value: Boolean): String =
    Await.result(ModelClient.postRequest(s"api/model/field/put/row", Json.obj(
      "field" -> fieldValue,
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def putCol(fieldValue: String, x: Int, y: Int, value: Boolean): String =
    Await.result(ModelClient.postRequest(s"api/model/field/put/col", Json.obj(
      "field" -> fieldValue,
      "value" -> value,
      "x"     -> x,
      "y"     -> y
    )), 5.seconds)

  def rowCell(fieldValue: String, row: Int, col: Int): Boolean =
    Await.result(ModelClient.postRequest(s"api/model/field/get/rowCell/$row/$col", Json.obj(
      "field" -> fieldValue
    )), 5.seconds).toBoolean

  def colCell(fieldValue: String, row: Int, col: Int): Boolean =
    Await.result(ModelClient.postRequest(s"api/model/field/get/colCell/$row/$col", Json.obj(
      "field" -> fieldValue
    )), 5.seconds).toBoolean

  def shutdown: Future[Unit] = ModelClient.shutdown
