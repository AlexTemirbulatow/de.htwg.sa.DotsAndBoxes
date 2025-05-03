package metric.databaseComponent

import scala.util.Try

trait DAOInterface:
  def create(timestamp: Long, playerName: String): Try[String]
  def getTotalGameDuration: Int
  def getAvgMoveDuration(playerName: String): Int
  def getMinMoveDuration(playerName: String): Int
  def getMaxMoveDuration(playerName: String): Int
  def getLongestMoveStreak(playerName: String): Int
  def getNumOfTotalMoves(playerName: String): Int
