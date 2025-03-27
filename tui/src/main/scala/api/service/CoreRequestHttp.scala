package api.service

import api.client.CoreClient
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, Move, PlayerSize, PlayerType}
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object CoreRequestHttp:
  override def toString: String =
    Await.result(CoreClient.getRequest("api/core/"), 5.seconds)

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

  def restart: Future[String] =
    CoreClient.getRequest("api/core/restart")

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

  def finalStats: String =
    val winner = Await.result(CoreClient.getRequest("api/core/get/winner"), 5.seconds)
    val stats  = Await.result(CoreClient.getRequest("api/core/get/stats"), 5.seconds)
    "\n" +
      winner + "\n" +
      "_________________________" + "\n\n" +
      stats +
      "\n"

  def registerTUIObserver(tuiObserverUrl: String): Future[String] =
    CoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> tuiObserverUrl
    ))

  def deregisterTUIObserver(tuiObserverUrl: String): Future[Unit] =
    Await.result(CoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> tuiObserverUrl
    )), 5.seconds)
    CoreClient.shutdown
