package computer.api.server

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig._
import model.api.routes.FieldRoutes
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class ZZComputerHttpServerSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ComputerHttpServerTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testComputerServerSystem: Option[ActorSystem] = None
  private var testComputerServerBinding: Option[ServerBinding] = None

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  override def beforeAll(): Unit =
    val (bindingFuture, coreActorSystem) = ComputerHttpServer.run
    testComputerServerSystem = Some(coreActorSystem)
    testComputerServerBinding = Some(Await.result(bindingFuture, 10.seconds))
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testComputerServerBinding.map(_.unbind()),
      testModelServerBinding.map(_.unbind())
    ).flatten
    Await.result(Future.sequence(unbindFutures), 10.seconds)
    Await.result(system.terminate(), 10.seconds)

  "ComputerHttpServer" should {
    "return OK on pre connect request when started" in {
      val futureResponse: Future[StatusCode] = Http()
        .singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = COMPUTER_BASE_URL.concat("api/computer/preConnect")
          )
        ).map { response => response.status }
      val response: StatusCode = Await.result(futureResponse, 5.seconds)
      response shouldBe StatusCodes.OK
    }
    "handle double binding failure during server startup" in {
      val exception = intercept[Exception] {
        Await.result(ComputerHttpServer.run._1, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(testComputerServerSystem.get).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
