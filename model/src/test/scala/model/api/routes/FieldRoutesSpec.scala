package model.api.routes

import akka.http.scaladsl.model.HttpEntity.Strict
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import io.circe.Error
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.parser.FieldParser
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class FieldRoutesSpec extends AnyWordSpec with ScalatestRouteTest {
  val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
  val fieldRoutes: FieldRoutes = new FieldRoutes
  val routes: Route = fieldRoutes.fieldRoutes

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

  private def fieldFromJsonString(fieldValue: String): FieldInterface =
    FieldParser.fromJson(fieldValue)

  "FieldRoutes" when {
    "pre connecting" should {
      "return Status code OK" in {
        Get("/preConnect") ~> routes ~> check {
          status shouldBe StatusCodes.OK
        }
      }
    }
    "getting a new field request" should {
      "return a correct field" in {
        val newFieldJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> BoardSize.Small.toString,
          "status"     -> Status.Empty.toString,
          "playerSize" -> PlayerSize.Four.toString,
          "playerType" -> PlayerType.Computer.toString
        ).toString

        Post("/newField", newFieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          newField.boardSize shouldBe BoardSize.Small
          newField.playerSize shouldBe PlayerSize.Four
          newField.playerType shouldBe PlayerType.Computer
        }
      }
      "throw exception on wrong values" in {
        val invalidBoardSizeJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> "invalidBoardSize",
          "status"     -> Status.Empty.toString,
          "playerSize" -> PlayerSize.Four.toString,
          "playerType" -> PlayerType.Computer.toString
        ).toString
        Post("/newField", invalidBoardSizeJson) ~> routes ~> check {
          responseAs[String] shouldBe "Invalid Board Size."
        }

        val invalidStatusJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> BoardSize.Small.toString,
          "status"     -> "InvalidStatus",
          "playerSize" -> PlayerSize.Four.toString,
          "playerType" -> PlayerType.Computer.toString
        ).toString
        Post("/newField", invalidStatusJson) ~> routes ~> check {
          responseAs[String] shouldBe "Invalid Status."
        }

        val invalidPlayerSizeJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> BoardSize.Small.toString,
          "status"     -> Status.Empty.toString,
          "playerSize" -> "InvalidPlayerSize",
          "playerType" -> PlayerType.Computer.toString
        ).toString
        Post("/newField", invalidPlayerSizeJson) ~> routes ~> check {
          responseAs[String] shouldBe "Invalid Player Size."
        }

        val invalidPlayerTypeJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> BoardSize.Small.toString,
          "status"     -> Status.Empty.toString,
          "playerSize" -> PlayerSize.Four.toString,
          "playerType" -> "Invalid Player Type"
        ).toString
        Post("/newField", invalidPlayerTypeJson) ~> routes ~> check {
          responseAs[String] shouldBe "Invalid Player Type."
        }
      }
    }
    "getting a place move request" should {
      "put a row and return the updated field" in {
        val putRowJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "x"     -> 0,
          "y"     -> 0,
          "value" -> true
        ).toString
        Post("/put/row", putRowJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getRowCell(0, 0) shouldBe false
          newField.getRowCell(0, 0) shouldBe true
        }
      }
      "put a col and return the updated field" in {
        val putColJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "x"     -> 0,
          "y"     -> 0,
          "value" -> true
        ).toString
        Post("/put/col", putColJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getColCell(0, 0) shouldBe false
          newField.getColCell(0, 0) shouldBe true
        }
      }
    }
    "getting a player request" should {
      "add points and return the updated field" in {
        val addPointsJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "points" -> 2
        ).toString
        Post("/player/add", addPointsJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
          newField.currentPlayer shouldBe Player("Blue", 2, Status.Blue, PlayerType.Human)
        }
      }
      "change to the next player and return the updated field" in {
        val nextPlayerJson = Json.obj(
          "field"  -> fieldToJsonString(field)
        ).toString
        Post("/player/next", nextPlayerJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
          newField.currentPlayer shouldBe Player("Red", 0, Status.Red, PlayerType.Human)
        }
      }
    }
    "handling game data request" should {
      "return the field as String" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/asString", fieldJson) ~> routes ~> check {
          responseAs[String] shouldBe field.toString
        }
      }
      "return field maxPosX" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/maxPosX", fieldJson) ~> routes ~> check {
          responseAs[String].toInt shouldBe field.maxPosX
        }
      }
      "return field maxPosY" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/maxPosY", fieldJson) ~> routes ~> check {
          responseAs[String].toInt shouldBe field.maxPosY
        }
      }
      "return all available coords" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/allAvailableCoords", fieldJson) ~> routes ~> check {
          val allAvailableCoords = decode[Vector[(Int, Int, Int)]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          allAvailableCoords shouldBe field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords
        }
      }
      "return fieldData" in {
        val fieldJson = Json.obj(
          "field"              -> fieldToJsonString(field),
          "computerDifficulty" -> ComputerDifficulty.Hard.toString
        ).toString
        Post("/get/fieldData", fieldJson) ~> routes ~> check {
          val fieldData = decode[FieldData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe FieldData(BoardSize.Medium, PlayerSize.Two, PlayerType.Human, ComputerDifficulty.Hard)
        }
      }
      "throw exception while getting fieldData" in {
        val invalidFieldJson = Json.obj(
          "field"              -> fieldToJsonString(field),
          "computerDifficulty" -> "InvalidComputerDifficulty"
        ).toString
        Post("/get/fieldData", invalidFieldJson) ~> routes ~> check {
          decode[FieldData](responseAs[String]) should matchPattern {
            case Left(_: Error) =>
          }
        }
      }
      "return gameBoardData" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/gameBoardData", fieldJson) ~> routes ~> check {
          val fieldData = decode[GameBoardData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe GameBoardData(
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            Vector(Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false)),
            Vector(Vector(false, false, false, false, false, false), Vector(false, false, false, false, false, false), Vector(false, false, false, false, false, false), Vector(false, false, false, false, false, false)),
            Vector(Vector("-", "-", "-", "-", "-"), Vector("-", "-", "-", "-", "-"), Vector("-", "-", "-", "-", "-"), Vector("-", "-", "-", "-", "-"))
          )
        }
      }
      "return playerGameData" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/playerGameData", fieldJson) ~> routes ~> check {
          val fieldData = decode[PlayerGameData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe PlayerGameData(
            Player("Blue", 0, Status.Blue, PlayerType.Human),
            "It's a draw!",
            "Player Blue [points: 0]\n" +
            "Player Red [points: 0]",
            Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human))
          )
        }
      }
      "return FieldSizeData" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/fieldSizeData", fieldJson) ~> routes ~> check {
          val fieldData = decode[FieldSizeData](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe FieldSizeData(5, 6)
        }
      }
      "return currentPlayer" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/currentPlayer", fieldJson) ~> routes ~> check {
          val fieldData = decode[Player](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
        }
      }
      "return currentStatus" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/currentStatus", fieldJson) ~> routes ~> check {
          val fieldData = decode[Vector[Vector[Status]]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          fieldData shouldBe Vector(
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty, Status.Empty),
            Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty, Status.Empty)
          )
        }
      }
      "return gameEnded" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field)
        ).toString
        Post("/get/gameEnded", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String].toBoolean shouldBe false
        }
      }
    }
    "handling an is edge request" should {
      "return true if the move is an edge move" in {
        val moveJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "vec"   -> 1,
          "x"     -> 0,
          "y"     -> 0,
          "value" -> true
        ).toString
        Post("/get/isEdge", moveJson) ~> routes ~> check {
          val fieldResponse = responseAs[String].toBoolean shouldBe true
        }
      }
      "return false if the move is not an edge move" in {
        val moveJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "vec"   -> 1,
          "x"     -> 1,
          "y"     -> 1,
          "value" -> true
        ).toString
        Post("/get/isEdge", moveJson) ~> routes ~> check {
          val fieldResponse = responseAs[String].toBoolean shouldBe false
        }
      }
    }
    "handle winning moves request" should {
      "return no winning move" in {
        val fieldJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "coords" -> (field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords).asJson.toString
        ).toString
        Post("/get/winningMoves", fieldJson) ~> routes ~> check {
          val moves = decode[Vector[Move]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          moves shouldBe Vector()
        }
      }
      "return winning moves" in {
        val updatedField = field
          .putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)
          .putRow(2, 0, true).putCol(1, 0, true)

        val fieldJson = Json.obj(
          "field"  -> fieldToJsonString(updatedField),
          "coords" -> (updatedField.getUnoccupiedRowCoords ++ updatedField.getUnoccupiedColCoords).asJson.toString
        ).toString
        Post("/get/winningMoves", fieldJson) ~> routes ~> check {
          val moves = decode[Vector[Move]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          moves shouldBe Vector(Move(2, 0, 1, true), Move(2, 1, 1, true))
        }
      }
      "throw exception if cannot decode" in {
        val invalidFieldJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "coords" -> "invalidCoords"
        ).toString
        Post("/get/winningMoves", invalidFieldJson) ~> routes ~> check {
          decode[Vector[Move]](responseAs[String]) should matchPattern {
            case Left(_: Error) =>
          }
        }
      }
    }
    "handle save moves request" should {
      "return save moves" in {
        val fieldJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "coords" -> (field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords).asJson.toString
        ).toString
        Post("/get/saveMoves", fieldJson) ~> routes ~> check {
          val moves = decode[Vector[Move]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          val allMoves: Vector[Move] = (field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords).map(coord => Move(coord._1, coord._2, coord._3, true))
          moves shouldBe allMoves
        }
      }
      "throw exception if cannot decode" in {
        val invalidFieldJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "coords" -> "invalidCoords"
        ).toString
        Post("/get/saveMoves", invalidFieldJson) ~> routes ~> check {
          decode[Vector[Move]](responseAs[String]) should matchPattern {
            case Left(_: Error) =>
          }
        }
      }
    }
    "handle missing moves request" should {
      "return missing moves" in {
        val updatedField = field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)

        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(updatedField),
          "vec"   -> 1,
          "x"     -> 0,
          "y"     -> 0
        ).toString
        Post("/get/missingMoves", fieldJson) ~> routes ~> check {
          val missingMove = decode[Vector[(Int, Int, Int)]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          missingMove shouldBe Vector((2, 0, 1))
        }
      }
    }
    "handle a is circular sequence" should {
      val circularField = field
        .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(2, 0, true)
        .putRow(2, 1, true).putRow(2, 2, true).putRow(1, 1, true).putCol(0, 0, true)
        .putCol(0, 3, true).putCol(1, 0, true).putCol(1, 3, true).asInstanceOf[Field]
      "return true if it is a circular sequence" in {
        val circularMoves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 2))
        val fieldJson = Json.obj(
          "field"    -> fieldToJsonString(circularField),
          "moveSeq1" -> circularField.evaluateChainWithPointsOutcome(circularMoves.head, circularField).asJson.toString,
          "moveSeq2" -> circularField.evaluateChainWithPointsOutcome(circularMoves.last, circularField).asJson.toString
        ).toString
        Post("/get/isCircularSequence", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String].toBoolean shouldBe true
        }
      }
      "return false if it is not a circular sequence" in {
        val notCircularMoves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 3))
        val fieldJson = Json.obj(
          "field"    -> fieldToJsonString(circularField),
          "moveSeq1" -> circularField.evaluateChainWithPointsOutcome(notCircularMoves.head, circularField).asJson.toString,
          "moveSeq2" -> circularField.evaluateChainWithPointsOutcome(notCircularMoves.last, circularField).asJson.toString
        ).toString
        Post("/get/isCircularSequence", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String].toBoolean shouldBe false
        }
      }
      "throw exception if cannot decode moveSeq1" in {
        val moves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 3))
        val invalidFieldJson = Json.obj(
          "field"    -> fieldToJsonString(circularField),
          "moveSeq1" -> "invalidMoveSeq1",
          "moveSeq2" -> circularField.evaluateChainWithPointsOutcome(moves.last, circularField).asJson.toString
        ).toString
        Post("/get/isCircularSequence", invalidFieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String] should include("Error decoding (Int, Vector[(Int, Int, Int)]):")
        }
      }
      "throw exception if cannot decode moveSeq2" in {
        val moves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 3))
        val invalidFieldJson = Json.obj(
          "field"    -> fieldToJsonString(circularField),
          "moveSeq1" -> circularField.evaluateChainWithPointsOutcome(moves.head, circularField).asJson.toString,
          "moveSeq2" -> "invalidMoveSeq2"
        ).toString
        Post("/get/isCircularSequence", invalidFieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String] should include("Error decoding (Int, Vector[(Int, Int, Int)]):")
        }
      }
    }
    "handle chains with points outcome request" should {
      "return correct chains with points" in {
        val chainedField: Field = field
          .putRow(1, 1, true).putRow(1, 2, true).putRow(2, 2, true).putRow(2, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true).putCol(2, 0, true)
          .putCol(2, 1, true).putCol(2, 2, true).putCol(2, 4, true).asInstanceOf[Field]

        val allAvailableCoords = chainedField.getUnoccupiedRowCoords ++ chainedField.getUnoccupiedColCoords
        val fieldJson = Json.obj(
          "field"  -> fieldToJsonString(chainedField),
          "coords" -> allAvailableCoords.asJson.toString
        ).toString
        Post("/get/chainsWithPointsOutcome", fieldJson) ~> routes ~> check {
          val chains = decode[Vector[(Int, Vector[(Int, Int, Int)])]](responseAs[String]) match
            case Left(value)  => value
            case Right(value) => value
          chains shouldBe chainedField.chainsWithPointsOutcome(allAvailableCoords, chainedField)
        }
      }
      "throw exception if cannot decode" in {
        val invalidFieldJson = Json.obj(
          "field"  -> fieldToJsonString(field),
          "coords" -> "invalidCoords"
        ).toString
        Post("/get/chainsWithPointsOutcome", invalidFieldJson) ~> routes ~> check {
          decode[Vector[(Int, Vector[(Int, Int, Int)])]](responseAs[String]) should matchPattern {
            case Left(_: Error) =>
          }
        }
      }
    }
    "handle check square requests" should {
      "return the updated field on a horizontal midState" in {
        val updatedField = field
          .putRow(0, 0, true).putRow(2, 0, true)
          .putCol(0, 0, true).putCol(0, 1, true)
          .putCol(1, 0, true).putCol(1, 1, true)
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(updatedField),
          "x"     -> 1,
          "y"     -> 0
        ).toString
        Post("/checkSquare/midState/horizontal", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getStatusCell(0, 0) shouldBe Status.Empty
          field.getStatusCell(1, 0) shouldBe Status.Empty
          newField.getStatusCell(0, 0) shouldBe Status.Blue
          newField.getStatusCell(1, 0) shouldBe Status.Blue
        }
      }
      "return the updated field on a vertical midState" in {
        val updatedField = field
          .putRow(0, 0, true).putRow(0, 1, true)
          .putRow(1, 0, true).putRow(1, 1, true)
          .putCol(0, 0, true).putCol(0, 2, true)
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(updatedField),
          "x"     -> 0,
          "y"     -> 1
        ).toString
        Post("/checkSquare/midState/vertical", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getStatusCell(0, 0) shouldBe Status.Empty
          field.getStatusCell(0, 1) shouldBe Status.Empty
          newField.getStatusCell(0, 0) shouldBe Status.Blue
          newField.getStatusCell(0, 1) shouldBe Status.Blue
        }
      }
      "return the updated field on a downCase edgeState" in {
        val updatedField = field.putRow(1, 0, true).putCol(0, 0, true).putCol(0, 1, true)
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(updatedField),
          "x"     -> 0,
          "y"     -> 0
        ).toString
        Post("/checkSquare/edgeState/downCase", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getStatusCell(0, 0) shouldBe Status.Empty
          newField.getStatusCell(0, 0) shouldBe Status.Blue
        }
      }
      "return the updated field on a upCase edgeState" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "x"     -> 4,
          "y"     -> 0
        ).toString
        Post("/checkSquare/edgeState/upCase", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          newField.getStatusCell(3, 0) shouldBe Status.Empty
        }
      }
      "return the updated field on a rightCase edgeState" in {
        val updatedField = field.putRow(0, 0, true).putRow(1, 0, true).putCol(0, 1, true)
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(updatedField),
          "x"     -> 0,
          "y"     -> 0
        ).toString
        Post("/checkSquare/edgeState/rightCase", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          field.getStatusCell(0, 0) shouldBe Status.Empty
          newField.getStatusCell(0, 0) shouldBe Status.Blue
        }
      }
      "return the updated field on a leftCase edgeState" in {
        val fieldJson = Json.obj(
          "field" -> fieldToJsonString(field),
          "x"     -> 0,
          "y"     -> 5
        ).toString
        Post("/checkSquare/edgeState/leftCase", fieldJson) ~> routes ~> check {
          val fieldResponse = responseAs[String]
          fieldResponse should include("field")
          val newField: FieldInterface = fieldFromJsonString(fieldResponse)
          newField.getStatusCell(0, 4) shouldBe Status.Empty
        }
      }
    }
    "handle request exception, the exceptionHandler" should {
      "return 404 Not Found for NoSuchElementException on GET request" in {
        Get("/notExistent") ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.NotFound
          responseAs[String] shouldBe "The requested resource could not be found."
        }
      }
      "return 409 Conflict for IllegalArgumentException" in {
        val invalidBoardSizeJson = Json.obj(
          "field"      -> fieldToJsonString(field),
          "boardSize"  -> "invalidBoardSize",
          "status"     -> Status.Empty.toString,
          "playerSize" -> PlayerSize.Four.toString,
          "playerType" -> PlayerType.Computer.toString
        ).toString
        Post("/newField", invalidBoardSizeJson) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.Conflict
          responseAs[String] should include("Invalid Board Size.")
        }
      }
      "return 500 Internal Server Error for unexpected exceptions" in {
        val invalidRequest = "invalidJson"
        Post("/newField", invalidRequest) ~> Route.seal(routes) ~> check {
          status shouldBe StatusCodes.InternalServerError
          responseAs[String] should include("Unrecognized token 'invalidJson'")
        }
      }
    }
  }
}
