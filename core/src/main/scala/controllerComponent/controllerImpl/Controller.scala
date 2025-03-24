package controllerComponent
package controllerImpl

import akka.actor.ActorSystem
import akka.actor.CoordinatedShutdown
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
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
import io.circe.generic.auto._
import io.circe.parser.decode
import java.net.{HttpURLConnection, URL}
import java.nio.charset.StandardCharsets
import org.slf4j.LoggerFactory
import play.api.libs.json.JsLookupResult
import play.api.libs.json.{JsObject, Json}
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try, Using}
import api.util.ModelRequest

class Controller(using var field: FieldInterface, val fileIO: FileIOInterface, var computer: ComputerInterface) extends ControllerInterface:
  val MODEL_HOST = "localhost"
  val MODEL_PORT = "8080"
  val MODEL_BASE_URL = s"http://$MODEL_HOST:$MODEL_PORT/"

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
  override def getStatusCell(row: Int, col: Int): Status = fieldStatusCellHttp(row, col)
  override def getRowCell(row: Int, col: Int): Boolean = fieldRowCellHttp(row, col)
  override def getColCell(row: Int, col: Int): Boolean = fieldColCellHttp(row, col)

  override def put(move: Move): String = undoManager.doStep(field, PutCommand(move, field))
  override def restart: FieldInterface = initGame(fieldBoardSizeHttp, fieldPlayerSizeHttp, playerType, computerDifficulty)
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

  override def colSize(): Int = fieldColSizeHttp
  override def rowSize(): Int = fieldRowSizeHttp
  override def computerDifficulty: ComputerDifficulty = getComputerDifficulty(computer)
  override def boardSize: BoardSize = fieldBoardSizeHttp
  override def playerSize: PlayerSize = fieldPlayerSizeHttp
  override def playerType: PlayerType = fieldPlayerTypeHttp
  override def playerList: Vector[Player] = fieldPlayerListHttp
  override def currentPlayer: String = fieldGameDataHttp("currentPlayer")
  override def currentPoints: Int = fieldGameDataHttp("currentPoints").toInt
  override def gameEnded: Boolean = fieldGameDataHttp("gameEnded").toBoolean
  override def winner: String = fieldGameDataHttp("winner")
  override def stats: String = fieldGameDataHttp("stats")

  override def publish(doThis: => FieldInterface): FieldInterface =
    field = doThis
    notifyObservers(Event.Move)
    if gameEnded then notifyObservers(Event.End)
    field
  override def publish(doThis: Move => String, move: Move): Try[FieldInterface] =
    MoveValidator.validate(move, field) match
      case Failure(exception) =>
        print(exception.getMessage.dropRight(28))
        Failure(exception)
      case Success(_) =>
        field = field.fromJson(doThis(move))
        val preStatus = fieldCurrentStatusHttp
        val movePosition = if fieldIsEdgeHttp(move) then EdgeState else MidState
        field = field.fromJson(MoveStrategy.executeStrategy(movePosition, move, field))
        val postStatus = fieldCurrentStatusHttp
        field = field.fromJson(PlayerStrategy.updatePlayer(field, preStatus, postStatus))
        notifyObservers(Event.Move)
        if gameEnded then notifyObservers(Event.End); Success(field)
        if !gameEnded && fieldCurrentPlayerTypeHttp == PlayerType.Computer then computerMove(field)
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
    s"\n${{ fieldGameDataHttp("asString") }}\n${currentPlayer}s turn\n[points: ${currentPoints}]\n\n${moveString}"

  def fieldGameDataHttp(data: String): String =
    Await.result(ModelRequest.getRequest(s"api/field/get/$data"), 5.seconds)

  def fieldPlayerListHttp: Vector[Player] =
    decode[Vector[Player]](Await.result(ModelRequest.getRequest("api/field/get/playerList"), 5.seconds))
      .getOrElse(Vector.empty)

  def fieldCurrentStatusHttp: Vector[Vector[Status]] =
    decode[Vector[Vector[Status]]](Await.result(ModelRequest.getRequest("api/field/get/currentStatus"), 5.seconds))
      .getOrElse(Vector.empty)

  def fieldBoardSizeHttp: BoardSize =
    Try(BoardSize.valueOf(Await.result(ModelRequest.getRequest("api/field/get/boardSize"), 5.seconds))).getOrElse(BoardSize.Medium)

  def fieldPlayerSizeHttp: PlayerSize =
    Try(PlayerSize.valueOf(Await.result(ModelRequest.getRequest("api/field/get/playerSize"), 5.seconds))).getOrElse(PlayerSize.Two)

  def fieldPlayerTypeHttp: PlayerType =
    Try(PlayerType.valueOf(Await.result(ModelRequest.getRequest("api/field/get/playerType"), 5.seconds))).getOrElse(PlayerType.Human)

  def fieldCurrentPlayerTypeHttp: PlayerType =
    Try(PlayerType.valueOf(Await.result(ModelRequest.getRequest("api/field/get/currentPlayerType"), 5.seconds))).getOrElse(PlayerType.Human)

  def fieldStatusCellHttp(row: Int, col: Int): Status =
    Try(Await.result(ModelRequest.getRequest(s"api/field/get/statusCell/$row/$col"), 5.seconds))
      .toOption
      .flatMap(response => Status.values.find(_.toString == response))
      .getOrElse(Status.Empty)

  def fieldRowCellHttp(row: Int, col: Int): Boolean =
    Await.result(ModelRequest.getRequest(s"api/field/get/rowCell/$row/$col"), 5.seconds).toBoolean

  def fieldColCellHttp(row: Int, col: Int): Boolean =
    Await.result(ModelRequest.getRequest(s"api/field/get/colCell/$row/$col"), 5.seconds).toBoolean

  def fieldRowSizeHttp: Int =
    Await.result(ModelRequest.getRequest(s"api/field/get/rowSize"), 5.seconds).toInt

  def fieldColSizeHttp: Int =
    Await.result(ModelRequest.getRequest(s"api/field/get/colSize"), 5.seconds).toInt

  def fieldIsEdgeHttp(move: Move): Boolean =
    val jsonBody: JsObject = Json.obj(
      "vec" -> Json.toJson(move.vec),
      "x" -> Json.toJson(move.x),
      "y" -> Json.toJson(move.y),
      "value" -> Json.toJson(move.value),
      "field" -> Json.toJson(field.toJson)
    )
    Await.result(ModelRequest.postRequest("api/field/get/isEdge", jsonBody), 5.seconds).toBoolean
