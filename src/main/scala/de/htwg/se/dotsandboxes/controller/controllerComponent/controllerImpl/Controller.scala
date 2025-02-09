package de.htwg.se.dotsandboxes
package controller
package controllerComponent
package controllerImpl

import Default.given
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import model.fieldComponent.FieldInterface
import model.fieldComponent.fieldImpl.Move
import model.fileIoComponent.FileIOInterface
import model.matrixComponent.matrixImpl.Player
import scala.util.Try
import scala.util.{Failure, Success}
import util._
import util.moveState.{EdgeState, MidState}

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface) extends ControllerInterface:
  /* setup chain */
  val moveCheck_Available = CheckAvailable(None)
  val moveCheck_Y = CheckY(Some(moveCheck_Available))
  val moveCheck_X = CheckX(Some(moveCheck_Y))
  val moveCheck_Line = CheckLine(Some(moveCheck_X))

  /* setup undo manager */
  val undoManager = new UndoManager

  override def put(move: Move): FieldInterface = undoManager.doStep(field, PutCommand(move, field))
  override def getStatusCell(row: Int, col: Int): Status = field.getStatusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = field.getRowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = field.getColCell(row, col)
  override def undo: FieldInterface = undoManager.undoStep(field)
  override def redo: FieldInterface = undoManager.redoStep(field)
  override def save: FieldInterface =
    fileIO.save(field)
    if !gameEnded then notifyObservers(Event.Move)
    field
  override def load: FieldInterface =
    field = fileIO.load
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field

  override def colSize(): Int = field.colSize()
  override def rowSize(): Int = field.rowSize()
  override def playerList: Vector[Player] = field.playerList
  override def currentPlayer: String = field.currentPlayerId
  override def currentPoints: Int = field.currentPoints
  override def gameEnded: Boolean = field.isFinished
  override def winner: String = field.winner
  override def stats: String = field.stats

  override def publish(doThis: => FieldInterface): FieldInterface =
    field = doThis
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field
  override def publish(doThis: Move => FieldInterface, move: Move): Try[FieldInterface] =
    moveCheck_Line.handle(move, field) match
      case Failure(exception) =>
        print(exception.getMessage.dropRight(28))
        Failure(exception)
      case Success(value) =>
        field = doThis(move)
        val preStatus = field.currentStatus
        val movePosition = if field.isEdge(move) then EdgeState else MidState
        field = MoveStratagy.executeStrategy(movePosition, move, field)
        val postStatus = field.currentStatus
        field = PlayerStratagy.updatePlayer(field, preStatus, postStatus)
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End)
        Success(field)

  override def toString: String =
    def moveString: String = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n\n${field.toString}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
