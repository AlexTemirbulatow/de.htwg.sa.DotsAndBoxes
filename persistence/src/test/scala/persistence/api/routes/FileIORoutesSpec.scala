package persistence.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.config.ServiceConfig.FILEIO_FILENAME
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.parser.FieldParser
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class FileIORoutesSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  private val routes: Route = new FileIORoutes().fileIORoutes

  private val fieldJsonString: String = FieldConverter.toJson(field).toString
  private val fieldXmlString: String = FieldConverter.toXml(field).toString

  private def fieldFromJsonString(fieldValue: String): FieldInterface =
    FieldParser.fromJson(fieldValue)

  private def fieldFromXmlString(fieldValue: String): FieldInterface =
    FieldParser.fromXml(fieldValue)

  override def afterAll(): Unit =
    Await.result(system.terminate(), 10.seconds)

  "FileIORoutes" when {
    "receiving a pre connect request" should {
      "return with StatusCode OK" in {
        Get("/preConnect") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }
    "receiving a save request" should {
      "properly save the field as JSON" in {
        val saveJson = Json.obj(
          "field"      -> fieldJsonString,
          "fileFormat" -> FileFormat.JSON.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "properly save the field as XML" in {
        val saveJson = Json.obj(
          "field"      -> fieldXmlString,
          "fileFormat" -> FileFormat.XML.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "throw an exception on invalid File Format and return Conflict" in {
        val saveJson = Json.obj(
          "field"      -> fieldJsonString,
          "fileFormat" -> "invalidFileFormat",
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid File Format.")
        }
      }
      "return InternalServerError on invalid field" in {
        val saveJson = Json.obj(
          "field"      -> "invalidField",
          "fileFormat" -> FileFormat.JSON.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("There was an internal server error.")
        }
      }
    }
    "receiving a load request" should {
      "properly load the field as JSON" in {
        val saveJson = Json.obj(
          "field"      -> fieldJsonString,
          "fileFormat" -> FileFormat.JSON.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }

        val loadJson = Json.obj(
          "fileFormat" -> FileFormat.JSON.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          val field: FieldInterface = fieldFromJsonString(responseAs[String])
          field shouldBe a[FieldInterface]
          field.boardSize shouldBe BoardSize.Medium
          field.playerSize shouldBe PlayerSize.Two
          field.playerType shouldBe PlayerType.Human
        }
      }
      "properly load the field as XML" in {
        val saveJson = Json.obj(
          "field"      -> fieldXmlString,
          "fileFormat" -> FileFormat.XML.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/save", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }

        val loadJson = Json.obj(
          "fileFormat" -> FileFormat.XML.toString,
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          val field: FieldInterface = fieldFromXmlString(responseAs[String])
          field shouldBe a[FieldInterface]
          field.boardSize shouldBe BoardSize.Medium
          field.playerSize shouldBe PlayerSize.Two
          field.playerType shouldBe PlayerType.Human
        }
      }
      "throw an exception on invalid File Format an return Conflict" in {
        val loadJson = Json.obj(
          "fileFormat" -> "InvalidFileFormat",
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid File Format.")
        }
      }
      "return InternalServerError on invalid filename" in {
        val loadJson = Json.obj(
          "fileFormat" -> FileFormat.JSON.toString,
          "filename"   -> "InvalidFilename"
        ).toString
        Post("/load", loadJson) ~> routes ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("There was an internal server error.")
        }
      }
    }
    "handle request exception, the exceptionHandler" should {
      "return 404 Not Found for NoSuchElementException on GET request" in {
        Post("/notExistent", "") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.NotFound
          responseAs[String] shouldBe "The requested resource could not be found."
        }
      }
      "return 409 Conflict for IllegalArgumentException" in {
        val loadJson = Json.obj(
          "fileFormat" -> "InvalidFileFormat",
          "filename"   -> FILEIO_FILENAME
        ).toString
        Post("/load", loadJson) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid File Format.")
        }
      }
      "return 500 Internal Server Error for unexpected exceptions" in {
        val invalidRequest = "invalidJson"
        Post("/save", invalidRequest) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("Unrecognized token 'invalidJson'")
        }
      }
    }
  }
}
