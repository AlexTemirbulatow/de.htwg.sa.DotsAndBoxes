package core.controllerComponent.utils.moveStrategy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.{BoardSize, Move, PlayerSize, PlayerType, Status}
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.parser.FieldParser
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class MoveStrategySpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("MoveStrategyTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private def fieldFromJsonString(fieldValue: String): FieldInterface =
    FieldParser.fromJson(fieldValue)

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    testModelServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "MoveStrategy" when {
    "in edge state" should {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return same field bc no square was finished in down case" in {
        val move = Move(1, 0, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return same field bc no square was finished in up case" in {
        val move = Move(1, field.maxPosX, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return same field bc no square was finished in right case" in {
        val move = Move(2, 0, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return same field bc no square was finished in left case" in {
        val move = Move(2, 0, field.maxPosY, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return field with new status cell in down case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(1, 0, true)
          .putCol(0, 0, true)
          .putCol(0, 1, true)

        val move = Move(1, 0, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 0) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(0, 0) should be(Status.Blue)
      }
      "return field with new status cell in up case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(2, 0, true)
          .putCol(2, 0, true)
          .putCol(2, 1, true)

        val move = Move(1, newField.maxPosX, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(2, 0) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(2, 0) should be(Status.Blue)
      }
      "return field with new status cell in right case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(0, 0, true)
          .putRow(1, 0, true)
          .putCol(0, 1, true)

        val move = Move(2, 0, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 0) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(0, 0) should be(Status.Blue)
      }
      "return field with new status cell in left case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(0, 3, true)
          .putRow(1, 3, true)
          .putCol(0, 3, true)

        val move = Move(2, 0, newField.maxPosY, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 3) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(0, 3) should be(Status.Blue)
      }
    }
    "in mid state" should {
      val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return same field bc no square was finished in horizontal state" in {
        val move = Move(1, 1, 1, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return same field bc no square was finished in vertical state" in {
        val move = Move(2, 1, 1, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe fieldFromJsonString(updatedField)
      }
      "return field with one new status cell in horizontal state" in {
        val newField: FieldInterface = field
          .putRow(2, 1, true)
          .putCol(1, 1, true)
          .putCol(1, 2, true)

        val move = Move(1, 1, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(1, 1) should be(Status.Blue)
      }
      "return field with two new status cell in horizontal state" in {
        val newField: FieldInterface = field
          .putRow(1, 1, true)
          .putRow(3, 1, true)
          .putCol(1, 1, true)
          .putCol(1, 2, true)
          .putCol(2, 1, true)
          .putCol(2, 2, true)

        val move = Move(1, 2, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        newField.getStatusCell(2, 1) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(1, 1) should be(Status.Blue)
        fieldFromJsonString(updatedField).getStatusCell(2, 1) should be(Status.Blue)
      }
      "return field with one new status cell in vertical state" in {
        val newField: FieldInterface = field
          .putRow(1, 0, true)
          .putRow(2, 0, true)
          .putCol(1, 0, true)

        val move = Move(2, 1, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 0) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(1, 0) should be(Status.Blue)
      }
      "return field with two new status cell in vertical state" in {
        val newField: FieldInterface = field
          .putRow(1, 0, true)
          .putRow(1, 1, true)
          .putRow(2, 0, true)
          .putRow(2, 1, true)
          .putCol(1, 0, true)
          .putCol(1, 2, true)

        val move = Move(2, 1, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: String = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 0) should be(Status.Empty)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        fieldFromJsonString(updatedField).getStatusCell(1, 0) should be(Status.Blue)
        fieldFromJsonString(updatedField).getStatusCell(1, 1) should be(Status.Blue)
      }
    }
  }
}
