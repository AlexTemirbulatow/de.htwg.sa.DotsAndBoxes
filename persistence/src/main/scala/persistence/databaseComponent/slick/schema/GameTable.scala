package persistence.databaseComponent.slick.schema

import slick.jdbc.PostgresProfile.api._

class GameTable(tag: Tag) extends Table[(Int, String, String, String, Int, String)](tag, "game"):
  def gameID              = column[Int]("game_id", O.PrimaryKey, O.AutoInc)
  def state               = column[String]("state")
  def boardSize           = column[String]("board_size")
  def playerSize          = column[String]("player_size")
  def currentPlayerIndex  = column[Int]("current_player_index")
  def currentPlayerType   = column[String]("current_player_type")

  override def * = (gameID, state, boardSize, playerSize, currentPlayerIndex, currentPlayerType)
