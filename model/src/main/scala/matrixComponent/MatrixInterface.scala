package matrixComponent

import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, SquareCase, Status, Player, Move}

trait MatrixInterface:
  val vectorStatus: Vector[Vector[Status]]
  val vectorRow: Vector[Vector[Boolean]]
  val vectorCol: Vector[Vector[Boolean]]
  val maxPosX: Int
  val maxPosY: Int
  def rowSize(): Int
  def colSize(): Int
  def getPlayerSize: PlayerSize
  def getBoardSize: BoardSize
  def statusCell(row: Int, col: Int): Status
  def rowCell(row: Int, col: Int): Boolean
  def colCell(row: Int, col: Int): Boolean
  def replaceStatusCell(row: Int, col: Int, status: Status): MatrixInterface
  def replaceRowCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def replaceColCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def getUnoccupiedRowCoord(): Vector[(Int, Int, Int)]
  def getUnoccupiedColCoord(): Vector[(Int, Int, Int)]
  def checkSquare(squareCase: SquareCase, x: Int, y: Int): MatrixInterface
  def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean]
  def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)]
  def isEdge(move: Move): Boolean
  def getCurrentPlayer: Player
  def currentPlayerInfo: (String, Int)
  def currentPoints: Int
  def updatePlayer(curPlayerIndex: Int): MatrixInterface
  def playerIndex: Int
  def playerList: Vector[Player]
  def getPoints(index: Int): Int
  def addPoints(curPlayerIndex: Int, points: Int): MatrixInterface
  def changePlayer: MatrixInterface
