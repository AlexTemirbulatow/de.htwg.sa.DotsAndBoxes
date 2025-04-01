package api.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import common.config.ServiceConfig.MODEL_BASE_URL
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import org.scalatest.BeforeAndAfterAll

class ModelHttpServerIntegrationSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ModelHttpTestServer")

  "ModelHttpServer" should {
    "start and respond to requests" in {
      val serverBindingFuture: Future[ServerBinding] = ModelHttpServer.run

      serverBindingFuture.flatMap { binding =>
        val request = Http().singleRequest(HttpRequest(
          uri = MODEL_BASE_URL + "api/model/field/preConnect"
        ))

        request.flatMap { response =>
          Unmarshal(response.entity).to[String].map { responseBody =>
            response.status shouldBe StatusCodes.OK
            responseBody should include("expected response content")
          }
        }.transformWith { result =>
          binding.unbind()
            .flatMap(_ => binding.terminate(hardDeadline = 3.seconds))
            .map(_ => result.get)
        }
      }
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}
