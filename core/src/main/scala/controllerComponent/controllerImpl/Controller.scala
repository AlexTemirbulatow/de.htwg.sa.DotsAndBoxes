package controllerComponent
package controllerImpl

import api.service.{ComputerRequestHttp, ModelRequestHttp, PersistenceRequestHttp}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import common.persistence.fileIOService.serializer.FileIOSerializer
import controllerImpl.command.{PutCommand, UndoManager}
import controllerImpl.moveHandler.MoveValidator
import controllerImpl.moveStrategy.{EdgeState, MidState, MoveStrategy}
import controllerImpl.playerStrategy.PlayerStrategy
import de.github.dotsandboxes.lib.{BoardSize, CellData, ComputerDifficulty, Event, FileFormat, Move, Player, PlayerSize, PlayerType, Status}
import fieldComponent.parser.FieldParser
import org.slf4j.LoggerFactory
import play.api.libs.json.JsObject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Controller(using var field: FieldInterface, var fileFormat: FileFormat, var computerDifficulty: ComputerDifficulty) extends ControllerInterface:
  val undoManager = new UndoManager

  val logger = LoggerFactory.getLogger(getClass)

  override def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, difficulty: ComputerDifficulty): FieldInterface =
    if playerType == PlayerType.Computer then ComputerRequestHttp.connect
    field = FieldParser.fromJson(ModelRequestHttp.newGame(boardSize, Status.Empty, playerSize, playerType, field))
    this.computerDifficulty = difficulty
    notifyObservers(Event.Move)
    field
  override def getCellData: CellData = ModelRequestHttp.cellData(field)
  override def getStatusCell(row: Int, col: Int): Status = ModelRequestHttp.statusCell(row, col, field)
  override def getRowCell(row: Int, col: Int): Boolean = ModelRequestHttp.rowCell(row, col, field)
  override def getColCell(row: Int, col: Int): Boolean = ModelRequestHttp.colCell(row, col, field)
  override def colSize(): Int = ModelRequestHttp.colSize(field)
  override def rowSize(): Int = ModelRequestHttp.rowSize(field)

  override def put(move: Move): String = undoManager.doStep(field, PutCommand(move, field))
  override def restart: FieldInterface = initGame(boardSize, playerSize, playerType, computerDifficulty)
  override def undo: FieldInterface = undoManager.undoStep(field)
  override def redo: FieldInterface = undoManager.redoStep(field)
  override def save: FieldInterface =
    PersistenceRequestHttp.saveFileIO(
      FileIOSerializer.serialize(field, fileFormat),
      fileFormat
    )
    if !gameEnded then notifyObservers(Event.Move)
    field
  override def load: FieldInterface =
    val fieldValue: String = PersistenceRequestHttp.loadFileIO(fileFormat)
    field = fileFormat match
      case FileFormat.JSON => FieldParser.fromJson(fieldValue)
      case FileFormat.XML  => FieldParser.fromXml(fieldValue)
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field

  override def getComputerDifficulty: ComputerDifficulty = computerDifficulty
  override def boardSize: BoardSize = ModelRequestHttp.boardSize(field)
  override def playerSize: PlayerSize = ModelRequestHttp.playerSize(field)
  override def playerType: PlayerType = ModelRequestHttp.playerType(field)
  override def playerList: Vector[Player] = ModelRequestHttp.playerList(field)
  override def currentPlayerType: PlayerType = ModelRequestHttp.currentPlayerType(field)
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
        field = FieldParser.fromJson(doThis(move))
        val preStatus = ModelRequestHttp.currentStatus(field)
        val movePosition = if ModelRequestHttp.isEdge(move, field) then EdgeState else MidState
        field = FieldParser.fromJson(MoveStrategy.executeStrategy(movePosition, move, field))
        val postStatus = ModelRequestHttp.currentStatus(field)
        field = FieldParser.fromJson(PlayerStrategy.updatePlayer(field, preStatus, postStatus))
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End); Success(field)
        if !gameEnded && currentPlayerType == PlayerType.Computer then computerMove(field)
        Success(field)
  override def computerMove(field: FieldInterface): Future[FieldInterface] =
    Future {
      Thread.sleep(1000)
      calculateComputerMove(field)
    }
  override def calculateComputerMove(field: FieldInterface): FieldInterface =
    val move: Move = ComputerRequestHttp.calculateMove(
      FieldConverter.toJson(field).toString,
      getComputerDifficulty
    )
    publish(put, move) match
      case Success(updatedField) => updatedField
      case Failure(_)            => field

  override def toString: String =
    val moveString = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n${ModelRequestHttp.gameData("asString", field)}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"
