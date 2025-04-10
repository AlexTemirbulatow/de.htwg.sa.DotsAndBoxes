import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object ModelService:
  @main def startModelServer: Unit =
    model.api.server.ModelHttpServer.run
    Await.result(Future.never, Duration.Inf)
