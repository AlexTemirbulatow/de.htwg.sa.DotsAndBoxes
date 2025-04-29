package core.api.service

import core.api.client.MetricClient
import de.github.dotsandboxes.lib.GameStats
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object MetricRequestHttp:
  def preConnect = MetricClient.getRequest("api/metric/preConnect")

  def insertMove(timestamp: Long, playerName: String) =
    MetricClient.postRequest("api/metric/insertMove", Json.obj(
      "timestamp"  -> timestamp,
      "playerName" -> playerName
    ))

  def getStats(playerNames: Vector[String]): GameStats =
    decode[GameStats](
      Await.result(MetricClient.postRequest("api/metric/getStats", Json.obj(
        "playerNames" -> playerNames
      )), 5.seconds)
    ) match
      case Right(gameStats) => gameStats
      case Left(error)   => throw new RuntimeException(s"Error decoding GameStats: ${error.getMessage}")    
