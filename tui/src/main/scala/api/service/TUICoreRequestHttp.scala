package api.service

import api.client.TUICoreClient
import de.github.dotsandboxes.lib.{Move, BoardSize, PlayerSize, PlayerType, ComputerDifficulty}
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object TUICoreRequestHttp:
  override def toString: String =
    Await.result(TUICoreClient.getRequest("api/core/"), 5.seconds)

  def publish(move: Move): Future[String] =
    TUICoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> Json.toJson("put"),
      "vec"    -> Json.toJson(move.vec),
      "x"      -> Json.toJson(move.x),
      "y"      -> Json.toJson(move.y),
      "value"  -> Json.toJson(move.value)
    ))

  def publish(method: String): Future[String] =
    TUICoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> Json.toJson(method)
    ))

  def restart: Future[String] =
    TUICoreClient.getRequest("api/core/restart")

  def initGame(
      boardSize: BoardSize,
      playerSize: PlayerSize,
      playerType: PlayerType,
      computerDifficulty: ComputerDifficulty
  ): Future[String] =
    TUICoreClient.postRequest("api/core/initGame", Json.obj(
      "boardSize"          -> Json.toJson(boardSize.toString),
      "playerSize"         -> Json.toJson(playerSize.toString),
      "playerType"         -> Json.toJson(playerType.toString),
      "computerDifficulty" -> Json.toJson(computerDifficulty.toString)
    ))

  def finalStats: String =
    val winner = Await.result(TUICoreClient.getRequest("api/core/get/winner"), 5.seconds)
    val stats  = Await.result(TUICoreClient.getRequest("api/core/get/stats"), 5.seconds)
    "\n" +
      winner + "\n" +
      "_________________________" + "\n\n" +
      stats +
      "\n"

  def registerTUIObserver(tuiObserverUrl: String): Future[String] =
    TUICoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> Json.toJson(tuiObserverUrl)
    ))

  def deregisterTUIObserver(tuiObserverUrl: String): Future[Unit] =
    Await.result(TUICoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> Json.toJson(tuiObserverUrl)
    )), 5.seconds)
    TUICoreClient.shutdown
