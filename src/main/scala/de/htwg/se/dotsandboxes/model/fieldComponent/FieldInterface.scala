package de.htwg.se.dotsandboxes.model
package fieldComponent

import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.moveState.SquareState
import matrixComponent.MatrixInterface
import matrixComponent.matrixImpl.Player

trait FieldInterface:
  val maxPosX: Int
  val maxPosY: Int
  def bar(length: Int = 7, cellNum: Int = 5, rowIndex: Int): String
  def cells(rowSize: Int, length: Int = 7, height: Int = 2): String
  def mesh(length: Int = 7, height: Int = 2): String
  def rows(rowIndex: Int, colIndex: Int, length: Int): String
  def columns(rowIndex: Int, colIndex: Int, length: Int): String
  def status(rowIndex: Int, colIndex: Int, length: Int): String
  def winner: String
  def stats: String
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def putStatus(row: Int, col: Int, status: Status): FieldInterface
  def putRow(row: Int, col: Int, value: Boolean): FieldInterface
  def putCol(row: Int, col: Int, value: Boolean): FieldInterface
  def isFinished: Boolean
  def isEdge(move: fieldImpl.Move): Boolean
  def checkSquare(squareCase: SquareState, x: Int, y: Int): FieldInterface
  def currentPlayerId: String
  def currentPlayerIndex: Int
  def currentStatus: Vector[Vector[Status]]
  def currentPoints: Int
  def nextPlayer: FieldInterface
  def updatePlayer(curPlayerIndex: Int = playerIndex): FieldInterface
  def playerIndex: Int
  def addPoints(curPlayerIndex: Int = playerIndex, points: Int): FieldInterface
  def playerList: Vector[Player]
  def getPoints(index: Int): Int
  def rowSize(): Int
  def colSize(): Int
  def space(length: Int): String
  override def toString: String
