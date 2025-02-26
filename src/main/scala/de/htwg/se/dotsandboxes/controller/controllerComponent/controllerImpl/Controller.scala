package de.htwg.se.dotsandboxes
package controller
package controllerComponent
package controllerImpl

import Default.given
import model.computerComponent.ComputerInterface
import model.computerComponent.computerEasyImpl.ComputerEasy
import model.computerComponent.computerHardImpl.ComputerHard
import model.computerComponent.computerMediumImpl.ComputerMedium
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.FieldInterface
import model.matrixComponent.matrixImpl.{Status, Player}
import model.fileIoComponent.FileIOInterface
import util.moveState.{EdgeState, MidState}
import util.{Event, Move, MoveStrategy, MoveValidator, PackT, PlayerStrategy, PlayerType, UndoManager, BoardSize, ComputerDifficulty, PlayerSize}
import scala.util.Try
import scala.util.{Failure, Success}

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface, var computer: ComputerInterface) extends ControllerInterface:
  val undoManager = new UndoManager

  override def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, difficulty: ComputerDifficulty): FieldInterface =
    field = new Field(boardSize, Status.Empty, playerSize, playerType)
    computer = if playerSize != PlayerSize.Two && difficulty == ComputerDifficulty.Hard then new ComputerMedium() else getComputerImpl(difficulty)
    notifyObservers(Event.Move)
    field
  override def getComputerImpl(difficulty: ComputerDifficulty): ComputerInterface = difficulty match
    case ComputerDifficulty.Easy   => new ComputerEasy()
    case ComputerDifficulty.Medium => new ComputerMedium()
    case ComputerDifficulty.Hard   => new ComputerHard()
  override def getComputerDifficulty(computer: ComputerInterface): ComputerDifficulty = computer match
    case _: ComputerEasy   => ComputerDifficulty.Easy
    case _: ComputerMedium => ComputerDifficulty.Medium
    case _: ComputerHard   => ComputerDifficulty.Hard
  override def put(move: Move): FieldInterface = undoManager.doStep(field, PutCommand(move, field))
  override def getStatusCell(row: Int, col: Int): Status = field.getStatusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = field.getRowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = field.getColCell(row, col)
  override def restart: FieldInterface = initGame(field.boardSize, field.playerSize, playerType, computerDifficulty)
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
  override def computerDifficulty: ComputerDifficulty = getComputerDifficulty(computer)
  override def boardSize: BoardSize = field.boardSize
  override def playerSize: PlayerSize = field.playerSize
  override def playerType: PlayerType = field.playerType
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
        if gameEnded then notifyObservers(Event.End); Success(field)
        if field.currentPlayer.playerType == PlayerType.Computer then computerMove(field)
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

  override def computerMove(field: FieldInterface): FieldInterface =
    val moveOption: Option[Move] = computer.calculateMove(field)
    moveOption match
      case Some(move) =>
        publish(put, move) match
          case Success(updatedField) => updatedField
          case Failure(_)            => field
      case None => field

  override def toString: String =
    def moveString: String = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n\n${field.toString}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
