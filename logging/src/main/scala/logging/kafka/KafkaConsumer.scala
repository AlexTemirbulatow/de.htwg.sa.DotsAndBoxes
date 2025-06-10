package logging.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Sink
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContextExecutor
import logging.config.Config.BOOTSTRAP_SERVER_ADDRESS
import logging.config.Config.KAFKA_LOGGING_GROUP_IP
import logging.config.Config.KAFKA_LOGGING_TOPIC

object KafkaConsumer:
  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  private def consumerSettings: ConsumerSettings[String, String] =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(BOOTSTRAP_SERVER_ADDRESS)
      .withGroupId(KAFKA_LOGGING_GROUP_IP)

  def fetchLogs =
    val futureResult = Consumer
      .plainSource(consumerSettings, Subscriptions.topics(KAFKA_LOGGING_TOPIC))
      .map(record => record.value)
      .runForeach(logMessage => println(s"\n\n#########      $logMessage      #########"))
