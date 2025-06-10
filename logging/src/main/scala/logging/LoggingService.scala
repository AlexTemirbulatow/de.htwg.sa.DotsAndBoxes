import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object LoggingService:
  @main def startLoggingServer: Unit =
    //logging.loggingComponent.LogFileReader.startLiveMonitoring
    logging.kafka.KafkaConsumer.analyzeLogs
    Await.result(Future.never, Duration.Inf)
