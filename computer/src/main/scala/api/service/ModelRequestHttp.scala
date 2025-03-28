package api.service

import api.client.ModelClient
import de.github.dotsandboxes.lib.SquareCase
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
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

  def isClosingMove(fieldValue: String, vec: Int, x: Int, y: Int): Boolean =
    Await.result(ModelClient.postRequest("api/model/field/get/isClosingMove", Json.obj(
        "field" -> fieldValue,
        "vec"   -> vec,
        "x"     -> x,
        "y"     -> y
    )), 5.seconds).toBoolean

  def isRiskyMove(fieldValue: String, vec: Int, x: Int, y: Int): Boolean =
    Await.result(ModelClient.postRequest("api/model/field/get/isRiskyMove", Json.obj(
        "field" -> fieldValue,
        "vec"   -> vec,
        "x"     -> x,
        "y"     -> y
    )), 5.seconds).toBoolean

  def isCircularSequence(fieldValue: String, moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean =
    Await.result(ModelClient.postRequest("api/model/field/get/isCircularSequence", Json.obj(
        "field"    -> fieldValue,
        "moveSeq1" -> moveSeq1.asJson.toString,
        "moveSeq2" -> moveSeq2.asJson.toString,
    )), 5.seconds).toBoolean

  def getMissingMoves(fieldValue: String, vec: Int, x: Int, y: Int): Vector[(Int, Int, Int)] =
    decode[Vector[(Int, Int, Int)]](
      Await.result(ModelClient.postRequest("api/model/field/get/missingMoves", Json.obj(
        "field" -> fieldValue,
        "vec"   -> vec,
        "x"     -> x,
        "y"     -> y
      )), 5.seconds)
    ) match
      case Right(coords) => coords
      case Left(error)   => throw new RuntimeException(s"Error decoding Vector[(Int, Int, Int)]: ${error.getMessage}")

  def evaluateChainWithPointsOutcome(fieldValue: String, moveCoord: (Int, Int, Int)): (Int, Vector[(Int, Int, Int)]) =
    decode[(Int, Vector[(Int, Int, Int)])](
      Await.result(ModelClient.postRequest("api/model/field/get/evaluateChainWithPointsOutcome", Json.obj(
        "field" -> fieldValue,
        "vec"   -> moveCoord._1,
        "x"     -> moveCoord._2,
        "y"     -> moveCoord._3
      )), 5.seconds)
    ) match
      case Right(coords) => coords
      case Left(error)   => throw new RuntimeException(s"Error decoding (Int, Vector[(Int, Int, Int)]): ${error.getMessage}")

  def shutdown: Future[Unit] = ModelClient.shutdown
