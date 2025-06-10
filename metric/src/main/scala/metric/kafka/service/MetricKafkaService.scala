package metric.kafka.service

import akka.actor.{ActorSystem, Cancellable}
import common.config.KafkaConfig.KAFKA_METRIC_UPDATES_TOPIC
import de.github.dotsandboxes.lib.{GameStats, PlayerStats, list}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import metric.api.module.MetricModule.given_DAOInterface as daoInterface
import metric.kafka.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.{Json, OFormat}
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

object MetricKafkaService:
  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private implicit val playerStatsFormat: OFormat[PlayerStats] = Json.format[PlayerStats]
  private implicit val gameStatsFormat: OFormat[GameStats] = Json.format[GameStats]

  private def buildMetricJson: String =
    val statsJson = Json.obj(
      "totalDuration" -> daoInterface.getTotalGameDuration,
      "playerStats" -> Json.obj(
        list.map(_.playerId).map { name =>
          name -> Json.obj(
            "avgMoveDuration" -> daoInterface.getAvgMoveDuration(name),
            "minMoveDuration" -> daoInterface.getMinMoveDuration(name),
            "maxMoveDuration" -> daoInterface.getMaxMoveDuration(name),
            "longestMoveStreak" -> daoInterface.getLongestMoveStreak(name),
            "numOfTotalMoves" -> daoInterface.getNumOfTotalMoves(name)
          )
        }*
      )
    )
    val gameStats: GameStats = statsJson.as[GameStats]
    gameStats.asJson.toString

  def start: Cancellable =
    system.scheduler.scheduleAtFixedRate(0.seconds, 10.seconds) { () =>
      val record = new ProducerRecord[String, String](KAFKA_METRIC_UPDATES_TOPIC, "stats", buildMetricJson)
      KafkaProducer.send(record)
    }
