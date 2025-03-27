package api.service

import api.client.CoreClient
import de.github.dotsandboxes.lib.{BoardSize, CellData, ComputerDifficulty, Move, Player, PlayerSize, PlayerType}
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

object CoreRequestHttp:
  def playerList: Vector[Player] =
    decode[Vector[Player]](
      Await.result(CoreClient.getRequest("api/core/get/playerList"), 5.seconds)
    ) match
      case Right(players) => players
      case Left(error)    => throw new RuntimeException(s"Error decoding Vector[Player]: ${error.getMessage}")

  def cellData: CellData =
    decode[CellData](
      Await.result(CoreClient.getRequest("api/core/get/cellData"), 5.seconds)
    ) match
      case Right(cellData) => cellData
      case Left(error)     => throw new RuntimeException(s"Error decoding CellData: ${error.getMessage}")

  def boardSize: BoardSize =
    Try(BoardSize.valueOf(
      Await.result(CoreClient.getRequest("api/core/get/boardSize"), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Board Size."))

  def playerSize: PlayerSize =
    Try(PlayerSize.valueOf(
      Await.result(CoreClient.getRequest("api/core/get/playerSize"), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Size."))

  def playerType: PlayerType =
    Try(PlayerType.valueOf(
      Await.result(CoreClient.getRequest("api/core/get/playerType"), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Player Type."))

  def computerDifficulty: ComputerDifficulty =
    Try(ComputerDifficulty.valueOf(
      Await.result(CoreClient.getRequest("api/core/get/computerDifficulty"), 5.seconds)
    )).getOrElse(throw new RuntimeException("Invalid Computer Difficulty"))

  def statusCell(row: Int, col: Int): String =
    Await.result(CoreClient.getRequest(s"api/core/get/statusCell/$row/$col"), 5.seconds)

  def rowCell(row: Int, col: Int): Boolean =
    Await.result(CoreClient.getRequest(s"api/core/get/rowCell/$row/$col"), 5.seconds).toBoolean

  def colCell(row: Int, col: Int): Boolean =
    Await.result(CoreClient.getRequest(s"api/core/get/colCell/$row/$col"), 5.seconds).toBoolean

  def rowSize: Int =
    Await.result(CoreClient.getRequest(s"api/core/get/rowSize"), 5.seconds).toInt

  def colSize: Int =
    Await.result(CoreClient.getRequest(s"api/core/get/colSize"), 5.seconds).toInt

  def gameEnded: Boolean =
    Await.result(CoreClient.getRequest(s"api/core/get/gameEnded"), 5.seconds).toBoolean

  def currentPlayer: String =
    Await.result(CoreClient.getRequest(s"api/core/get/currentPlayer"), 5.seconds)

  def currentPoints: String =
    Await.result(CoreClient.getRequest(s"api/core/get/currentPoints"), 5.seconds)

  def winner: String =
    Await.result(CoreClient.getRequest(s"api/core/get/winner"), 5.seconds)

  def restart: Future[String] =
    CoreClient.getRequest("api/core/restart")

  def publish(move: Move): Future[String] =
    CoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> "put",
      "value"  -> move.value,
      "vec"    -> move.vec,
      "x"      -> move.x,
      "y"      -> move.y
    ))

  def publish(method: String): Future[String] =
    CoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> method
    ))

  def initGame(
      boardSize: BoardSize,
      playerSize: PlayerSize,
      playerType: PlayerType,
      computerDifficulty: ComputerDifficulty
  ): Future[String] =
    CoreClient.postRequest("api/core/initGame", Json.obj(
      "boardSize"          -> boardSize.toString,
      "playerSize"         -> playerSize.toString,
      "playerType"         -> playerType.toString,
      "computerDifficulty" -> computerDifficulty.toString
    ))

  def registerGUIObserver(guiObserverUrl: String): Future[String] =
    CoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> guiObserverUrl
    ))

  def deregisterGUIObserver(guiObserverUrl: String): Future[Unit] =
    Await.result(CoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> guiObserverUrl
    )), 5.seconds)
    CoreClient.shutdown
