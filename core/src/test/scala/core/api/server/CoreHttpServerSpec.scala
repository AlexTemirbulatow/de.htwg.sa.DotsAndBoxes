package core.api.server

import akka.Done
import akka.actor.ActorSystem
import akka.actor.CoordinatedShutdown
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
  private implicit val system: ActorSystem = ActorSystem("CoreHttpTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  var testCoreServerSystem: Option[ActorSystem] = None
  var testCoreServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    val (bindingFuture, coreActorSystem) = CoreHttpServer.run
    testCoreServerSystem = Some(coreActorSystem)
    testCoreServerBinding = Some(Await.result(bindingFuture, 10.seconds))

  override def afterAll(): Unit =
    Await.result(testCoreServerBinding.map(_.unbind()).getOrElse(Future.successful(())), 10.seconds)

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
        Await.result(CoreHttpServer.run._1, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "shutdown the server correctly" in {
      val shutdown = Await.result(
        CoreHttpServer.shutdown(
          Future {
            testCoreServerBinding.get
          }
        ), 5.seconds
      )
      shutdown shouldBe true
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(testCoreServerSystem.get).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
