package core.controllerComponent.utils.playerStrategy

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import common.model.fieldService.FieldInterface
import model.fieldComponent.fieldImpl.Field
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}

class PlayerStrategySpec extends AnyWordSpec {
  "PlayerStrategy" should { /*
    "add one point based on one status difference and keep current player" in {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

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
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

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
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

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
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

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
    } */
  }
}
