package fieldComponent.fieldImpl

import fieldComponent.FieldInterface
import matrixComponent.MatrixInterface
import matrixComponent.matrixImpl.Matrix
import lib.{PlayerType, BoardSize, PlayerSize, SquareCases, Player, Status, Move}

case class Field(matrix: MatrixInterface) extends FieldInterface:
  def this(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType) =
    this(new Matrix(boardSize, status, playerSize, playerType))
  
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
  override def checkAllCells(squareCase: SquareCases, x: Int, y: Int): Vector[Boolean] = matrix.checkAllCells(squareCase, x, y)
  override def cellsToCheck(squareCase: SquareCases, x: Int, y: Int): Vector[(Int, Int, Int)] = matrix.cellsToCheck(squareCase, x, y)
  override def putStatus(row: Int, col: Int, status: Status): Field = copy(matrix.replaceStatusCell(row, col, status))
  override def putRow(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceRowCell(row, col, value))
  override def putCol(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceColCell(row, col, value))
  override def getUnoccupiedRowCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedRowCoord()
  override def getUnoccupiedColCoord(): Vector[(Int, Int, Int)] = matrix.getUnoccupiedColCoord()
  override def isFinished: Boolean = (matrix.vectorRow ++ matrix.vectorCol).forall(_.forall(_.equals(true)))
  override def isEdge(move: Move): Boolean = matrix.isEdge(move)
  override def checkSquare(squareCase: SquareCases, x: Int, y: Int): Field = copy(matrix.checkSquare(squareCase, x, y))
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
  override val maxPosX = matrix.maxPosX
  override val maxPosY = matrix.maxPosY
  override val vectorRow = matrix.vectorRow
  override val vectorCol = matrix.vectorCol
  override def toString = mesh(7, 2)
