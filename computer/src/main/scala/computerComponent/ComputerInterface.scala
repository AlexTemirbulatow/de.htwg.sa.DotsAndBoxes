package computer
package computerComponent

import de.htwg.se.dotsandboxes.model.fieldComponent.FieldInterface
import de.htwg.se.dotsandboxes.util.Move
import de.htwg.se.dotsandboxes.util.moveState.SquareCase
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status

trait ComputerInterface:
  def calculateMove(field: FieldInterface): Option[Move]

  def isClosingMove(field: FieldInterface, vec: Int, x: Int, y: Int): Boolean =
    val squareCases: Vector[SquareCase] = getSquareCases(vec, x, y, field)
    squareCases.exists(state => field.checkAllCells(state, x, y).forall(identity))

  def isRiskyMove(field: FieldInterface, vec: Int, x: Int, y: Int): Boolean =
    val squareCases: Vector[SquareCase] = getSquareCases(vec, x, y, field)
    squareCases.exists(state => field.checkAllCells(state, x, y).count(identity) == 2)

  def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean =
    moveSeq1._1 == moveSeq2._1 && moveSeq1._2.toSet == moveSeq2._2.toSet

  def getMissingMoves(field: FieldInterface, vec: Int, x: Int, y: Int): Vector[(Int, Int, Int)] =
    val squareCases: Vector[SquareCase] = getSquareCases(vec, x, y, field)
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases.map(state => field.cellsToCheck(state, x, y))
    casesWithCellsToCheck.collect {
      case squareMoves if squareMoves.count {
        case (vec, x, y) =>
          if vec == 1 then field.getRowCell(x, y)
          else field.getColCell(x, y)
      } == 2 =>
        squareMoves.find { case (vec, x, y) =>
          if vec == 1 then !field.getRowCell(x, y)
          else !field.getColCell(x, y)
        }.get
    }

  def evaluateChainWithPointsOutcome(moveCoord: (Int, Int, Int), field: FieldInterface): (Int, Vector[(Int, Int, Int)]) =
    def exploreStackDFS(stack: Vector[(Int, Int, Int)], visited: Vector[(Int, Int, Int)], tempField: FieldInterface, count: Int): (Int, Vector[(Int, Int, Int)]) =
      return (stack: @unchecked) match
        case Vector() => (count, visited)
        case rest :+ thisMove =>
          val (vec, x, y) = thisMove
          val updatedField = if vec == 1
            then tempField.putRow(x, y, true)
            else tempField.putCol(x, y, true)

          val points: Int = evaluatePointsOutcome(vec, x, y, updatedField)
          val nextMissingMoves: Vector[(Int, Int, Int)] =
            getMissingMoves(updatedField, thisMove._1, x, y)
              .filterNot(visited.contains)
              .filterNot(stack.contains)

          exploreStackDFS(
            if nextMissingMoves.isEmpty then rest else rest ++ nextMissingMoves,
            visited :+ thisMove,
            updatedField,
            count + points
          )

    val (vec, x, y) = moveCoord
    val initialField = if vec == 1 then field.putRow(x, y, true) else field.putCol(x, y, true)
    val initialMissingMoves: Vector[(Int, Int, Int)] = getMissingMoves(initialField, vec, x, y)
    return exploreStackDFS(initialMissingMoves, Vector(moveCoord), initialField, evaluatePointsOutcome(vec, x, y, initialField))

  def evaluatePointsOutcome(vec: Int, x: Int, y: Int, field: FieldInterface): Int =
    val squareCases: Vector[SquareCase] = getSquareCases(vec, x, y, field)
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases.map(squareCase => field.cellsToCheck(squareCase, x, y))
    val cellStates: Vector[Vector[Boolean]] = casesWithCellsToCheck.map(
      _.map { case (vec, x, y) => if vec == 1 then field.getRowCell(x, y) else field.getColCell(x, y) }
    )
    return cellStates.count(_.forall(identity))

  def getSquareCases(vec: Int, x: Int, y: Int, field: FieldInterface): Vector[SquareCase] = vec match
    case 1 =>
      Vector(
        Option.when(x >= 0 && x < field.maxPosX)(SquareCase.DownCase),
        Option.when(x > 0 && x <= field.maxPosX)(SquareCase.UpCase)
      ).flatten
    case 2 =>
      Vector(
        Option.when(y >= 0 && y < field.maxPosY)(SquareCase.RightCase),
        Option.when(y > 0 && y <= field.maxPosY)(SquareCase.LeftCase)
      ).flatten
