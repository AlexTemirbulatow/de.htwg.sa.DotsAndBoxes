package controllerComponent

import common.model.fieldService.FieldInterface
import controllerImpl.observer.Observable
import de.github.dotsandboxes.lib._
import scala.concurrent.Future
import scala.util.Try

trait ControllerInterface extends Observable:
  def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, computerDifficulty: ComputerDifficulty): FieldInterface
  def put(move: Move): String
  def getCellData: CellData
  def fieldData: FieldData
  def gameBoardData: GameBoardData
  def playerTurnData: PlayerTurnData
  def playerResultData: PlayerResultData
  def fieldSizeData: FieldSizeData
  def getStatusCell(row: Int, col: Int): Status
  def getRowCell(row: Int, col: Int): Boolean
  def getColCell(row: Int, col: Int): Boolean
  def restart: FieldInterface
  def undo: FieldInterface
  def redo: FieldInterface
  def save: FieldInterface
  def load: FieldInterface
  def colSize(): Int
  def rowSize(): Int
  def getComputerDifficulty: ComputerDifficulty
  def boardSize: BoardSize
  def playerSize: PlayerSize
  def playerType: PlayerType
  def playerList: Vector[Player]
  def currentPlayer: String
  def currentPlayerType: PlayerType
  def currentPoints: Int
  def gameEnded: Boolean
  def winner: String
  def stats: String
  def publish(doThis: => FieldInterface): FieldInterface
  def publish(doThis: Move => String, move: Move): Try[FieldInterface]
  def computerMove(field: FieldInterface): Future[FieldInterface]
  def calculateComputerMove(field: FieldInterface): FieldInterface
  override def toString: String
