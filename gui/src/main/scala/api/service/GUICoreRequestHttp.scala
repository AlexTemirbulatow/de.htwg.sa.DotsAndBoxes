package api.service

import api.client.GUICoreClient
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, Move, Player, PlayerSize, PlayerType, CellData}
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import scala.util.Try

object GUICoreRequestHttp:
  def playerList: Vector[Player] =
    decode[Vector[Player]](
      Await.result(GUICoreClient.getRequest("api/core/get/playerList"), 5.seconds)
    ).getOrElse(Vector.empty)

  def cellData: CellData =
    decode[CellData](
      Await.result(GUICoreClient.getRequest("api/core/get/cellData"), 5.seconds)
    ).getOrElse(CellData(Vector.empty, Vector.empty, Vector.empty))

  def boardSize: BoardSize =
    Try(BoardSize.valueOf(
      Await.result(GUICoreClient.getRequest("api/core/get/boardSize"), 5.seconds)
    )).getOrElse(BoardSize.Medium)

  def playerSize: PlayerSize =
    Try(PlayerSize.valueOf(
      Await.result(GUICoreClient.getRequest("api/core/get/playerSize"), 5.seconds)
    )).getOrElse(PlayerSize.Two)

  def playerType: PlayerType =
    Try(PlayerType.valueOf(
      Await.result(GUICoreClient.getRequest("api/core/get/playerType"), 5.seconds)
    )).getOrElse(PlayerType.Human)

  def computerDifficulty: ComputerDifficulty =
    Try(ComputerDifficulty.valueOf(
      Await.result(GUICoreClient.getRequest("api/core/get/computerDifficulty"), 5.seconds)
    )).getOrElse(ComputerDifficulty.Medium)

  def statusCell(row: Int, col: Int): String =
    Await.result(GUICoreClient.getRequest(s"api/core/get/statusCell/$row/$col"), 5.seconds)

  def rowCell(row: Int, col: Int): Boolean =
    Await.result(GUICoreClient.getRequest(s"api/core/get/rowCell/$row/$col"), 5.seconds).toBoolean

  def colCell(row: Int, col: Int): Boolean =
    Await.result(GUICoreClient.getRequest(s"api/core/get/colCell/$row/$col"), 5.seconds).toBoolean

  def rowSize: Int =
    Await.result(GUICoreClient.getRequest(s"api/core/get/rowSize"), 5.seconds).toInt

  def colSize: Int =
    Await.result(GUICoreClient.getRequest(s"api/core/get/colSize"), 5.seconds).toInt

  def gameEnded: Boolean =
    Await.result(GUICoreClient.getRequest(s"api/core/get/gameEnded"), 5.seconds).toBoolean

  def currentPlayer: String =
    Await.result(GUICoreClient.getRequest(s"api/core/get/currentPlayer"), 5.seconds)

  def currentPoints: String =
    Await.result(GUICoreClient.getRequest(s"api/core/get/currentPoints"), 5.seconds)

  def winner: String =
    Await.result(GUICoreClient.getRequest(s"api/core/get/winner"), 5.seconds)

  def restart: Future[String] =
    GUICoreClient.getRequest("api/core/restart")

  def publish(move: Move): Future[String] =
    GUICoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> Json.toJson("put"),
      "vec"    -> Json.toJson(move.vec),
      "x"      -> Json.toJson(move.x),
      "y"      -> Json.toJson(move.y),
      "value"  -> Json.toJson(move.value)
    ))

  def publish(method: String): Future[String] =
    GUICoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> Json.toJson(method)
    ))

  def initGame(
      boardSize: BoardSize,
      playerSize: PlayerSize,
      playerType: PlayerType,
      computerDifficulty: ComputerDifficulty
  ): Future[String] =
    GUICoreClient.postRequest("api/core/initGame", Json.obj(
      "boardSize"  -> Json.toJson(boardSize.toString),
      "playerSize" -> Json.toJson(playerSize.toString),
      "playerType" -> Json.toJson(playerType.toString),
      "computerDifficulty" -> Json.toJson(computerDifficulty.toString)
    ))

  def registerGUIObserver(guiObserverUrl: String): Future[String] =
    GUICoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> Json.toJson(guiObserverUrl)
    ))

  def deregisterGUIObserver(guiObserverUrl: String): Future[Unit] =
    Await.result(GUICoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> Json.toJson(guiObserverUrl)
    )), 5.seconds)
    GUICoreClient.shutdown
