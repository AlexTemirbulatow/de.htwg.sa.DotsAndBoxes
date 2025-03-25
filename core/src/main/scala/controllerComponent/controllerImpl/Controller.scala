package controllerComponent
package controllerImpl

import api.service.ModelRequestHttp
import computerComponent.ComputerInterface
import computerComponent.computerEasyImpl.ComputerEasy
import computerComponent.computerHardImpl.ComputerHard
import computerComponent.computerMediumImpl.ComputerMedium
import controllerImpl.command.{PutCommand, UndoManager}
import controllerImpl.moveHandler.MoveValidator
import controllerImpl.moveStrategy.{EdgeState, MidState, MoveStrategy}
import controllerImpl.playerStrategy.PlayerStrategy
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, Event, Move, Player, PlayerSize, PlayerType, Status, CellData}
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import fileIoComponent.FileIOInterface
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface, var computer: ComputerInterface) extends ControllerInterface:
  val undoManager = new UndoManager

  val logger = LoggerFactory.getLogger(getClass)

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
  override def getCellData: CellData = ModelRequestHttp.cellData(field)
  override def getStatusCell(row: Int, col: Int): Status = ModelRequestHttp.statusCell(row, col, field)
  override def getRowCell(row: Int, col: Int): Boolean = ModelRequestHttp.rowCell(row, col, field)
  override def getColCell(row: Int, col: Int): Boolean = ModelRequestHttp.colCell(row, col, field)

  override def put(move: Move): String = undoManager.doStep(field, PutCommand(move, field))
  override def restart: FieldInterface = initGame(
    ModelRequestHttp.boardSize(field),
    ModelRequestHttp.playerSize(field),
    playerType,
    computerDifficulty
  )
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

  override def colSize(): Int = ModelRequestHttp.colSize(field)
  override def rowSize(): Int = ModelRequestHttp.rowSize(field)
  override def computerDifficulty: ComputerDifficulty = getComputerDifficulty(computer)
  override def boardSize: BoardSize = ModelRequestHttp.boardSize(field)
  override def playerSize: PlayerSize = ModelRequestHttp.playerSize(field)
  override def playerType: PlayerType = ModelRequestHttp.playerType(field)
  override def playerList: Vector[Player] = ModelRequestHttp.playerList(field)
  override def currentPlayer: String = ModelRequestHttp.gameData("currentPlayer", field)
  override def currentPoints: Int = ModelRequestHttp.gameData("currentPoints", field).toInt
  override def gameEnded: Boolean = ModelRequestHttp.gameData("gameEnded", field).toBoolean
  override def winner: String = ModelRequestHttp.gameData("winner", field)
  override def stats: String = ModelRequestHttp.gameData("stats", field)

  override def publish(doThis: => FieldInterface): FieldInterface =
    field = doThis
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field
  override def publish(doThis: Move => String, move: Move): Try[FieldInterface] =
    MoveValidator.validate(move, field) match
      case Failure(exception) =>
        logger.error(exception.getMessage.dropRight(28))
        Failure(exception)
      case Success(_) =>
        field = field.fromJson(doThis(move))
        val preStatus = ModelRequestHttp.currentStatus(field)
        val movePosition = if ModelRequestHttp.isEdge(move, field) then EdgeState else MidState
        field = field.fromJson(MoveStrategy.executeStrategy(movePosition, move, field))
        val postStatus = ModelRequestHttp.currentStatus(field)
        field = field.fromJson(PlayerStrategy.updatePlayer(field, preStatus, postStatus))
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End); Success(field)
        if !gameEnded && ModelRequestHttp.currentPlayerType(field) == PlayerType.Computer then computerMove(field)
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
    s"\n${ModelRequestHttp.gameData("asString", field)}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
