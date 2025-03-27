package computerComponent

import api.service.ModelRequestHttp
import de.github.dotsandboxes.lib.{Move, SquareCase}

trait ComputerInterface:
  def calculateMove(field: String): Option[Move]

  def isClosingMove(field: String, vec: Int, x: Int, y: Int): Boolean =
    val squareCases: Vector[SquareCase] = ModelRequestHttp.squareCases(field, vec, x, y)
    squareCases.exists(state => ModelRequestHttp.checkAllCells(field, state, x, y).forall(identity))

  def isRiskyMove(field: String, vec: Int, x: Int, y: Int): Boolean =
    val squareCases: Vector[SquareCase] = ModelRequestHttp.squareCases(field, vec, x, y)
    squareCases.exists(state => ModelRequestHttp.checkAllCells(field, state, x, y).count(identity) == 2)

  def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean =
    moveSeq1._1 == moveSeq2._1 && moveSeq1._2.toSet == moveSeq2._2.toSet

  def getMissingMoves(field: String, vec: Int, x: Int, y: Int): Vector[(Int, Int, Int)] =
    val squareCases: Vector[SquareCase] = ModelRequestHttp.squareCases(field, vec, x, y)
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases.map(state => ModelRequestHttp.cellsToCheck(field, state, x, y))
    casesWithCellsToCheck.collect {
      case squareMoves if squareMoves.count {
        case (vec, x, y) =>
          if vec == 1 then ModelRequestHttp.rowCell(field, x, y)
          else ModelRequestHttp.colCell(field, x, y)
      } == 2 =>
        squareMoves.find { case (vec, x, y) =>
          if vec == 1 then !ModelRequestHttp.rowCell(field, x, y)
          else !ModelRequestHttp.colCell(field, x, y)
        }.get
    }

  def evaluateChainWithPointsOutcome(moveCoord: (Int, Int, Int), field: String): (Int, Vector[(Int, Int, Int)]) =
    def exploreStackDFS(stack: Vector[(Int, Int, Int)], visited: Vector[(Int, Int, Int)], tempField: String, count: Int): (Int, Vector[(Int, Int, Int)]) =
      return (stack: @unchecked) match
        case Vector() => (count, visited)
        case rest :+ thisMove =>
          val (vec, x, y) = thisMove
          val updatedField = if vec == 1
            then ModelRequestHttp.putRow(tempField, x, y, true)
            else ModelRequestHttp.putCol(tempField, x, y, true)

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
    val initialField: String = if vec == 1 then ModelRequestHttp.putRow(field, x, y, true) else ModelRequestHttp.putCol(field, x, y, true)
    val initialMissingMoves: Vector[(Int, Int, Int)] = getMissingMoves(initialField, vec, x, y)
    return exploreStackDFS(initialMissingMoves, Vector(moveCoord), initialField, evaluatePointsOutcome(vec, x, y, initialField))

  def evaluatePointsOutcome(vec: Int, x: Int, y: Int, field: String): Int =
    val squareCases: Vector[SquareCase] = ModelRequestHttp.squareCases(field, vec, x, y)
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases.map(squareCase => ModelRequestHttp.cellsToCheck(field, squareCase, x, y))
    val cellStates: Vector[Vector[Boolean]] = casesWithCellsToCheck.map(
      _.map { case (vec, x, y) => if vec == 1 then ModelRequestHttp.rowCell(field, x, y) else ModelRequestHttp.colCell(field, x, y) }
    )
    return cellStates.count(_.forall(identity))
