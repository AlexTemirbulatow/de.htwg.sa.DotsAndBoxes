package metric.databaseComponent.slick.schema

import slick.jdbc.PostgresProfile.api._

class MoveTable(tag: Tag) extends Table[(Int, Long, String)](tag, "move"):
  def moveID = column[Int]("move_id", O.PrimaryKey, O.AutoInc)
  def timestamp = column[Long]("timestamp")
  def playerName = column[String]("player_name")

  override def * = (moveID, timestamp, playerName)
