package controllerComponent
package controllerImpl

import api.service.{ComputerRequestHttp, ModelRequestHttp, PersistenceRequestHttp}
import common.config.ServiceConfig.COMPUTER_SLEEP_TIME
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import common.persistence.fileIOService.serializer.FileIOSerializer
import controllerImpl.command.{PutCommand, UndoManager}
import controllerImpl.moveHandler.MoveValidator
import controllerImpl.moveStrategy.{EdgeState, MidState, MoveStrategy}
import controllerImpl.playerStrategy.PlayerStrategy
import de.github.dotsandboxes.lib._
import fieldComponent.parser.FieldParser
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class Controller(using var field: FieldInterface, var fileFormat: FileFormat, var computerDifficulty: ComputerDifficulty) extends ControllerInterface:
  private val undoManager = new UndoManager

  def newField(boardSize: BoardSize, status: Status, playerSize: PlayerSize, playerType: PlayerType): FieldInterface =
    FieldParser.fromJson(ModelRequestHttp.newField(boardSize, Status.Empty, playerSize, playerType, field))

  def fieldString: String = ModelRequestHttp.gameData("asString", field)
  def currentPlayer: Player = ModelRequestHttp.currentPlayer(field)
  def currentStatus: Vector[Vector[Status]] = ModelRequestHttp.currentStatus(field)
  def isEdge(move: Move): Boolean = ModelRequestHttp.isEdge(move, field)

  override def fieldData: FieldData = ModelRequestHttp.fieldData(computerDifficulty, field)
  override def gameBoardData: GameBoardData = ModelRequestHttp.gameBoardData(field)
  override def playerGameData: PlayerGameData = ModelRequestHttp.playerGameData(field)
  override def fieldSizeData: FieldSizeData = ModelRequestHttp.fieldSizeData(field)
  override def gameEnded: Boolean = ModelRequestHttp.gameData("gameEnded", field).toBoolean

  override def put(move: Move): String = undoManager.doStep(field, PutCommand(move, field))
  override def undo: FieldInterface = undoManager.undoStep(field)
  override def redo: FieldInterface = undoManager.redoStep(field)
  override def save: FieldInterface =
    PersistenceRequestHttp.saveFileIO(FileIOSerializer.serialize(field, fileFormat), fileFormat)
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

  override def restart: FieldInterface =
    val data: FieldData = fieldData
    initGame(data.boardSize, data.playerSize, data.playerType, computerDifficulty)
  override def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, difficulty: ComputerDifficulty): FieldInterface =
    if playerType == PlayerType.Computer then ComputerRequestHttp.preConnect
    field = newField(boardSize, Status.Empty, playerSize, playerType)
    computerDifficulty = if playerSize != PlayerSize.Two && difficulty == ComputerDifficulty.Hard then ComputerDifficulty.Medium else difficulty
    notifyObservers(Event.Move)
    field

  override def publish(doThis: => FieldInterface): FieldInterface =
    field = doThis
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field
  override def publish(doThis: Move => String, move: Move): Try[FieldInterface] =
    MoveValidator.validate(move, field) match
      case Failure(exception) => Failure(exception)
      case Success(_) =>
        field = FieldParser.fromJson(doThis(move))
        val preStatus = currentStatus
        val movePosition = if isEdge(move) then EdgeState else MidState
        field = FieldParser.fromJson(MoveStrategy.executeStrategy(movePosition, move, field))
        val postStatus = currentStatus
        field = FieldParser.fromJson(PlayerStrategy.updatePlayer(field, preStatus, postStatus))
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End); Success(field)
        if !gameEnded && currentPlayer.playerType == PlayerType.Computer then computerMove(field)
        Success(field)

  override def computerMove(field: FieldInterface): Future[FieldInterface] =
    Future {
      Thread.sleep(COMPUTER_SLEEP_TIME)
      val move: Move = ComputerRequestHttp.calculateMove(
        FieldConverter.toJson(field).toString,
        computerDifficulty
      )
      publish(put, move) match
        case Success(updatedField) => updatedField
        case Failure(_)            => field
    }

  override def toString: String =
    val currPlayer: Player = currentPlayer
    val moveString = if !gameEnded then "Your Move <Line><X><Y>: " else ""
    s"\n$fieldString\n${currPlayer.playerId}s turn\n[points: ${currPlayer.points}]\n\n$moveString"
