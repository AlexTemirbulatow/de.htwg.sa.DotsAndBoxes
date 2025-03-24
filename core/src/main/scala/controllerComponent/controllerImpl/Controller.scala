package controllerComponent
package controllerImpl

import computerComponent.ComputerInterface
import computerComponent.computerEasyImpl.ComputerEasy
import computerComponent.computerHardImpl.ComputerHard
import computerComponent.computerMediumImpl.ComputerMedium
import controllerImpl.command.{PutCommand, UndoManager}
import controllerImpl.moveHandler.MoveValidator
import controllerImpl.moveStrategy.{EdgeState, MidState, MoveStrategy}
import controllerImpl.playerStrategy.PlayerStrategy
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, Event, Move, Player, PlayerSize, PlayerType, Status}
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Try, Failure, Success}

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface, var computer: ComputerInterface) extends ControllerInterface:
  given ExecutionContext = ExecutionContext.global
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
  override def getStatusCell(row: Int, col: Int): Status = field.getStatusCell(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = field.getRowCell(row, col)
  override def getColCell(row: Int, col: Int): Boolean = field.getColCell(row, col)

  override def put(move: Move): FieldInterface = undoManager.doStep(field, PutCommand(move, field))
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
        if !gameEnded && field.currentPlayer.playerType == PlayerType.Computer then computerMove(field)
        Success(field)
  override def computerMove(field: FieldInterface): Future[FieldInterface] =
    Future {
      Thread.sleep(1000)
      calculateComputerMove(field)
    }
  override def calculateComputerMove(field: FieldInterface): FieldInterface =
    val moveOption: Option[Move] = computer.calculateMove(field)
    moveOption match
      case Some(move) =>
        publish(put, move) match
          case Success(updatedField) => updatedField
          case Failure(_)            => field
      case None => field

  override def toString: String =
    def moveString: String = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n${field.toString}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
