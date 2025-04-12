import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object CoreService:
  @main def startCoreServer: Unit =
    core.api.server.CoreHttpServer.run
    Await.result(Future.never, Duration.Inf)
