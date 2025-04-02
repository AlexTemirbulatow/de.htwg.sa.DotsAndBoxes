package core.api.client

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt

class ModelClientSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private var testModelServerBinding: Option[ServerBinding] = None
  private val mockTestRoute: Route = pathPrefix("test-endpoint") {
    get {
      complete(StatusCodes.OK, "getSuccess")
    } ~
    post {
      entity(as[String]) { body =>
        if (body.contains("valid")) complete(StatusCodes.OK, "postSuccess")
        else complete(StatusCodes.BadRequest, "postFailure") 
      }
    }
  }

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(mockTestRoute, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(()))
    )
    Await.result(Future.sequence(unbindFutures), 10.seconds)
    system.terminate()

  "ModelClient" should {
    "successfully send a GET request" in {
      val response = Await.result(ModelClient.getRequest("test-endpoint"), 5.seconds)
      response shouldBe "getSuccess"
    }
    "successfully send a POST request with valid json" in {
      val json = Json.obj("data" -> "valid")
      val response = Await.result(ModelClient.postRequest("test-endpoint", json), 5.seconds)
      response shouldBe "postSuccess"
    }
    "fail on POST request with invalid json" in {
      val json = Json.obj("data" -> "test")
      val exception = intercept[RuntimeException] {
        Await.result(ModelClient.postRequest("test-endpoint", json), 5.seconds)
      }
      exception.getMessage shouldBe s"HTTP ERROR: 400 Bad Request - http://$MODEL_HOST:$MODEL_PORT/test-endpoint - postFailure"
    }
    "shutdown the ModelClient correctly" in {
      val shutdown: Boolean = Await.result(ModelClient.shutdown, 5.seconds)
      shutdown shouldBe true
    }
  }
}
