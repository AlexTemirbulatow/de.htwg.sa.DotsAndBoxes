package model.fieldComponent.fieldImpl

import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib._
import model.matrixComponent.MatrixInterface
import model.matrixComponent.matrixImpl.Matrix

case class Field(matrix: MatrixInterface) extends FieldInterface:
  def this(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType) =
    this(new Matrix(boardSize, status, playerSize, playerType))

  def space(length: Int): String = " " * ((length - 1) / 2)

  def status(rowIndex: Int, colIndex: Int, length: Int): String = (colIndex < maxPosY) match
    case false => Connectors("")
    case true  => space(length) + getStatusCell(rowIndex, colIndex) + space(length)

  def rows(rowIndex: Int, colIndex: Int, length: Int): String = getRowCell(rowIndex, colIndex) match
    case false => Connectors("-") * length
    case true  => Connectors("=") * length

  def columns(rowIndex: Int, colIndex: Int, length: Int): String = getColCell(rowIndex, colIndex) match
    case false => Connectors("¦") + status(rowIndex, colIndex, length)
    case true  => Connectors("‖") + status(rowIndex, colIndex, length)

  def bar(length: Int, cellNum: Int, rowIndex: Int): String = List
    .tabulate(cellNum)(colIndex => rows(rowIndex, colIndex, length))
    .mkString(Connectors("O"), Connectors("O"), Connectors("O")) + "\n"

  def cells(rowSize: Int, length: Int, height: Int): String = List.fill(height)(List
    .tabulate(maxPosY + 1)(colIndex => columns(rowSize, colIndex, length))
    .mkString + "\n").mkString

  def mesh(length: Int, height: Int): String = List
    .tabulate(maxPosX)(x => bar(length, maxPosY, x) + cells(x, length, height))
    .mkString + bar(length, maxPosY, maxPosX)

  def winner: String = if (playerList.indices.map(playerList(_).points).count(_ == playerList.maxBy(_._2).points) > 1)
    "It's a draw!" else s"Player ${playerList.maxBy(_._2).playerId} wins!"
  
  def stats: String = playerList.indices.map(x => s"Player ${playerList(x).playerId} [points: ${playerList(x).points}]").mkString("\n")

  override val maxPosX: Int = matrix.maxPosX
  override val maxPosY: Int = matrix.maxPosY
  override val rowSize: Int = matrix.rowSize
  override val colSize: Int = matrix.colSize

  override def newField(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): FieldInterface =
    new Field(new Matrix(boardSize, status, playerSize, playerType))

  override def boardSize: BoardSize = matrix.getBoardSize
  override def playerSize: PlayerSize = matrix.getPlayerSize
  override def playerType: PlayerType = matrix.getPlayerType

  override def getUnoccupiedRowCoords: Vector[(Int, Int, Int)] = matrix.getUnoccupiedRowCoords
  override def getUnoccupiedColCoords: Vector[(Int, Int, Int)] = matrix.getUnoccupiedColCoords

  override def getStatusCell(row: Int, col: Int): Status = matrix.statusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = matrix.rowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = matrix.colCell(row, col)

  override def putStatus(row: Int, col: Int, status: Status): FieldInterface = copy(matrix.replaceStatusCell(row, col, status))
  override def putRow(row: Int, col: Int, value: Boolean): FieldInterface = copy(matrix.replaceRowCell(row, col, value))
  override def putCol(row: Int, col: Int, value: Boolean): FieldInterface = copy(matrix.replaceColCell(row, col, value))

  override def addPoints(curPlayerIndex: Int, points: Int): FieldInterface = copy(matrix.addPoints(curPlayerIndex, points))
  override def nextPlayer: FieldInterface = copy(matrix.changePlayer)
  override def updatePlayer(curPlayerIndex: Int): FieldInterface = copy(matrix.updatePlayer(curPlayerIndex))

  override def checkSquare(squareCase: SquareCase, x: Int, y: Int): FieldInterface = copy(matrix.checkSquare(squareCase, x, y))
  
  override def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean = matrix.isCircularSequence(moveSeq1, moveSeq2)
  override def isFinished: Boolean = matrix.isFinished
  override def isEdge(move: Move): Boolean = matrix.isEdge(move)
  
  override def playerList: Vector[Player] = matrix.playerList
  override def currentPlayer: Player = matrix.getCurrentPlayer
  override def currentPlayerIndex: Int = matrix.currentPlayerIndex
  override def getPlayerPoints(playerIndex: Int): Int = matrix.getPlayerPoints(playerIndex)
  override def currentStatus: Vector[Vector[Status]] = matrix.vectorStatus

  override def gameBoardData: GameBoardData =
    val (row, col) = boardSize.dimensions
    GameBoardData(
      currentPlayer,
      Vector.tabulate(col+1, row)((row, col) => getRowCell(row, col)),
      Vector.tabulate(col, row+1)((row, col) => getColCell(row, col)),
      Vector.tabulate(col, row)((row, col) => getStatusCell(row, col).toString)
    )
  override def fieldData(computerDifficulty: ComputerDifficulty): FieldData = FieldData(boardSize, playerSize, playerType, computerDifficulty)
  override def playerGameData: PlayerGameData = PlayerGameData(currentPlayer, winner, stats, playerList)
  override def fieldSizeData: FieldSizeData = FieldSizeData(rowSize, colSize)
  override def getWinningMoves(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[Move] =
    coords.collect {
      case (vec, x, y) if isClosingMove(vec, x, y, field) => Move(vec, x, y, true)
    }
  override def getSaveMoves(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[Move] =
    coords.collect {
      case (vec, x, y) if !isRiskyMove(vec, x, y, field) && !isClosingMove(vec, x, y, field) => Move(vec, x, y, true)
    }
  override def getMissingMoves(vec: Int, row: Int, col: Int, field: FieldInterface): Vector[(Int, Int, Int)] =
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases(vec, row, col, field).map(state => cellsToCheck(state, row, col))
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
  override def chainsWithPointsOutcome(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[(Int, Vector[(Int, Int, Int)])] =
    coords.map(evaluateChainWithPointsOutcome(_, field))

  def squareCases(vec: Int, row: Int, col: Int, field: FieldInterface): Vector[SquareCase] = vec match
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

  def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean] = matrix.checkAllCells(squareCase, x, y)
  def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)] = matrix.cellsToCheck(squareCase, x, y)

  def isClosingMove(vec: Int, row: Int, col: Int, field: FieldInterface): Boolean =
    squareCases(vec, row, col, field).exists(state => checkAllCells(state, row, col).forall(identity))
  def isRiskyMove(vec: Int, row: Int, col: Int, field: FieldInterface): Boolean =
    squareCases(vec, row, col, field).exists(state => checkAllCells(state, row, col).count(identity) == 2)

  def evaluatePointsOutcome(vec: Int, row: Int, col: Int, field: FieldInterface): Int =
    val casesWithCellsToCheck: Vector[Vector[(Int, Int, Int)]] =
      squareCases(vec, row, col, field).map(squareCase => cellsToCheck(squareCase, row, col))
    val cellStates: Vector[Vector[Boolean]] = casesWithCellsToCheck.map(
      _.map { case (vec, x, y) => if vec == 1 then field.getRowCell(x, y) else field.getColCell(x, y) }
    )
    return cellStates.count(_.forall(identity))

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

  override def toString = mesh(7, 2)
