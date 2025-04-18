package common.model.fieldService

import de.github.dotsandboxes.lib._

trait FieldInterface:
  val maxPosX: Int
  val maxPosY: Int
  val rowSize: Int
  val colSize: Int
  def newField(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): FieldInterface
  def boardSize: BoardSize
  def playerSize: PlayerSize
  def playerType: PlayerType
  def getUnoccupiedRowCoords: Vector[(Int, Int, Int)]
  def getUnoccupiedColCoords: Vector[(Int, Int, Int)]
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def putStatus(row: Int, col: Int, status: Status): FieldInterface
  def putRow(row: Int, col: Int, value: Boolean): FieldInterface
  def putCol(row: Int, col: Int, value: Boolean): FieldInterface
  def addPoints(curPlayerIndex: Int, points: Int): FieldInterface
  def nextPlayer: FieldInterface
  def updatePlayer(curPlayerIndex: Int): FieldInterface
  def checkSquare(squareCase: SquareCase, x: Int, y: Int): FieldInterface
  def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean
  def isFinished: Boolean
  def isEdge(move: Move): Boolean
  def playerList: Vector[Player]
  def currentPlayer: Player
  def currentPlayerIndex: Int
  def getPlayerPoints(playerIndex: Int): Int
  def currentStatus: Vector[Vector[Status]]
  def gameBoardData: GameBoardData
  def fieldData(computerDifficulty: ComputerDifficulty): FieldData
  def playerGameData: PlayerGameData
  def fieldSizeData: FieldSizeData
  def getWinningMoves(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[Move]
  def getSaveMoves(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[Move]
  def getMissingMoves(vec: Int, row: Int, col: Int, field: FieldInterface): Vector[(Int, Int, Int)]
  def chainsWithPointsOutcome(coords: Vector[(Int, Int, Int)], field: FieldInterface): Vector[(Int, Vector[(Int, Int, Int)])]
  override def toString: String
