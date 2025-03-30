package matrixComponent.matrixImpl

import de.github.dotsandboxes.lib._
import matrixComponent.MatrixInterface

case class Matrix(
  vecStatus: Vector[Vector[Status]],
  vecRow: Vector[Vector[Boolean]],
  vecCol: Vector[Vector[Boolean]],
  list: Vector[Player],
  currentPlayer: Player,
  boardSize: BoardSize,
  playerSize: PlayerSize,
  playerType: PlayerType
) extends MatrixInterface:
  
  def this(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType) =
    this(
      Vector.tabulate(boardSize.dimensions._2, boardSize.dimensions._1) { (_, _) => status },
      Vector.tabulate(boardSize.dimensions._2 + 1, boardSize.dimensions._1) { (_, _) => false },
      Vector.tabulate(boardSize.dimensions._2, boardSize.dimensions._1 + 1) { (_, _) => false },
      new PlayerList(playerSize.size, playerType).playerList,
      list.head,
      boardSize,
      playerSize,
      playerType
    )

  override val vectorStatus: Vector[Vector[Status]] = vecStatus
  override val vectorRow: Vector[Vector[Boolean]] = vecRow
  override val vectorCol: Vector[Vector[Boolean]] = vecCol
  override val maxPosX: Int = rowSize - 1
  override val maxPosY: Int = colSize - 1
  override def rowSize: Int = vecRow.size
  override def colSize: Int = vecCol(0).size

  override def getBoardSize: BoardSize = boardSize
  override def getPlayerSize: PlayerSize = playerSize
  override def getPlayerType: PlayerType = playerType

  override def getUnoccupiedRowCoord: Vector[(Int, Int, Int)] =
    for
      (row, x) <- vecRow.zipWithIndex
      (cell, y) <- row.zipWithIndex
      if !cell
    yield (1, x, y)
  override def getUnoccupiedColCoord: Vector[(Int, Int, Int)] =
    for
      (col, x) <- vecCol.zipWithIndex
      (cell, y) <- col.zipWithIndex
      if !cell
    yield (2, x, y)

  override def statusCell(row: Int, col: Int): Status = vecStatus(row)(col)
  override def rowCell(row: Int, col: Int): Boolean = vecRow(row)(col)
  override def colCell(row: Int, col: Int): Boolean = vecCol(row)(col)

  override def replaceStatusCell(row: Int, col: Int, status: Status): Matrix =
    copy(vecStatus = vecStatus.updated(row, vecStatus(row).updated(col, status)))
  override def replaceRowCell(row: Int, col: Int, value: Boolean): Matrix =
    copy(vecRow = vecRow.updated(row, vecRow(row).updated(col, value)))
  override def replaceColCell(row: Int, col: Int, value: Boolean): Matrix =
    copy(vecCol = vecCol.updated(row, vecCol(row).updated(col, value)))

  override def addPoints(curPlayerIndex: Int, points: Int): Matrix = copy(list = list.updated(curPlayerIndex, list(curPlayerIndex).copy(points = list(curPlayerIndex).points + points)))
  override def changePlayer: Matrix = if (playerIndex == list.size - 1) copy(currentPlayer = list.head) else copy(currentPlayer = list(playerIndex + 1))
  override def updatePlayer(curPlayerIndex: Int): Matrix = copy(currentPlayer = list(curPlayerIndex))

  override def checkSquare(squareCase: SquareCase, x: Int, y: Int): Matrix =
    if checkAllCells(squareCase, x, y).forall(identity) then
      val (newX, newY) = squareCase match
        case SquareCase.UpCase   => (x - 1, y)
        case SquareCase.LeftCase => (x, y - 1)
        case _                    => (x, y)
      replaceStatusCell(newX, newY, currentPlayer.status)
    else copy()
  override def checkAllCells(squareCase: SquareCase, x: Int, y: Int): Vector[Boolean] =
    cellsToCheck(squareCase, x, y).map {
    case (1, row, col) => rowCell(row, col)
    case (2, row, col) => colCell(row, col)
  }
  override def cellsToCheck(squareCase: SquareCase, x: Int, y: Int): Vector[(Int, Int, Int)] = squareCase match
    case SquareCase.DownCase  => Vector((1, x + 1, y), (2, x, y), (2, x, y + 1))
    case SquareCase.UpCase    => Vector((1, x - 1, y), (2, x - 1, y), (2, x - 1, y + 1))
    case SquareCase.RightCase => Vector((2, x, y + 1), (1, x, y), (1, x + 1, y))
    case SquareCase.LeftCase  => Vector((2, x, y - 1), (1, x, y - 1), (1, x + 1, y - 1))
  
  override def isCircularSequence(moveSeq1: (Int, Vector[(Int, Int, Int)]), moveSeq2: (Int, Vector[(Int, Int, Int)])): Boolean =
    moveSeq1._1 == moveSeq2._1 && moveSeq1._2.toSet == moveSeq2._2.toSet
  override def isFinished: Boolean = (vecRow ++ vecCol).forall(_.forall(_.equals(true)))
  override def isEdge(move: Move): Boolean = move.vec match
    case 1 => move.x == 0 || move.x == maxPosX
    case 2 => move.y == 0 || move.y == maxPosY

  override def playerList: Vector[Player] = list
  override def getCurrentPlayer: Player = currentPlayer
  override def currentPlayerInfo: (String, Int) = (currentPlayer.playerId, list.indexOf(currentPlayer))
  override def currentPoints: Int = currentPlayer.points
  override def playerIndex: Int = list.indices.map(x => list(x).playerId).indexOf(currentPlayer.playerId)
  override def getPoints(index: Int): Int = list(index).points
