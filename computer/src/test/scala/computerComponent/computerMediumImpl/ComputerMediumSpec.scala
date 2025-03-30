package computerComponent.computerMediumImpl

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status, Move}

class ComputerMediumSpec extends AnyWordSpec {
  "ComputerMedium" when {/*
    val computerMedium = new ComputerMedium()
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "there is a winning move" should {
      "always return the winning move" in {
        val winningField = field.putRow(0, 0, true).putCol(0, 0, true).putCol(0, 1, true)
        val winningMove = computerMedium.calculateMove(winningField)

        winningMove should be(Some(Move(1, 1, 0, true)))
      }
    }
    "there is no winning move but a save move" should {
      "always take the save move" in {
        val saveField = field
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 3, true)
          .putCol(1, 0, true).putCol(1, 1, true)
          .putCol(2, 0, true).putCol(2, 1, true)
        val saveMove = computerMedium.calculateMove(saveField)

        saveMove should (be(Some(Move(1, 3, 3, true))) or be(Some(Move(2, 2, 4, true))))
      }
    }
    "there is no winning and no save moves left" should {
      "return a random available move" in {
        val unsavedField = field
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 3, true)
          .putCol(1, 0, true).putCol(1, 1, true)
          .putCol(2, 0, true).putCol(2, 1, true)
        val randomMove = computerMedium.calculateMove(unsavedField)

        randomMove should be(defined)

        val allAvailableCoords = unsavedField.getUnoccupiedRowCoord ++ unsavedField.getUnoccupiedColCoord
        val allAvailableMoves = allAvailableCoords.map(coord => Some(Move(coord._1, coord._2, coord._3, true)))

        allAvailableMoves should contain(randomMove)
      }
    }
    "there is no more available moves left" should {
      "return None" in {
        val finishedField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 1, true).putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 3, true).putCol(2, 4, true)

        computerMedium.calculateMove(finishedField) shouldBe None
      }
    }*/
  }
}
