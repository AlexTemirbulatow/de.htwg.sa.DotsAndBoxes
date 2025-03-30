package controllerComponent

import common.model.fieldService.FieldInterface
import controllerImpl.observer.Observable
import de.github.dotsandboxes.lib._
import scala.concurrent.Future
import scala.util.Try

trait ControllerInterface extends Observable:
  def fieldData: FieldData
  def gameBoardData: GameBoardData
  def playerGameData: PlayerGameData
  def fieldSizeData: FieldSizeData
  def gameEnded: Boolean
  def put(move: Move): String
  def undo: FieldInterface
  def redo: FieldInterface
  def save: FieldInterface
  def load: FieldInterface
  def restart: FieldInterface
  def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, computerDifficulty: ComputerDifficulty): FieldInterface
  def publish(doThis: => FieldInterface): FieldInterface
  def publish(doThis: Move => String, move: Move): Try[FieldInterface]
  def computerMove(field: FieldInterface): Future[FieldInterface]
  override def toString: String
