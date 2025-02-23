package de.htwg.se.dotsandboxes.model.computerComponent.computerEasyImpl

import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.{BoardSize, PlayerSize, PlayerType}
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.util.Move

class ComputerEasySpec extends AnyWordSpec {
  "ComputerEasy" when {
    val computerEasy = new ComputerEasy()
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "there is a winning move" should {
      "always return the winning move" in {
        val winningField = field.putRow(0, 0, true).putCol(0, 0, true).putCol(0, 1, true)
        val winningMove = computerEasy.calculateMove(winningField)

        winningMove should be(Some(Move(1, 1, 0, true)))
      }
    }
    "there is no winning move" should {
      "return a random move available move" in {
        val randomMove = computerEasy.calculateMove(field)
        val allCoords = field.getUnoccupiedRowCoord() ++ field.getUnoccupiedColCoord()
        val allMoves = allCoords.map(coord => Some(Move(coord._1, coord._2, coord._3, true)))

        randomMove should be(defined)
        allMoves should contain(randomMove)
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

        computerEasy.calculateMove(finishedField) shouldBe None
      }
    }
  }
}
