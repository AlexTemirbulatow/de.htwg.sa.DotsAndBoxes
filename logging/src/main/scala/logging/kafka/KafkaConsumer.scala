package logging.kafka

import akka.actor.ActorSystem
import akka.kafka._
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.scaladsl._
import akka.stream.Materializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import io.circe._, io.circe.parser._, io.circe.generic.auto._, io.circe.syntax._
import scala.concurrent.ExecutionContext
import logging.config.Config.{BOOTSTRAP_SERVER_ADDRESS, KAFKA_LOGGING_GROUP_IP, KAFKA_LOGGING_METRIC_TOPIC, KAFKA_LOGGING_TOPIC}
import org.slf4j.LoggerFactory

case class Log(level: String, message: String)
case class LogMetric(level: String, count: Long)

object KafkaConsumer:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher
  private implicit val materializer: Materializer = Materializer(system)

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  private val consumerSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(BOOTSTRAP_SERVER_ADDRESS)
      .withGroupId(KAFKA_LOGGING_GROUP_IP)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  private val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(BOOTSTRAP_SERVER_ADDRESS)

  private val state = scala.collection.mutable.Map[String, Long]().withDefaultValue(0L)

  def analyzeLogs = Consumer
    .plainSource(consumerSettings, Subscriptions.topics(KAFKA_LOGGING_TOPIC))
    .map { msg =>
      decode[Log](msg.value()) match {
        case Right(log) =>
          val updated = state(log.level) + 1
          state.update(log.level, updated)
          Some(LogMetric(log.level, updated))
        case Left(_) => None
      }
    }
    .collect { case Some(metric) =>
      val json = metric.asJson.noSpaces
      logger.info(s"Kafka-Logging-Metric -- level: ${metric.level} -> count: ${metric.count}\n\n")
      new ProducerRecord[String, String](KAFKA_LOGGING_METRIC_TOPIC, metric.level, json)
    }
    .runWith(Producer.plainSink(producerSettings))
