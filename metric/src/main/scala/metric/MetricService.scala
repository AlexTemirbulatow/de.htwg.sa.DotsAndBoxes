import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object MetricService:
  @main def startMetricsServer: Unit =
    metric.api.server.MetricHttpServer.run
    //metric.kafka.consumer.KafkaConsumer.run
    //metric.kafka.service.MetricKafkaService.start
    Await.result(Future.never, Duration.Inf)
