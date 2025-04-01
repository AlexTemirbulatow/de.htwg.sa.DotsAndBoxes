package gui.api.service

import de.github.dotsandboxes.lib._
import gui.api.client.CoreClient
import io.circe.generic.auto._
import io.circe.parser.decode
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object CoreRequestHttp:
  def fieldData: FieldData =
    decode[FieldData](
      Await.result(CoreClient.getRequest("api/core/get/fieldData"), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding FieldData: ${error.getMessage}")

  def gameBoardData: GameBoardData =
    decode[GameBoardData](
      Await.result(CoreClient.getRequest("api/core/get/gameBoardData"), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding GameBoardData: ${error.getMessage}")

  def playerGameData: PlayerGameData =
    decode[PlayerGameData](
      Await.result(CoreClient.getRequest("api/core/get/playerGameData"), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding PlayerGameData: ${error.getMessage}")

  def fieldSizeData: FieldSizeData =
    decode[FieldSizeData](
      Await.result(CoreClient.getRequest("api/core/get/fieldSizeData"), 5.seconds)
    ) match
      case Right(data) => data
      case Left(error) => throw new RuntimeException(s"Error decoding FieldSizeData: ${error.getMessage}")

  def gameEnded: Boolean =
    Await.result(CoreClient.getRequest(s"api/core/get/gameEnded"), 5.seconds).toBoolean

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

  def registerGUIObserver(guiObserverUrl: String): Future[String] =
    CoreClient.postRequest("api/core/registerObserver", Json.obj(
      "url" -> guiObserverUrl
    ))

  def deregisterGUIObserver(guiObserverUrl: String): Future[Unit] =
    Await.result(CoreClient.postRequest("api/core/deregisterObserver", Json.obj(
      "url" -> guiObserverUrl
    )), 5.seconds)
    CoreClient.shutdown
