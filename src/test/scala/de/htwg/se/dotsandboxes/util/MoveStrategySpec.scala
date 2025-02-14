package de.htwg.se.dotsandboxes.util

import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.moveState.{EdgeState, MidState, MoveState}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class MoveStrategySpec extends AnyWordSpec {
  "MoveStrategy" when {
    "in edge state" should {
      val field: FieldInterface = new Field(3, 3, Status.Empty, 2)
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
        val newField: FieldInterface = new Field(2, 2, Status.Empty, 2)
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
        val newField: FieldInterface = new Field(2, 2, Status.Empty, 2)
          .putRow(1, 0, true)
          .putCol(1, 0, true)
          .putCol(1, 1, true)

        val move = Move(1, newField.maxPosX, 0, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(1, 0) should be(Status.Empty)
        updatedField.getStatusCell(1, 0) should be(Status.Blue)
      }
      "return field with new status cell in right case" in {
        val newField: FieldInterface = new Field(2, 2, Status.Empty, 2)
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
        val newField: FieldInterface = new Field(2, 2, Status.Empty, 2)
          .putRow(0, 1, true)
          .putRow(1, 1, true)
          .putCol(0, 1, true)

        val move = Move(2, 0, newField.maxPosY, true)
        val moveState: MoveState = if newField.isEdge(move) then EdgeState else MidState

        val updatedField: FieldInterface = MoveStrategy.executeStrategy(moveState, move, newField)
        newField.getStatusCell(0, 1) should be(Status.Empty)
        updatedField.getStatusCell(0, 1) should be(Status.Blue)
      }
    }
    "in mid state" should {
      val field: FieldInterface = new Field(4, 4, Status.Empty, 2)
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
