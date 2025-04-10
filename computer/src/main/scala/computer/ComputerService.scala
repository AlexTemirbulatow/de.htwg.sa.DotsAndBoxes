import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object ComputerService:
  @main def startComputerServer: Unit =
    computer.api.server.ComputerHttpServer.run
    Await.result(Future.never, Duration.Inf)
