package model.api.server

import akka.actor.ActorSystem
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

class ModelHttpServerIntegrationSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ModelHttpTestServer")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  var testModelServerBinding: Option[ServerBinding] = None

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(model.api.server.ModelHttpServer.run, 10.seconds))

  override def afterAll(): Unit =
    Await.result(testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(())), 10.seconds)

  "ModelHttpServer" should {
    "start and respond to requests" in {
      val responseFuture = Http().singleRequest(
        HttpRequest(
          uri = MODEL_BASE_URL.concat("api/model/field/preConnect")
        )
      )
      responseFuture.map { response =>
        response.status shouldBe StatusCodes.OK
      }
    }
  }
}
