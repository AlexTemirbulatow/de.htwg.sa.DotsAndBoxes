package core.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCode}
import common.config.ServiceConfig.CORE_BASE_URL
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class ZCoreHttpServerSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("CoreHttpServerTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testCoreServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    testCoreServerBinding = Some(Await.result(CoreHttpServer.run, 10.seconds))

  override def afterAll(): Unit =
    testCoreServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "CoreHttpServer" should {
    "return OK on pre connect request when started" in {
      val futureResponse: Future[StatusCode] = Http()
        .singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = CORE_BASE_URL.concat("api/core/preConnect")
          )
        ).map { response => response.status }
      val response: StatusCode = Await.result(futureResponse, 5.seconds)
      response shouldBe StatusCodes.OK
    }
    "handle double binding failure during server startup" in {
      val exception = intercept[Exception] {
        Await.result(CoreHttpServer.run, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(CoreHttpServer.system).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
