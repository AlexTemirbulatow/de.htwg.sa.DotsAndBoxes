package persistence.databaseComponent

import de.github.dotsandboxes.lib.Player
import scala.concurrent.Future
import scala.util.Try

case class GameTableData(state: String, boardSize: String, playerSize: String, playerType: String, currPlayerIndex: Int, playerData: Vector[(Int, Int)])

trait DAOInterface:
  def init: Unit
  def create: Try[Int]
  def read: Try[GameTableData]
  def update(gameTableData: GameTableData): Try[Int]
  def delete: Try[Int]
