import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object MetricService:
  @main def startMetricsServer: Unit =
    metric.api.server.MetricHttpServer.run
    Await.result(Future.never, Duration.Inf)
