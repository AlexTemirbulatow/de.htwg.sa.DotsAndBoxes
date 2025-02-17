package de.htwg.se.dotsandboxes.util

import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.playerState.{AddOnePoint, AddTwoPoints, NextPlayer}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field

class PlayerStrategySpec extends AnyWordSpec {
  "PlayerStrategy" should {
    "add one point based on one status difference and keep current player" in {
      val field: FieldInterface = new Field(2, 2, Status.Empty, 2)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Blue),
        Vector(Status.Empty, Status.Empty)
      )

      val updatedField = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      updatedField.getPoints(0) should be(1)
      updatedField.currentPlayerIndex should be(0)
    }
    "add two points based on two status differences and keep current player" in {
      val field: FieldInterface = new Field(2, 2, Status.Empty, 2)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Blue),
        Vector(Status.Blue, Status.Empty)
      )

      val updatedField = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      updatedField.getPoints(0) should be(2)
      updatedField.currentPlayerIndex should be(0)
    }
    "add no points based on no status differences and change current player to next" in {
      val field: FieldInterface = new Field(2, 2, Status.Empty, 2)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )

      val updatedField = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      updatedField.getPoints(0) should be(0)
      updatedField.currentPlayerIndex should be(1)
    }
    "add no points based on too many status differences and change current player to next" in {
      val field: FieldInterface = new Field(2, 2, Status.Empty, 2)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Blue, Status.Blue),
        Vector(Status.Blue, Status.Blue)
      )

      val updatedField = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      updatedField.getPoints(0) should be(0)
      updatedField.currentPlayerIndex should be(1)
    }
  }
}
