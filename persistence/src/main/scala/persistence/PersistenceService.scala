import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object PersistenceService:
  @main def startPersistenceServer: Unit =
    persistence.api.server.PersistenceHttpServer.run
    Await.result(Future.never, Duration.Inf)
