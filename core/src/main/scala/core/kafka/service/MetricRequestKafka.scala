package core.kafka.service

import common.config.KafkaConfig.KAFKA_METRIC_MOVE_TOPIC
import core.kafka.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.Json
import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global

object MetricRequestKafka:
  def insertMove(timestamp: Long, playerName: String) =
    KafkaProducer.send(
      new ProducerRecord[String, String](KAFKA_METRIC_MOVE_TOPIC, "move", Json.obj(
        "timestamp"  -> timestamp,
        "playerName" -> playerName
      ).toString)
    ).map(_.toString)
