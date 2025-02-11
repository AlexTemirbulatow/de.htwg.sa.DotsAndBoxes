package de.htwg.se.dotsandboxes.model
package matrixComponent.matrixImpl

import fieldComponent.fieldImpl.Move
import matrixComponent.MatrixInterface
import de.htwg.se.dotsandboxes.util.moveState.SquareState

case class Matrix(
  vecStatus: Vector[Vector[Status]],
  vecRow: Vector[Vector[Boolean]],
  vecCol: Vector[Vector[Boolean]],
  list: Vector[Player],
  currentPlayer: Player
) extends MatrixInterface:
  
  def this(rowSize: Int, colSize: Int, status: Status, playerSize: Int = 2) =
    this(
      Vector.tabulate(colSize, rowSize) { (_, _) => status },
      Vector.tabulate(colSize + 1, rowSize) { (_, _) => false },
      Vector.tabulate(colSize, rowSize + 1) { (_, _) => false },
      new PlayerList(playerSize).playerList,
      list.head
    )

  override val vectorStatus: Vector[Vector[Status]] = vecStatus
  override val vectorRow: Vector[Vector[Boolean]] = vecRow
  override val vectorCol: Vector[Vector[Boolean]] = vecCol
  override val maxPosX: Int = rowSize() - 1
  override val maxPosY: Int = colSize() - 1
  override def rowSize(): Int =
    vectorRow.size
  override def colSize(): Int =
    vectorCol(0).size
  override def statusCell(row: Int, col: Int): Status = vectorStatus(row)(col)
  override def rowCell(row: Int, col: Int): Boolean = vectorRow(row)(col)
  override def colCell(row: Int, col: Int): Boolean = vectorCol(row)(col)
  override def replaceStatusCell(row: Int, col: Int, status: Status): Matrix =
    copy(vecStatus = vectorStatus.updated(row, vectorStatus(row).updated(col, status)))
  override def replaceRowCell(row: Int, col: Int, value: Boolean): Matrix =
    copy(vecRow = vectorRow.updated(row, vectorRow(row).updated(col, value)))
  override def replaceColCell(row: Int, col: Int, value: Boolean): Matrix =
    copy(vecCol = vectorCol.updated(row, vectorCol(row).updated(col, value)))
  override def checkSquare(squareCase: SquareState, x: Int, y: Int): Matrix =
    val cellsToCheck: List[Boolean] = squareCase match
      case SquareState.DownCase  => List(rowCell(x + 1, y), colCell(x, y), colCell(x, y + 1))
      case SquareState.UpCase    => List(rowCell(x - 1, y), colCell(x - 1, y), colCell(x - 1, y + 1))
      case SquareState.RightCase => List(colCell(x, y + 1), rowCell(x, y), rowCell(x + 1, y))
      case SquareState.LeftCase  => List(colCell(x, y - 1), rowCell(x, y - 1), rowCell(x + 1, y - 1))
    if cellsToCheck.forall(identity) then
      val (newX, newY) = squareCase match
        case SquareState.UpCase  => (x - 1, y)
        case SquareState.LeftCase => (x, y - 1)
        case _                    => (x, y)
      replaceStatusCell(newX, newY, currentPlayer.status)
    else 
      copy()
  override def isEdge(move: Move): Boolean = move.vec match
    case 1 => if (move.x == 0 || move.x == maxPosX) true else false
    case 2 => if (move.y == 0 || move.y == maxPosY) true else false
  override def currentPlayerInfo: (String, Int) = (currentPlayer.playerId, list.indexOf(currentPlayer))
  override def currentPoints: Int = currentPlayer.points
  override def updatePlayer(curPlayerIndex: Int = playerIndex): Matrix = copy(currentPlayer = list(curPlayerIndex))
  override def playerIndex: Int = list.indices.map(x => list(x).playerId).indexOf(currentPlayer.playerId)
  override def playerList: Vector[Player] = list
  override def getMatrix: Matrix = this.asInstanceOf[Matrix]
  override def getPoints(index: Int): Int = list(index).points
  override def addPoints(curPlayerIndex: Int = currentPlayerInfo._2, points: Int): Matrix = copy(list = list.updated(curPlayerIndex, list(curPlayerIndex).copy(points = list(curPlayerIndex).points + points)))
  override def changePlayer: Matrix = if (playerIndex == list.size - 1) copy(currentPlayer = list.head) else copy(currentPlayer = list(playerIndex + 1))
