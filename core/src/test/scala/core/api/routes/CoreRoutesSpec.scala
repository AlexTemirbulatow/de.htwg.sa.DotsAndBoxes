package core.api.routes

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.config.ServiceConfig._
import core.controllerComponent.controllerImpl.Controller
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.parser.decode
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import persistence.api.routes.FileIORoutes
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class CoreRoutesSpec extends AnyWordSpec with ScalatestRouteTest with BeforeAndAfterAll {
  private val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
  private val controller = new Controller(using field, FileFormat.JSON, ComputerDifficulty.Medium)
  private val routes: Route = new CoreRoutes(controller).coreRoutes

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private var testPersistenceServerBinding: Option[ServerBinding] = None
  private val fileIORoutes: Route = pathPrefix("api") { pathPrefix("persistence") { pathPrefix("fileIO") { new FileIORoutes().fileIORoutes } } }

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))
    testPersistenceServerBinding = Some(Await.result(Http().bindAndHandle(fileIORoutes, PERSISTENCE_HOST, PERSISTENCE_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testModelServerBinding.map(_.unbind()),
      testPersistenceServerBinding.map(_.unbind())
    ).flatten
    Await.result(Future.sequence(unbindFutures), 10.seconds)
    Await.result(system.terminate(), 10.seconds)

  "CoreRoutes" when {
    "receiving a single slash request" should {
      "return the field state correctly as String" in {
        Get("/") ~> routes ~> check {
          val fieldState: String = responseAs[String]
          fieldState should be(
            "\n" +
            "O-------O-------O-------O-------O\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "O-------O-------O-------O-------O\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "O-------O-------O-------O-------O\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
            "O-------O-------O-------O-------O\n\n" +
            "Blues turn\n" +
            "[points: 0]\n\n" +
            "Your Move <Line><X><Y>: "
          )
        }
      }
    }
    "receiving game data request" should {
      "return the correct FieldData" in {
        Get("/get/fieldData") ~> routes ~> check {
          val fieldData = decode[FieldData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe FieldData(BoardSize.Small, PlayerSize.Two, PlayerType.Human, ComputerDifficulty.Medium)
        }
      }
      "return the correct GameBoardData" in {
        Get("/get/gameBoardData") ~> routes ~> check {
          val gameBoardData = decode[GameBoardData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          gameBoardData shouldBe GameBoardData(
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            Vector(Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false)),
            Vector(Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false)),
            Vector(Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"))
          )
        }
      }
      "return the correct PlayerGameData" in {
        Get("/get/playerGameData") ~> routes ~> check {
          val playerGameData = decode[PlayerGameData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          playerGameData shouldBe PlayerGameData(
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            "It's a draw!",
            "Player Blue [points: 0]\n" +
            "Player Red [points: 0]",
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human))
          )
        }
      }
      "return the correct FieldSizeData" in {
        Get("/get/fieldSizeData") ~> routes ~> check {
          val fieldSizeData = decode[FieldSizeData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldSizeData shouldBe FieldSizeData(4, 5)
        }
      }
      "return wether the game has ended or not" in {
        Get("/get/gameEnded") ~> routes ~> check {
          val gameEnded = responseAs[String].toBoolean
          gameEnded shouldBe false
        }
      }
    }
    "publishing a move" should {
      "putting a move properly and return OK" in {
        val moveJson = Json.obj(
          "method" -> "put",
          "vec"    -> 1,
          "x"      -> 0,
          "y"      -> 0,
          "value"  -> true
        ).toString
        Post("/publish", moveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "return Forbidden if the move was not successful" in {
        val moveJson = Json.obj(
          "method" -> "put",
          "vec"    -> 9,
          "x"      -> 0,
          "y"      -> 0,
          "value"  -> true
        ).toString
        Post("/publish", moveJson) ~> routes ~> check {
          status shouldBe StatusCodes.Forbidden
          responseAs[String] should include("\n<Line> index failed the check. Try again: ")
        }
      }
      "undo and return OK" in {
        val undoJson = Json.obj(
          "method" -> "undo"
        ).toString
        Post("/publish", undoJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "redo and return OK" in {
        val redoJson = Json.obj(
          "method" -> "redo"
        ).toString
        Post("/publish", redoJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "save and return OK" in {
        val saveJson = Json.obj("method" -> "save").toString
        Post("/publish", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "load and return OK" in {
        val saveJson = Json.obj("method" -> "save").toString
        Post("/publish", saveJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
        val loadJson = Json.obj("method" -> "load").toString
        Post("/publish", loadJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "return BadRequest when a invalid method is provided" in {
        val invalidMethodJson = Json.obj(
          "method" -> "invalidMethod"
        ).toString
        Post("/publish", invalidMethodJson) ~> routes ~> check {
          status shouldBe StatusCodes.BadRequest
          responseAs[String] shouldBe "Invalid method"
        }
      }
    }
    "receiving a restart request" should {
      "restart the game and return OK" in {
        Get("/restart") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }
    "receiving a init game request" should {
      "init the game correctly and return OK" in {
        val initGameJson = Json.obj(
          "boardSize"          -> BoardSize.Large.toString,
          "playerSize"         -> PlayerSize.Three.toString,
          "playerType"         -> PlayerType.Computer.toString,
          "computerDifficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/initGame", initGameJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "throw an exception on invalid BoardSize and return Conflict" in {
        val initGameJson = Json.obj(
          "boardSize"          -> "InvalidBoardSize",
          "playerSize"         -> PlayerSize.Three.toString,
          "playerType"         -> PlayerType.Computer.toString,
          "computerDifficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/initGame", initGameJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Board Size.")
        }
      }
      "throw an exception on invalid PlayerSize and return Conflict" in {
        val initGameJson = Json.obj(
          "boardSize"          -> BoardSize.Large.toString,
          "playerSize"         -> "InvalidPlayerSize",
          "playerType"         -> PlayerType.Computer.toString,
          "computerDifficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/initGame", initGameJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Player Size.")
        }
      }
      "throw an exception on invalid PlayerType and return Conflict" in {
        val initGameJson = Json.obj(
          "boardSize"  -> BoardSize.Large.toString,
          "playerSize" -> PlayerSize.Three.toString,
          "playerType" -> "InvalidPlayerType",
          "computerDifficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/initGame", initGameJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Player Type.")
        }
      }
      "throw an exception on invalid ComputerDifficulty and return Conflict" in {
        val initGameJson = Json.obj(
          "boardSize"          -> BoardSize.Large.toString,
          "playerSize"         -> PlayerSize.Three.toString,
          "playerType"         -> PlayerType.Computer.toString,
          "computerDifficulty" -> "InvalidComputerDifficulty"
        ).toString
        Post("/initGame", initGameJson) ~> routes ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Computer Difficulty.")
        }
      }
    }
    "receiving a register or deregister observer request" should {
      val observerJson = Json.obj("url" -> "testObserverUrl").toString
      "register an observer and return OK" in {
        Post("/registerObserver", observerJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
      "deregister an observer and return OK" in {
        Post("/deregisterObserver", observerJson) ~> routes ~> check {
          status shouldBe StatusCodes.OK
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
        val initGameJson = Json.obj(
          "boardSize"          -> "InvalidBoardSize",
          "playerSize"         -> PlayerSize.Three.toString,
          "playerType"         -> PlayerType.Computer.toString,
          "computerDifficulty" -> ComputerDifficulty.Easy.toString
        ).toString
        Post("/initGame", initGameJson) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Board Size.")
        }
      }
      "return 500 Internal Server Error for unexpected exceptions" in {
        val invalidRequest = "invalidJson"
        Post("/initGame", invalidRequest) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("Unrecognized token 'invalidJson'")
        }
      }
    }
  }
}
