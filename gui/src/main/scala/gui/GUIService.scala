import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object GUIService:
  @main def startGuiServer: Unit =
    gui.api.server.GUIHttpServer.run
    Await.result(Future.never, Duration.Inf)
