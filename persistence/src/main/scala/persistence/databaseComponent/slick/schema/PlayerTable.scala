package persistence.databaseComponent.slick.schema

import slick.jdbc.PostgresProfile.api._

class PlayerTable(tag: Tag) extends Table[(Int, Int, Boolean, Int)](tag, "player"):
  def playerIndex  = column[Int]("player_index", O.PrimaryKey)
  def points       = column[Int]("points")
  def active       = column[Boolean]("active")
  def gameID       = column[Int]("game_id")

  override def * = (playerIndex, points, active, gameID)

  def game = foreignKey("game_fk", gameID, TableQuery[GameTable](new GameTable(_)))(_.gameID, onDelete = ForeignKeyAction.Cascade)
