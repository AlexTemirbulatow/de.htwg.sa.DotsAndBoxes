package core.api.client

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.config.ServiceConfig.{PERSISTENCE_HOST, PERSISTENCE_PORT}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class YPersistenceClientSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private var testPersistenceServerBinding: Option[ServerBinding] = None
  private val testRoute: Route = pathPrefix("test-endpoint") {
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
    testPersistenceServerBinding = Some(Await.result(Http().bindAndHandle(testRoute, PERSISTENCE_HOST, PERSISTENCE_PORT), 10.seconds))

  override def afterAll(): Unit =
    testPersistenceServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "PersistenceClient" should {
    "successfully send a GET request" in {
      val response = Await.result(PersistenceClient.getRequest("test-endpoint"), 5.seconds)
      response shouldBe "getSuccess"
    }
    "successfully send a POST request with valid json" in {
      val json = Json.obj("data" -> "valid")
      val response = Await.result(PersistenceClient.postRequest("test-endpoint", json), 5.seconds)
      response shouldBe "postSuccess"
    }
    "fail on POST request with invalid json" in {
      val json = Json.obj("data" -> "test")
      val exception = intercept[RuntimeException] {
        Await.result(PersistenceClient.postRequest("test-endpoint", json), 5.seconds)
      }
      exception.getMessage shouldBe s"HTTP ERROR: 400 Bad Request - http://$PERSISTENCE_HOST:$PERSISTENCE_PORT/test-endpoint - postFailure"
    }
    "call CoordinatedShutdown when JVM is shutting down" in {
      val shutdownFuture = CoordinatedShutdown(PersistenceClient.system).run(CoordinatedShutdown.unknownReason)
      val shutdownResult = Await.result(shutdownFuture, 5.seconds)
      shutdownResult shouldBe Done
    }
  }
}
