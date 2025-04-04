package tui.api.service

import akka.Done
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}
import tui.api.client.CoreClient

object CoreRequestHttp:
  def fieldString: String = Await.result(CoreClient.getRequest("api/core/"), 5.seconds) match
    case Right(field) => field
    case Left(error)  => throw new RuntimeException(s"Error fetching field as String: $error")

  def publish(move: Move): Either[String, String] =
    Await.result(CoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> "put",
      "value"  -> move.value,
      "vec"    -> move.vec,
      "x"      -> move.x,
      "y"      -> move.y
    )), 5.seconds)

  def publish(method: String): Future[Either[String, String]] =
    CoreClient.postRequest("api/core/publish", Json.obj(
      "method" -> method
    ))

  def restart: Future[Either[String, String]] =
    CoreClient.getRequest("api/core/restart")

  def initGame(
      boardSize: BoardSize,
      playerSize: PlayerSize,
      playerType: PlayerType,
      computerDifficulty: ComputerDifficulty
  ): Future[Either[String, String]] =
    CoreClient.postRequest("api/core/initGame", Json.obj(
      "boardSize"          -> boardSize.toString,
      "playerSize"         -> playerSize.toString,
      "playerType"         -> playerType.toString,
      "computerDifficulty" -> computerDifficulty.toString
    ))

  def playerGameData: PlayerGameData =
    Await.result(CoreClient.getRequest("api/core/get/playerGameData"), 5.seconds) match
      case Right(jsonString) => 
        decode[PlayerGameData](jsonString) match
          case Right(data) => data
          case Left(error) => throw new RuntimeException(s"Error decoding PlayerGameData: ${error.getMessage}")
      case Left(error) =>
        throw new RuntimeException(s"Error fetching PlayerGameData: $error")

  def registerTUIObserver(tuiObserverUrl: String): Future[Either[String, String]] =
    CoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> tuiObserverUrl
    ))

  def deregisterTUIObserver(tuiObserverUrl: String): Future[Either[String, String]] =
    CoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> tuiObserverUrl
    ))
