package computer.api.routes

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.parser.decode
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class ComputerRoutesSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
  private val routes: Route = new ComputerRoutes().computerRoutes

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(()))
    )
    Await.result(Future.sequence(unbindFutures), 10.seconds)

  "ComputerRoutes" when {
    "receiving a pre connect request" should {
      "return with StatusCode OK" in {
        Get("/preConnect") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }
    "receiving a get move request" should {
      "return a valid move on Computer Difficulty 'Easy'" in {
        val json = Json.obj(
          "field"      -> fieldToJsonString(field),
          "difficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/get/move", json) ~> routes ~> check {
          val move = decode[Move](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          move shouldBe a[Move]
          move.asInstanceOf[Move].vec should (be(1) or be(2))
          move.asInstanceOf[Move].x should (be >= 0 and be <= 3)
          move.asInstanceOf[Move].x should (be >= 0 and be <= 4)
          move.asInstanceOf[Move].value shouldBe true
        }
      }
      "return a valid move on Computer Difficulty 'Medium'" in {
        val json = Json.obj(
          "field"      -> fieldToJsonString(field),
          "difficulty" -> ComputerDifficulty.Medium.toString
        ).toString
        Post("/get/move", json) ~> routes ~> check {
          val move = decode[Move](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          move shouldBe a[Move]
          move.asInstanceOf[Move].vec should (be(1) or be(2))
          move.asInstanceOf[Move].x should (be >= 0 and be <= 3)
          move.asInstanceOf[Move].x should (be >= 0 and be <= 4)
          move.asInstanceOf[Move].value shouldBe true
        }
      }
      "return a valid move on Computer Difficulty 'Hard'" in {
        val json = Json.obj(
          "field"      -> fieldToJsonString(field),
          "difficulty" -> ComputerDifficulty.Hard.toString
        ).toString
        Post("/get/move", json) ~> routes ~> check {
          val move = decode[Move](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          move shouldBe a[Move]
          move.asInstanceOf[Move].vec should (be(1) or be(2))
          move.asInstanceOf[Move].x should (be >= 0 and be <= 3)
          move.asInstanceOf[Move].x should (be >= 0 and be <= 4)
          move.asInstanceOf[Move].value shouldBe true
        }
      }
      "throw an exception on invalid Computer Difficulty and return Conflict" in {
        val json = Json.obj(
          "field"      -> fieldToJsonString(field),
          "difficulty" -> "InvalidComputerDifficulty"
        ).toString
        Post("/get/move", json) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Computer Difficulty.")
        }
      }
    }
    "handle request exception, the exceptionHandler" should {
      "return 404 Not Found for NoSuchElementException on Get request" in {
        Get("/notExistent") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.NotFound
          responseAs[String] shouldBe "The requested resource could not be found."
        }
      }
      "return 409 Conflict for IllegalArgumentException" in {
        val json = Json.obj(
          "field"      -> fieldToJsonString(field),
          "difficulty" -> "InvalidComputerDifficulty"
        ).toString
        Post("/get/move", json) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Computer Difficulty.")
        }
      }
      "return 500 Internal Server Error for unexpected exceptions" in {
        val invalidRequest = "invalidJson"
        Post("/get/move", invalidRequest) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("Unrecognized token 'invalidJson'")
        }
      }
    }
  }
}
