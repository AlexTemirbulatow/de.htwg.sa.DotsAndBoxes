package api.service

import api.client.ComputerClient
import de.github.dotsandboxes.lib.{ComputerDifficulty, Move}
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object ComputerRequestHttp:
  def connect = ComputerClient.getRequest("api/computer/connect")

  def calculateMove(fieldValue: String, difficulty: ComputerDifficulty): Move =
    decode[Move](
      Await.result(ComputerClient.postRequest("api/computer/get/move", Json.obj(
        "field"      -> fieldValue,
        "difficulty" -> difficulty.toString
      )), 10.seconds)
    ) match
      case Right(move) => move
      case Left(error) => throw new RuntimeException(s"Error decoding Move: ${error.getMessage}")

  def shutdown: Future[Unit] = ComputerClient.shutdown
