package controllerComponent

import scala.util.Try
import computerComponent.ComputerInterface
import fieldComponent.FieldInterface
import controllerImpl.observer.Observable
import lib.{PackT, BoardSize, PlayerSize, PlayerType, ComputerDifficulty, Status, Player, Move}

trait ControllerInterface extends Observable:
  def initGame(boardSize: BoardSize, playerSize: PlayerSize, playerType: PlayerType, computerDifficulty: ComputerDifficulty): FieldInterface
  def getComputerImpl(difficulty: ComputerDifficulty): ComputerInterface
  def getComputerDifficulty(computer: ComputerInterface): ComputerDifficulty
  def put(move: Move): FieldInterface
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
  def computerDifficulty: ComputerDifficulty
  def boardSize: BoardSize
  def playerSize: PlayerSize
  def playerType: PlayerType
  def playerList: Vector[Player]
  def currentPlayer: String
  def currentPoints: Int
  def gameEnded: Boolean
  def winner: String
  def stats: String
  def publish(doThis: => FieldInterface): FieldInterface
  def publish(doThis: Move => FieldInterface, move: Move): Try[FieldInterface]
  def publishCheat(doThis: Move => FieldInterface, pack: PackT[Option[Move]]): Try[FieldInterface]
  def computerMove(field: FieldInterface): FieldInterface
  override def toString: String
