package fieldComponent.fieldImpl

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.ComputerDifficulty
import de.github.dotsandboxes.lib.{BoardSize, CellData, Move, Player, PlayerSize, PlayerType, SquareCase, Status}
import matrixComponent.MatrixInterface
import matrixComponent.matrixImpl.Matrix
import play.api.libs.json.{JsLookupResult, JsValue, Json}
import scala.util.Try

case class Field(matrix: MatrixInterface) extends FieldInterface:
  def this(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType) =
    this(new Matrix(boardSize, status, playerSize, playerType))

  override def newField(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): FieldInterface =
    new Field(new Matrix(boardSize, status, playerSize, playerType))

  override def bar(
    length: Int,
    cellNum: Int,
    rowIndex: Int,
    rowFunc: (Int, Int, Int) => String
  ): String =
    List
      .tabulate(cellNum)(colIndex => rowFunc(rowIndex, colIndex, length))
      .mkString(Connectors("O"), Connectors("O"), Connectors("O")) + "\n"

  override def cells(
    rowSize: Int,
    length: Int,
    height: Int,
    colFunc: (Int, Int, Int) => String
  ): String =
    List.fill(height)(
      List
        .tabulate(maxPosY + 1)(colIndex => colFunc(rowSize, colIndex, length))
        .mkString + "\n"
    ).mkString

  override def mesh(length: Int, height: Int): String =
    List
      .tabulate(maxPosX)(x => bar(length, maxPosY, x, rows) + cells(x, length, height, columns))
      .mkString + bar(length, maxPosY, maxPosX, rows)
      
  override def rows(rowIndex: Int, colIndex: Int, length: Int): String = getRowCell(rowIndex, colIndex) match
    case false => Connectors("-") * length
    case true  => Connectors("=") * length
  override def columns(rowIndex: Int, colIndex: Int, length: Int): String = getColCell(rowIndex, colIndex) match
    case false => Connectors("¦") + status(rowIndex, colIndex, length)
    case true  => Connectors("‖") + status(rowIndex, colIndex, length)
  override def boardSize: BoardSize = matrix.getBoardSize
  override def playerSize: PlayerSize = matrix.getPlayerSize
  override def status(rowIndex: Int, colIndex: Int, length: Int): String = (colIndex < maxPosY) match
    case false => Connectors("")
    case true  => space(length) + getStatusCell(rowIndex, colIndex) + space(length)
  override def winner: String = if (playerList.indices.map(playerList(_).points).count(_ == playerList.maxBy(_._2).points) > 1) "It's a draw!"
  else s"Player ${playerList.maxBy(_._2).playerId} wins!"
  override def stats: String = playerList.indices.map(x => s"Player ${playerList(x).playerId} [points: ${playerList(x).points}]").mkString("\n")
  override def getStatusCell(row: Int, col: Int): Status = matrix.statusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = matrix.rowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = matrix.colCell(row, col)
  override def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean] = matrix.checkAllCells(squareCase, x, y)
  override def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)] = matrix.cellsToCheck(squareCase, x, y)
  override def putStatus(row: Int, col: Int, status: Status): Field = copy(matrix.replaceStatusCell(row, col, status))
  override def putRow(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceRowCell(row, col, value))
  override def putCol(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceColCell(row, col, value))
  override def getUnoccupiedRowCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedRowCoord()
  override def getUnoccupiedColCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedColCoord()
  override def isFinished: Boolean = (matrix.vectorRow ++ matrix.vectorCol).forall(_.forall(_.equals(true)))
  override def isEdge(move: Move): Boolean = matrix.isEdge(move)
  override def checkSquare(squareCase: SquareCase, x: Int, y: Int): Field = copy(matrix.checkSquare(squareCase, x, y))
  override def currentPlayer: Player = matrix.getCurrentPlayer
  override def currentPlayerId: String = matrix.currentPlayerInfo._1
  override def currentPlayerIndex: Int = matrix.currentPlayerInfo._2
  override def currentStatus: Vector[Vector[Status]] = matrix.vectorStatus
  override def currentPoints: Int = matrix.currentPoints
  override def nextPlayer: Field = copy(matrix.changePlayer)
  override def updatePlayer(curPlayerIndex: Int): Field = copy(matrix.updatePlayer(curPlayerIndex))
  override def playerIndex: Int = matrix.playerIndex
  override def addPoints(curPlayerIndex: Int, points: Int): Field = copy(matrix.addPoints(curPlayerIndex, points))
  override def playerList: Vector[Player] = matrix.playerList
  override def playerType: PlayerType = matrix.playerList.last.playerType
  override def getPoints(index: Int): Int = matrix.getPoints(index)
  override def rowSize(): Int = matrix.rowSize()
  override def colSize(): Int = matrix.colSize()
  override def space(length: Int): String = " " * ((length - 1) / 2)

  override def squareCases(vec: Int, row: Int, col: Int, field: FieldInterface): Vector[SquareCase] = vec match
    case 1 =>
      Vector(
        Option.when(row >= 0 && row < field.maxPosX)(SquareCase.DownCase),
        Option.when(row > 0 && row <= field.maxPosX)(SquareCase.UpCase)
      ).flatten
    case 2 =>
      Vector(
        Option.when(col >= 0 && col < field.maxPosY)(SquareCase.RightCase),
        Option.when(col > 0 && col <= field.maxPosY)(SquareCase.LeftCase)
      ).flatten

  override def isClosingMove(vec: Int, row: Int, col: Int, field: FieldInterface): Boolean =
    field.squareCases(vec, row, col, field).exists(state => field.checkAllCells(state, row, col).forall(identity))
  override def isRiskyMove(vec: Int, row: Int, col: Int, field: FieldInterface): Boolean =
    field.squareCases(vec, row, col, field).exists(state => field.checkAllCells(state, row, col).count(identity) == 2)
  override def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean =
    moveSeq1._1 == moveSeq2._1 && moveSeq1._2.toSet == moveSeq2._2.toSet

  override def getMissingMoves(vec: Int, row: Int, col: Int, field: FieldInterface): Vector[(Int, Int, Int)] =
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      field.squareCases(vec, row, col, field).map(state => field.cellsToCheck(state, row, col))
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

  override def evaluatePointsOutcome(vec: Int, row: Int, col: Int, field: FieldInterface): Int =
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      field.squareCases(vec, row, col, field).map(squareCase => field.cellsToCheck(squareCase, row, col))
    val cellStates: Vector[Vector[Boolean]] = casesWithCellsToCheck.map(
      _.map { case (vec, x, y) => if vec == 1 then field.getRowCell(x, y) else field.getColCell(x, y) }
    )
    return cellStates.count(_.forall(identity))

  override def evaluateChainWithPointsOutcome(moveCoord: (Int, Int, Int), field: FieldInterface): (Int, Vector[(Int, Int, Int)]) =
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
            getMissingMoves(thisMove._1, x, y, updatedField)
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
    val initialMissingMoves: Vector[(Int, Int, Int)] = getMissingMoves(vec, x, y, initialField)
    return exploreStackDFS(initialMissingMoves, Vector(moveCoord), initialField, evaluatePointsOutcome(vec, x, y, initialField))

  override def toCellData: CellData =
    val (row, col) = boardSize.dimensions
    CellData(
      Vector.tabulate(col+1, row)((row, col) => getRowCell(row, col)),
      Vector.tabulate(col, row+1)((row, col) => getColCell(row, col)),
      Vector.tabulate(col, row)((row, col) => getStatusCell(row, col).toString)
    )
  override def toString = mesh(7, 2)
  override val maxPosX = matrix.maxPosX
  override val maxPosY = matrix.maxPosY
  override val vectorRow = matrix.vectorRow
  override val vectorCol = matrix.vectorCol
