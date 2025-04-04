package persistence.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.pathPrefix
import common.config.ServiceConfig.PERSISTENCE_BASE_URL
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class PersistenceHttpServerSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("PersistenceHttpServerTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testPersistenceServerSystem: Option[ActorSystem] = None
  private var testPersistenceServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    val (bindingFuture, persistenceActorSystem) = PersistenceHttpServer.run
    testPersistenceServerSystem = Some(persistenceActorSystem)
    testPersistenceServerBinding = Some(Await.result(bindingFuture, 10.seconds))

  override def afterAll(): Unit =
    testPersistenceServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "PersistenceHttpServer" should {
    "return OK on fileIO pre connect request when started" in {
      val futureResponse: Future[StatusCode] = Http()
        .singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = PERSISTENCE_BASE_URL.concat("api/persistence/fileIO/preConnect")
          )
        ).map { response => response.status }
      val response: StatusCode = Await.result(futureResponse, 5.seconds)
      response shouldBe StatusCodes.OK
    }
    "handle double binding failure during server startup" in {
      val exception = intercept[Exception] {
        Await.result(PersistenceHttpServer.run._1, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(testPersistenceServerSystem.get).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
