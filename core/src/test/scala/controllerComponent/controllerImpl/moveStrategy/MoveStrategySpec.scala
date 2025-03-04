package controllerComponent.controllerImpl.moveStrategy

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import lib.{BoardSize, PlayerSize, PlayerType, Status, Move}

class MoveStrategySpec extends AnyWordSpec {
  "MoveStrategy" when {
    "in edge state" should {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return same field bc no square was finished in down case" in {
        val move = Move(1, 0, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return same field bc no square was finished in up case" in {
        val move = Move(1, field.maxPosX, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return same field bc no square was finished in right case" in {
        val move = Move(2, 0, 0, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return same field bc no square was finished in left case" in {
        val move = Move(2, 0, field.maxPosY, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return field with new status cell in down case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(1, 0, true)
          .putCol(0, 0, true)
          .putCol(0, 1, true)

        val move = Move(1, 0, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 0) should be(Status.Empty)
        updatedField.getStatusCell(0, 0) should be(Status.Blue)
      }
      "return field with new status cell in up case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(2, 0, true)
          .putCol(2, 0, true)
          .putCol(2, 1, true)

        val move = Move(1, newField.maxPosX, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(2, 0) should be(Status.Empty)
        updatedField.getStatusCell(2, 0) should be(Status.Blue)
      }
      "return field with new status cell in right case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(0, 0, true)
          .putRow(1, 0, true)
          .putCol(0, 1, true)

        val move = Move(2, 0, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 0) should be(Status.Empty)
        updatedField.getStatusCell(0, 0) should be(Status.Blue)
      }
      "return field with new status cell in left case" in {
        val newField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
          .putRow(0, 3, true)
          .putRow(1, 3, true)
          .putCol(0, 3, true)

        val move = Move(2, 0, newField.maxPosY, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 3) should be(Status.Empty)
        updatedField.getStatusCell(0, 3) should be(Status.Blue)
      }
    }
    "in mid state" should {
      val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return same field bc no square was finished in horizontal state" in {
        val move = Move(1, 1, 1, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return same field bc no square was finished in vertical state" in {
        val move = Move(2, 1, 1, true)
        val moveState: MoveState = if field.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, field)
        field shouldBe updatedField
      }
      "return field with one new status cell in horizontal state" in {
        val newField: FieldInterface = field
          .putRow(2, 1, true)
          .putCol(1, 1, true)
          .putCol(1, 2, true)
          
        val move = Move(1, 1, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        updatedField.getStatusCell(1, 1) should be(Status.Blue)
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

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        newField.getStatusCell(2, 1) should be(Status.Empty)
        updatedField.getStatusCell(1, 1) should be(Status.Blue)
        updatedField.getStatusCell(2, 1) should be(Status.Blue)
      }
      "return field with one new status cell in vertical state" in {
        val newField: FieldInterface = field
          .putRow(1, 0, true)
          .putRow(2, 0, true)
          .putCol(1, 0, true)
          
        val move = Move(2, 1, 1, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState
        moveState shouldBe MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 0) should be(Status.Empty)
        updatedField.getStatusCell(1, 0) should be(Status.Blue)
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

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 0) should be(Status.Empty)
        newField.getStatusCell(1, 1) should be(Status.Empty)
        updatedField.getStatusCell(1, 0) should be(Status.Blue)
        updatedField.getStatusCell(1, 1) should be(Status.Blue)
      }
    }
  }
}
