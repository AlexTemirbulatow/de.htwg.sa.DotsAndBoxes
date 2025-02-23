package de.htwg.se.dotsandboxes
package controller.controllerComponent

import de.htwg.se.dotsandboxes.model.computerComponent.ComputerInterface
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import de.htwg.se.dotsandboxes.util.ComputerDifficulty
import de.htwg.se.dotsandboxes.util.{BoardSize, PackT, PlayerSize, PlayerType}
import model.fieldComponent.FieldInterface
import model.matrixComponent.matrixImpl.Player
import scala.util.Try
import util.Move
import util.Observable

trait ControllerInterface extends Observable:
  def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, computerDifficulty: ComputerInterface): FieldInterface
  def put(move: Move): FieldInterface
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def restart: FieldInterface
  def undo: FieldInterface
  def redo: FieldInterface
  def save: FieldInterface
  def load: FieldInterface
  def colSize(): Int
  def rowSize(): Int
  def computerImpl: ComputerInterface
  def boardSize: BoardSize
  def playerSize: PlayerSize
  def playerType: PlayerType
  def playerList: Vector[Player]
  def currentPlayer: String
  def currentPoints: Int
  def gameEnded: Boolean
  def winner: String
  def stats: String
  def publish(doThis: => FieldInterface): FieldInterface
  def publish(doThis: Move => FieldInterface, move: Move): Try[FieldInterface]
  def publishCheat(doThis: Move => FieldInterface, pack: PackT[Option[Move]]): Try[FieldInterface]
  def computerMove(field: FieldInterface): FieldInterface
  override def toString: String
