import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object LoggingService:
  @main def startLoggingServer: Unit =
    logging.loggingComponent.LogFileReader.startLiveMonitoring
    Await.result(Future.never, Duration.Inf)
