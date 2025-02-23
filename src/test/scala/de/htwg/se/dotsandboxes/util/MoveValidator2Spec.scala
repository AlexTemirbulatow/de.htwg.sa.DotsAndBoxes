package de.htwg.se.dotsandboxes.util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status

class MoveValidator2Spec extends AnyWordSpec {
  "The MoveValidator2" when {
    "handling the line validation" should {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return properly with a correct line" in {
        val correctLine = Move(1, 0, 0, true)
        val moveValidator2 = MoveValidator2(Some(correctLine), field).validateLine
        moveValidator2 shouldBe MoveValidator2(Some(Move(1, 0, 0, true)), field)
      }
      "return None bc of an invalid line" in {
        val incorrectLine = Move(9, 0, 0, true)
        val moveValidator2 = MoveValidator2(Some(incorrectLine), field).validateLine
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
      "return None bc of a None move" in {
        val moveValidator2 = MoveValidator2(None, field).validateLine
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
    }
    "handling the x coordinate validation" should {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return properly with a correct x coordinate" in {
        val correctXCoord = Move(1, 0, 0, true)
        val moveValidator2 = MoveValidator2(Some(correctXCoord), field).validateXCoord
        moveValidator2 shouldBe MoveValidator2(Some(Move(1, 0, 0, true)), field)
      }
      "return None bc of an invalid x coordinate" in {
        val incorrectXCoord = Move(1, -1, 0, true)
        val moveValidator2 = MoveValidator2(Some(incorrectXCoord), field).validateXCoord
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
      "return None bc of a None move" in {
        val moveValidator2 = MoveValidator2(None, field).validateXCoord
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
    }
    "handling the y coordinate validation" should {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      "return properly with a correct y coordinate" in {
        val correctYCoord = Move(1, 0, 0, true)
        val moveValidator2 = MoveValidator2(Some(correctYCoord), field).validateYCoord
        moveValidator2 shouldBe MoveValidator2(Some(Move(1, 0, 0, true)), field)
      }
      "return None bc of an invalid y coordinate" in {
        val incorrectYCoord = Move(1, 0, -1, true)
        val moveValidator2 = MoveValidator2(Some(incorrectYCoord), field).validateYCoord
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
      "return None bc of a None move" in {
        val moveValidator2 = MoveValidator2(None, field).validateYCoord
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
    }
    "handling the the availability of a move" should {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(0, 0, true).putCol(0, 0, true)
      "return properly with a available move" in {
        val availableMove = Move(1, 1, 1, true)
        val moveValidator2 = MoveValidator2(Some(availableMove), field).validateAvailable
        moveValidator2 shouldBe MoveValidator2(Some(Move(1, 1, 1, true)), field)

        MoveValidator2(Some(Move(2, 1, 1, true)), field).validateAvailable shouldBe
          MoveValidator2(Some(Move(2, 1, 1, true)), field)
      }
      "return None bc of a not available move" in {
        val notAvailableMove = Move(1, 0, 0, true)
        val moveValidator2 = MoveValidator2(Some(notAvailableMove), field).validateAvailable
        moveValidator2 shouldBe MoveValidator2(None, field)

        MoveValidator2(Some(Move(2, 0, 0, true)), field).validateAvailable shouldBe
          MoveValidator2(None, field)
      }
      "return None bc of a None move" in {
        val moveValidator2 = MoveValidator2(None, field).validateAvailable
        moveValidator2 shouldBe MoveValidator2(None, field)
      }
    }
  }
}
