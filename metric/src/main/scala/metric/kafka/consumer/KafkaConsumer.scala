package metric.kafka.consumer

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import akka.{Done, NotUsed}
import common.config.KafkaConfig.{KAFKA_BOOTSTRAP_SERVER_ADDRESS, KAFKA_METRIC_GROUP_ID, KAFKA_METRIC_MOVE_TOPIC}
import metric.api.module.MetricModule.given_DAOInterface as daoInterface
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import play.api.libs.json.Json
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object KafkaConsumer:
  implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  implicit val ec: ExecutionContextExecutor = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  private val consumerSettings =
    ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(KAFKA_BOOTSTRAP_SERVER_ADDRESS)
      .withGroupId(KAFKA_METRIC_GROUP_ID)

  private val source: Source[String, Consumer.Control] =
    Consumer
      .plainSource(consumerSettings, Subscriptions.topics(KAFKA_METRIC_MOVE_TOPIC))
      .map(_.value)

  private val parseFlow: Flow[String, (Long, String), NotUsed] =
    Flow[String].map { json =>
      val jsonValue = Json.parse(json)
      val timestamp = (jsonValue \ "timestamp").as[Long]
      val playerName = (jsonValue \ "playerName").as[String]
      (timestamp, playerName)
    }

  private val dbInsertFlow: Flow[(Long, String), Unit, NotUsed] =
    Flow[(Long, String)].map { case (timestamp, playerName) =>
      daoInterface.create(timestamp, playerName) match
        case Success(moveID) =>
          logger.info(s"Metric Service -- Move successfully inserted into database [ID: $moveID]")
        case Failure(ex) =>
          logger.error(s"Metric Service -- Failed to insert Move: ${ex.getMessage}")
    }

  private val sink: Sink[Unit, Future[Done]] =
    Sink.ignore

  private val graph: RunnableGraph[Future[Done]] =
    source.via(parseFlow).via(dbInsertFlow).toMat(sink)(Keep.right)

  def run: Future[Done] = graph.run()
