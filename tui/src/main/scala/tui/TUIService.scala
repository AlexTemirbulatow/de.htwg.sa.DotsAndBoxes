import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object TUIService:
  @main def startTuiServer: Unit =
    tui.api.server.TUIHttpServer.run
    Await.result(Future.never, Duration.Inf)
