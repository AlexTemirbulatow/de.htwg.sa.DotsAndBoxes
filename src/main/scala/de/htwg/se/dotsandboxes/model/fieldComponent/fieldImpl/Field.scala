package de.htwg.se.dotsandboxes.model
package fieldComponent.fieldImpl

import fieldComponent.FieldInterface
import matrixComponent.MatrixInterface
import matrixComponent.matrixImpl.{Matrix, Player}
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.moveState.SquareState

case class Field(matrix: MatrixInterface) extends FieldInterface:
  def this(rowSize: Int, colSize: Int, status: Status, playerSize: Int = 2) = this(new Matrix(rowSize, colSize, status, playerSize))
  override def bar(length: Int = 7, cellNum: Int = 5, rowIndex: Int): String = (0 until cellNum).map(rows(rowIndex, _, length)).mkString(Connectors("O"), Connectors("O"), Connectors("O")) + "\n"
  override def cells(rowSize: Int, length: Int = 7, height: Int = 2): String =
    ((0 to maxPosY).map(columns(rowSize, _, length)).mkString + "\n") * height
  override def mesh(length: Int = 7, height: Int = 2): String =
    ((0 until maxPosX)
    .map(x => bar(length, maxPosY, x) + cells(x, length, height))).mkString + bar(length, maxPosY, maxPosX)
  override def rows(rowIndex: Int, colIndex: Int, length: Int): String = getRowCell(rowIndex, colIndex) match
    case false => Connectors("-") * length
    case true  => Connectors("=") * length
  override def columns(rowIndex: Int, colIndex: Int, length: Int): String = getColCell(rowIndex, colIndex) match
    case false => Connectors("¦") + status(rowIndex, colIndex, length)
    case true  => Connectors("‖") + status(rowIndex, colIndex, length)
  override def status(rowIndex: Int, colIndex: Int, length: Int): String = (colIndex < maxPosY) match
    case false => Connectors("")
    case true  => space(length) + getStatusCell(rowIndex, colIndex) + space(length)
  override def winner: String = if (playerList.indices.map(playerList(_).points).count(_ == playerList.maxBy(_._2).points) > 1) "It's a draw!"
  else s"Player ${playerList.maxBy(_._2).playerId} wins!"
  override def stats: String = playerList.indices.map(x => s"Player ${playerList(x).playerId} [points: ${playerList(x).points}]").mkString("\n")
  override def getStatusCell(row: Int, col: Int): Status = matrix.statusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = matrix.rowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = matrix.colCell(row, col)
  override def putStatus(row: Int, col: Int, status: Status): Field = copy(matrix.replaceStatusCell(row, col, status))
  override def putRow(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceRowCell(row, col, value))
  override def putCol(row: Int, col: Int, value: Boolean): Field = copy(matrix.replaceColCell(row, col, value))
  override def isFinished: Boolean = (matrix.vectorRow ++ matrix.vectorCol).forall(_.forall(_.equals(true)))
  override def isEdge(move: Move): Boolean = matrix.isEdge(move)
  override def checkSquare(squareCase: SquareState, x: Int, y: Int): Field = copy(matrix.checkSquare(squareCase, x, y))
  override def currentPlayerId: String = matrix.currentPlayerInfo._1
  override def currentPlayerIndex: Int = matrix.currentPlayerInfo._2
  override def currentStatus: Vector[Vector[Status]] = matrix.vectorStatus
  override def currentPoints: Int = matrix.currentPoints
  override def nextPlayer: Field = copy(matrix.changePlayer)
  override def updatePlayer(curPlayerIndex: Int = playerIndex): Field = copy(matrix.updatePlayer(curPlayerIndex))
  override def playerIndex: Int = matrix.playerIndex
  override def addPoints(curPlayerIndex: Int = playerIndex, points: Int): Field = copy(matrix.addPoints(curPlayerIndex, points))
  override def playerList: Vector[Player] = matrix.playerList
  override def getPoints(index: Int): Int = matrix.getPoints(index)
  override def rowSize(): Int = matrix.rowSize()
  override def colSize(): Int = matrix.colSize()
  override def space(length: Int): String = " " * ((length - 1) / 2)
  override val maxPosX = matrix.maxPosX
  override val maxPosY = matrix.maxPosY
  override def toString = mesh()
