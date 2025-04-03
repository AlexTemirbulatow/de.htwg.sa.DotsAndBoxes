package model.api.server

import akka.Done
import akka.actor.ActorSystem
import akka.actor.CoordinatedShutdown
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import common.config.ServiceConfig.MODEL_BASE_URL
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}

class ModelHttpServerSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ModelHttpTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  var testModelServerSystem: Option[ActorSystem] = None
  var testModelServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    val (bindingFuture, modelActorSystem) = ModelHttpServer.run
    testModelServerSystem = Some(modelActorSystem)
    testModelServerBinding = Some(Await.result(bindingFuture, 10.seconds))

  override def afterAll(): Unit =
    Await.result(testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(())), 10.seconds)

  "ModelHttpServer" should {
    "start and respond to requests" in {
      val futureResponse: Future[StatusCode] = Http()
        .singleRequest(
          HttpRequest(
            method = HttpMethods.GET,
            uri = MODEL_BASE_URL.concat("api/model/field/preConnect")
          )
        ).map { response => response.status }
      val response: StatusCode = Await.result(futureResponse, 5.seconds)
      response shouldBe StatusCodes.OK
    }
    "handle double binding failure during server startup" in {
      val exception = intercept[Exception] {
        Await.result(ModelHttpServer.run._1, 5.seconds)
      }
      exception.getMessage should include("Bind failed")
    }
    "shutdown the server correctly" in {
      val shutdown = Await.result(
        ModelHttpServer.shutdown(
          Future {
            testModelServerBinding.get
          }
        ), 5.seconds
      )
      shutdown shouldBe true
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(testModelServerSystem.get).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
