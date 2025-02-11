package de.htwg.se.dotsandboxes
package controller.controllerComponent

import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import model.fieldComponent.FieldInterface
import util.Move
import model.matrixComponent.matrixImpl.Player
import scala.util.Try
import util.Observable

trait ControllerInterface extends Observable:
  def put(move: Move): FieldInterface
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def undo: FieldInterface
  def redo: FieldInterface
  def save: FieldInterface
  def load: FieldInterface
  def colSize(): Int
  def rowSize(): Int
  def playerList: Vector[Player]
  def currentPlayer: String
  def currentPoints: Int
  def gameEnded: Boolean
  def winner: String
  def stats: String
  def publish(doThis: => FieldInterface): FieldInterface
  def publish(doThis: Move => FieldInterface, move: Move): Try[FieldInterface]
  override def toString: String
