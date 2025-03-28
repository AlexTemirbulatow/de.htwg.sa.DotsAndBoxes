package common.model.fieldService

import de.github.dotsandboxes.lib.{BoardSize, CellData, Move, Player, PlayerSize, PlayerType, SquareCase, Status}

trait FieldInterface:
  val maxPosX: Int
  val maxPosY: Int
  val vectorRow: Vector[Vector[Boolean]]
  val vectorCol: Vector[Vector[Boolean]]
  def newField(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): FieldInterface
  def bar(length: Int, cellNum: Int, rowIndex: Int, rowFunc: (Int, Int, Int) => String): String
  def cells(rowSize: Int, length: Int, height: Int, colFunc: (Int, Int, Int) => String): String
  def mesh(length: Int, height: Int): String
  def rows(rowIndex: Int, colIndex: Int, length: Int): String
  def columns(rowIndex: Int, colIndex: Int, length: Int): String
  def status(rowIndex: Int, colIndex: Int, length: Int): String
  def boardSize: BoardSize
  def playerSize: PlayerSize
  def winner: String
  def stats: String
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean]
  def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)]
  def putStatus(row: Int, col: Int, status: Status): FieldInterface
  def putRow(row: Int, col: Int, value: Boolean): FieldInterface
  def putCol(row: Int, col: Int, value: Boolean): FieldInterface
  def getUnoccupiedRowCoord(): Vector[(Int, Int, Int)]
  def getUnoccupiedColCoord(): Vector[(Int, Int, Int)]
  def isFinished: Boolean
  def isEdge(move: Move): Boolean
  def checkSquare(squareCase: SquareCase, x: Int, y: Int): FieldInterface
  def currentPlayer: Player
  def currentPlayerId: String
  def currentPlayerIndex: Int
  def currentStatus: Vector[Vector[Status]]
  def currentPoints: Int
  def nextPlayer: FieldInterface
  def updatePlayer(curPlayerIndex: Int): FieldInterface
  def playerIndex: Int
  def addPoints(curPlayerIndex: Int, points: Int): FieldInterface
  def playerList: Vector[Player]
  def playerType: PlayerType
  def getPoints(index: Int): Int
  def rowSize(): Int
  def colSize(): Int
  def space(length: Int): String
  def toCellData: CellData
  override def toString: String
