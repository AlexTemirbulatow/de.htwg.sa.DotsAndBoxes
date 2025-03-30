package computerComponent

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, SquareCase, Status, Move}

class ComputerInterfaceSpec extends AnyWordSpec {
  "ComputerInterface" when {
    /*
    val computerInterface: ComputerInterface = new ComputerInterface:
      def calculateMove(field: FieldInterface): Option[Move] = ???
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "checking for specific moves in a field" should {
      "recognize a closing move" in {
        val closingField = field.putRow(0, 0, true).putCol(0, 0, true).putCol(0, 1, true)
        val closingMove = Move(1, 1, 0, true)
        val notClosingMove = Move(2, 1, 1, true)

        val closing: Boolean = computerInterface
          .isClosingMove(closingField, closingMove.vec, closingMove.x, closingMove.y)
        val notClosing: Boolean = computerInterface
          .isClosingMove(closingField, notClosingMove.vec, notClosingMove.x, notClosingMove.y)

        closing shouldBe true
        notClosing shouldBe false
      }
      "recognize a risky move" in {
        val riskyField = field.putRow(0, 0, true).putCol(0, 0, true)
        val riskyMove1 = Move(1, 1, 0, true)
        val riskyMove2 = Move(2, 0, 1, true)
        val notRiskyMove = Move(1, 1, 1, true)

        val risky1: Boolean = computerInterface
          .isRiskyMove(riskyField, riskyMove1.vec, riskyMove1.x, riskyMove1.y)
        val risky2: Boolean = computerInterface
          .isRiskyMove(riskyField, riskyMove2.vec, riskyMove2.x, riskyMove2.y)
        val notRisky: Boolean = computerInterface
          .isRiskyMove(riskyField, notRiskyMove.vec, notRiskyMove.x, notRiskyMove.y)

        risky1 shouldBe true
        risky2 shouldBe true
        notRisky shouldBe false
      }
      "recognize a circular sequence in a 2x2 circle" in {
        val circularField = field
          .putRow(0, 0, true)
          .putRow(0, 1, true)
          .putRow(2, 0, true)
          .putRow(2, 1, true)
          .putCol(0, 0, true)
          .putCol(0, 2, true)
          .putCol(1, 0, true)
          .putCol(1, 2, true)

        val circularMoves1: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 1))
        val circularMoves2: Vector[(Int, Int, Int)] = Vector((2, 0, 1), (2, 1, 1))
        val notCircularMoves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 2))

        val circular1: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(circularMoves1.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(circularMoves1.last, circularField)
          )
        val circular2: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(circularMoves2.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(circularMoves2.last, circularField)
          )
        val notCircular: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(notCircularMoves.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(notCircularMoves.last, circularField)
          )

        circular1 shouldBe true
        circular2 shouldBe true
        notCircular shouldBe false
      }
      "recognize a circular sequence in a 3x2 circle" in {
        val circularField = field
          .putRow(0, 0, true)
          .putRow(0, 1, true)
          .putRow(0, 2, true)
          .putRow(2, 0, true)
          .putRow(2, 1, true)
          .putRow(2, 2, true)
          .putRow(1, 1, true)
          .putCol(0, 0, true)
          .putCol(0, 3, true)
          .putCol(1, 0, true)
          .putCol(1, 3, true)

        val circularMoves1: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 2))
        val circularMoves2: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (2, 0, 1))
        val circularMoves3: Vector[(Int, Int, Int)] = Vector((2, 0, 1), (2, 1, 2))
        val notCircularMoves: Vector[(Int, Int, Int)] = Vector((1, 1, 0), (1, 1, 3))

        val circular1: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(circularMoves1.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(circularMoves1.last, circularField)
          )
        val circular2: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(circularMoves2.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(circularMoves2.last, circularField)
          )
        val circular3: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(circularMoves3.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(circularMoves3.last, circularField)
          )
        val notCircular: Boolean = computerInterface
          .isCircularSequence(
            computerInterface.evaluateChainWithPointsOutcome(notCircularMoves.head, circularField),
            computerInterface.evaluateChainWithPointsOutcome(notCircularMoves.last, circularField)
          )

        circular1 shouldBe true
        circular2 shouldBe true
        circular3 shouldBe true
        notCircular shouldBe false
      }
      "get the only missing row move to fill a square" in {
        val missingRowField = field
          .putRow(0, 0, true)
          .putCol(0, 0, true)
          .putCol(0, 1, true)

        val missingMove1 = computerInterface.getMissingMoves(missingRowField, 1, 0, 0)
        val missingMove2 = computerInterface.getMissingMoves(missingRowField, 2, 0, 0)
        val missingMove3 = computerInterface.getMissingMoves(missingRowField, 2, 0, 1)

        missingMove1 shouldBe missingMove2
        missingMove2 shouldBe missingMove3

        missingMove1.size shouldBe 1
        missingMove1.head should be((1, 1, 0))
      }
      "get the only missing column move to fill a square" in {
        val missingColField = field
          .putRow(0, 0, true)
          .putRow(1, 0, true)
          .putCol(0, 0, true)

        val missingMove1 = computerInterface.getMissingMoves(missingColField, 1, 0, 0)
        val missingMove2 = computerInterface.getMissingMoves(missingColField, 1, 1, 0)
        val missingMove3 = computerInterface.getMissingMoves(missingColField, 2, 0, 0)
        val noMissingMove = computerInterface.getMissingMoves(missingColField, 1, 0, 1)

        missingMove1 shouldBe missingMove2
        missingMove2 shouldBe missingMove3
        missingMove1 should not be (noMissingMove)

        missingMove1.size shouldBe 1
        missingMove1.head should be((2, 0, 1))

        noMissingMove.size shouldBe 0
      }
      "get multiple missing moves that each fill a square" in {
        val missingMovesField = field
          .putRow(0, 0, true)
          .putRow(0, 1, true)
          .putRow(1, 0, true)
          .putRow(1, 1, true)

        val missingMoves = computerInterface.getMissingMoves(missingMovesField, 2, 0, 1)

        missingMoves.size shouldBe 2
        missingMoves.head should be((2, 0, 2))
        missingMoves.last should be((2, 0, 0))
      }
    }
    "evaluating depth first search move outcomes" should {
      val chainedField = field
        .putRow(1, 1, true)
        .putRow(1, 2, true)
        .putRow(2, 2, true)
        .putRow(2, 3, true)
        .putCol(0, 0, true)
        .putCol(0, 1, true)
        .putCol(0, 3, true)
        .putCol(0, 4, true)
        .putCol(1, 0, true)
        .putCol(1, 1, true)
        .putCol(1, 4, true)
        .putCol(2, 0, true)
        .putCol(2, 1, true)
        .putCol(2, 2, true)
        .putCol(2, 4, true)
      "evaluate all chain moves and points outcome for given move" in {
        val chainMovesWithPoints: (Int, Vector[(Int, Int, Int)]) =
          computerInterface.evaluateChainWithPointsOutcome((1, 0, 0), chainedField)

        chainMovesWithPoints._1 shouldBe 3
        chainMovesWithPoints._2.size shouldBe 4
        chainMovesWithPoints._2 should be(
          Vector(
            (1, 0, 0),
            (1, 1, 0),
            (1, 2, 0),
            (1, 3, 0)
          )
        )
      }
      "evaluate all chains for available moves" in {
        val allAvailableMoves: Vector[(Int, Int, Int)] =
          chainedField.getUnoccupiedRowCoord ++ chainedField.getUnoccupiedColCoord

        val allPossibleChains: Vector[(Int, Vector[(Int, Int, Int)])] = allAvailableMoves
          .map(computerInterface.evaluateChainWithPointsOutcome(_, chainedField))

        val distinctChains: Vector[(Int, Set[(Int, Int, Int)])] = allPossibleChains
          .map { case (x, innerVec) => (x, innerVec.map { case (vec, x, y) => (vec, x, y) }.toSet) }
          .distinctBy(_._2)

        distinctChains.size shouldBe 4
        distinctChains(0)._1 shouldBe 3
        distinctChains(1)._1 shouldBe 2
        distinctChains(2)._1 shouldBe 5
        distinctChains(3)._1 shouldBe 2

        allPossibleChains should contain((3, Vector((1, 0, 0), (1, 1, 0), (1, 2, 0), (1, 3, 0))))
        allPossibleChains should contain((2, Vector((1, 0, 1), (2, 0, 2), (1, 0, 2))))
        allPossibleChains should contain((5, Vector((1, 0, 3), (1, 1, 3), (2, 1, 3), (2, 1, 2), (1, 2, 1), (1, 3, 1))))
        allPossibleChains should contain((2, Vector((1, 3, 2), (2, 2, 3), (1, 3, 3))))
      }
      "evaluate nothing on empty field" in {
        val allAvailableMoves: Vector[(Int, Int, Int)] =
          field.getUnoccupiedRowCoord ++ field.getUnoccupiedColCoord

        val emptyEval = allAvailableMoves
          .map(computerInterface.evaluateChainWithPointsOutcome(_, field))
          .filterNot(_._1 == 0)

        emptyEval.size shouldBe 0
      }
      "evaluate one point for a winning move" in {
        val winningField = chainedField.putRow(0, 0, true).putRow(0, 1, true)

        val point1 = computerInterface.evaluatePointsOutcome(1, 1, 0, winningField)
        val point2 = computerInterface.evaluatePointsOutcome(2, 0, 2, winningField)
        val noPoint = computerInterface.evaluatePointsOutcome(1, 0, 2, winningField)

        point1 shouldBe 1
        point2 shouldBe 1
        noPoint shouldBe 0
      }
      "evaluate two points for a winning move" in {
        val winningField = chainedField.putRow(0, 0, true).putRow(2, 0, true).putRow(0, 1, true).putRow(0, 2, true)

        val twoPoints1 = computerInterface.evaluatePointsOutcome(1, 1, 0, winningField)
        val twoPoints2 = computerInterface.evaluatePointsOutcome(2, 0, 2, winningField)

        twoPoints1 shouldBe 2
        twoPoints2 shouldBe 2
      }
    }
    "checking for square cases of a move" should {
      "return a down case" in {
        val squareCase = computerInterface.getSquareCases(1, 0, 0, field)
        squareCase.size shouldBe 1
        squareCase.head should be(SquareCase.DownCase)
      }
      "return a up case" in {
        val squareCase = computerInterface.getSquareCases(1, 3, 0, field)
        squareCase.size shouldBe 1
        squareCase.head should be(SquareCase.UpCase)
      }
      "return a right case" in {
        val squareCase = computerInterface.getSquareCases(2, 0, 0, field)
        squareCase.size shouldBe 1
        squareCase.head should be(SquareCase.RightCase)
      }
      "return a left case" in {
        val squareCase = computerInterface.getSquareCases(2, 0, 4, field)
        squareCase.size shouldBe 1
        squareCase.head should be(SquareCase.LeftCase)
      }
      "return down and up cases" in {
        val squareCases = computerInterface.getSquareCases(1, 1, 1, field)
        squareCases.size shouldBe 2
        squareCases should be(Vector(SquareCase.DownCase, SquareCase.UpCase))
      }
      "return right and left cases" in {
        val squareCases = computerInterface.getSquareCases(2, 0, 2, field)
        squareCases.size shouldBe 2
        squareCases should be(Vector(SquareCase.RightCase, SquareCase.LeftCase))
      }
    }*/
  }
}
