package de.htwg.se.dotsandboxes
package controller
package controllerComponent
package controllerImpl

import Default.given
import de.htwg.se.dotsandboxes.model.matrixComponent.matrixImpl.Status
import model.fieldComponent.FieldInterface
import model.fileIoComponent.FileIOInterface
import model.matrixComponent.matrixImpl.Player
import scala.util.Try
import scala.util.{Failure, Success}
import util.moveState.{EdgeState, MidState}
import util.{Event, Move, MoveStrategy, MoveValidator, PackT, PlayerStrategy, UndoManager}

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface) extends ControllerInterface:
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
    MoveValidator.validate(move, field) match
      case Failure(exception) =>
        print(exception.getMessage.dropRight(28))
        Failure(exception)
      case Success(_) =>
        field = doThis(move)
        val preStatus = field.currentStatus
        val movePosition = if field.isEdge(move) then EdgeState else MidState
        field = MoveStrategy.executeStrategy(movePosition, move, field)
        val postStatus = field.currentStatus
        field = PlayerStrategy.updatePlayer(field, preStatus, postStatus)
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End)
        Success(field)
  override def publishCheat(doThis: Move => FieldInterface, pack: PackT[Option[Move]]): Try[FieldInterface] =
    val results = for {
      (moveOpt, index) <- pack.moves.zipWithIndex
      isLast = index == pack.moves.length - 1
    } yield moveOpt match {
      case Some(move) =>
        MoveValidator.validate(move, field) match {
          case Failure(exception) =>
            print(exception.getMessage.dropRight(28))
            Failure(exception)
          case Success(_) =>
            field = doThis(move)
            val preStatus = field.currentStatus
            val movePosition = if field.isEdge(move) then EdgeState else MidState
            field = MoveStrategy.executeStrategy(movePosition, move, field)
            val postStatus = field.currentStatus
            if isLast then
              field = PlayerStrategy.updatePlayer(field, preStatus, postStatus)
              notifyObservers(Event.Move)
            if gameEnded then notifyObservers(Event.End)
            Success(field)
        }
      case None =>
        println(s"Invalid move at position ${index + 1}")
        Failure(new Exception(s"Found None at index $index"))
    }
    results.find(_.isFailure).getOrElse(results.lastOption.getOrElse(Failure(new Exception("No valid moves found"))))

  override def toString: String =
    def moveString: String = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n\n${field.toString}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
