package de.htwg.se.dotsandboxes.model
package matrixComponent

import de.htwg.se.dotsandboxes.util.Move
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.moveState.SquareState

trait MatrixInterface:
  val vectorStatus: Vector[Vector[Status]]
  val vectorRow: Vector[Vector[Boolean]]
  val vectorCol: Vector[Vector[Boolean]]
  val maxPosX: Int
  val maxPosY: Int
  def rowSize(): Int
  def colSize(): Int
  def statusCell(row: Int, col: Int): Status
  def rowCell(row: Int, col: Int): Boolean
  def colCell(row: Int, col: Int): Boolean
  def replaceStatusCell(row: Int, col: Int, status: Status): MatrixInterface
  def replaceRowCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def replaceColCell(row: Int, col: Int, value: Boolean): MatrixInterface
  def checkSquare(squareCase: SquareState, x: Int, y: Int): MatrixInterface
  def isEdge(move: Move): Boolean
  def currentPlayerInfo: (String, Int)
  def currentPoints: Int
  def updatePlayer(curPlayerIndex: Int): MatrixInterface
  def playerIndex: Int
  def playerList: Vector[matrixImpl.Player]
  def getPoints(index: Int): Int
  def addPoints(curPlayerIndex: Int, points: Int): MatrixInterface
  def changePlayer: MatrixInterface
