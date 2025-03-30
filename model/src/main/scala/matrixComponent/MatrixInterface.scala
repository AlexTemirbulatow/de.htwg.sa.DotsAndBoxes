package matrixComponent

import de.github.dotsandboxes.lib._

trait MatrixInterface:
  val vectorStatus: Vector[Vector[Status]]
  val vectorRow: Vector[Vector[Boolean]]
  val vectorCol: Vector[Vector[Boolean]]
  val maxPosX: Int
  val maxPosY: Int
  def rowSize: Int
  def colSize: Int
  def getBoardSize: BoardSize
  def getPlayerSize: PlayerSize
  def getPlayerType: PlayerType
  def getUnoccupiedRowCoords: Vector[(Int, Int, Int)]
  def getUnoccupiedColCoords: Vector[(Int, Int, Int)]
  def statusCell(row: Int, col: Int): Status
  def rowCell(row: Int, col: Int): Boolean
  def colCell(row: Int, col: Int): Boolean
  def replaceStatusCell(row: Int, col: Int, status: Status): MatrixInterface
  def replaceRowCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def replaceColCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def addPoints(curPlayerIndex: Int, points: Int): MatrixInterface
  def changePlayer: MatrixInterface
  def updatePlayer(curPlayerIndex: Int): MatrixInterface
  def checkSquare(squareCase: SquareCase, x: Int, y: Int): MatrixInterface
  def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean]
  def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)]
  def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean
  def isFinished: Boolean
  def isEdge(move: Move): Boolean
  def playerList: Vector[Player]
  def getCurrentPlayer: Player
  def currentPlayerIndex: Int
  def getPlayerPoints(playerIndex: Int): Int
