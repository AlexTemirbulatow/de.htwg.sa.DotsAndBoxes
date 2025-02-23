package de.htwg.se.dotsandboxes.util

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.util.{Failure, Success}
import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.model.fieldComponent.fieldImpl.Field
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status

class MoveValidatorSpec extends AnyWordSpec {
  "MoveHandler" should {
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "handle empty chain scenarios of handlers" in {
      val emptyLineHandler = LineHandler(None)
      val result1 = emptyLineHandler.handle(Move(1, 0, 0, true), field)
      result1 shouldBe a [Failure[?]]
      result1.failed.get.getMessage shouldBe "could not handle."

      val emptyXCoordHandler = XCoordHandler(None)
      val result2 = emptyXCoordHandler.handle(Move(1, 0, 0, true), field)
      result2 shouldBe a [Failure[?]]
      result2.failed.get.getMessage shouldBe "could not handle."

      val emptyYCoordHandler = YCoordHandler(None)
      val result3 = emptyYCoordHandler.handle(Move(1, 0, 0, true), field)
      result3 shouldBe a [Failure[?]]
      result3.failed.get.getMessage shouldBe "could not handle."

      val emptyMoveAvailableHandler = MoveAvailableHandler(None)
      val result4 = emptyMoveAvailableHandler.handle(Move(1, 0, 0, true), field)
      result4 shouldBe a [Success[?]]
      result4.get shouldBe "Move was successful!"
    }
    "handle nextHandler in MoveAvailableHandler but then fail" in {
      val emptyLineHandler = LineHandler(None)

      val emptyMoveAvailableHandler = MoveAvailableHandler(Some(emptyLineHandler))
      val result = emptyMoveAvailableHandler.handle(Move(1, 0, 0, true), field)
      result shouldBe a [Failure[?]]
      result.failed.get.getMessage shouldBe "could not handle."
    }
  }
  "MoveValidator" should {
    "return Success when move is valid" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val move = Move(1, 2, 2, true)
      MoveValidator.validate(move, field) shouldBe Success("Move was successful!")
    }

    "fail when move vector is invalid" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val move = Move(3, 2, 2, true)
      MoveValidator.validate(move, field).failed.get shouldBe a[MatchError]
    }

    "fail when x-coordinate is out of bounds" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val move = Move(1, -1, 2, true)
      MoveValidator.validate(move, field).failed.get shouldBe a[MatchError]
    }

    "fail when y-coordinate is out of bounds" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val move = Move(1, 2, 6, true)
      MoveValidator.validate(move, field).failed.get shouldBe a[MatchError]
    }

    "fail when the move is already taken" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(1, 1, true)
      val move = Move(1, 1, 1, true)
      MoveValidator.validate(move, field).failed.get shouldBe a[MatchError]
    }
  }
}
