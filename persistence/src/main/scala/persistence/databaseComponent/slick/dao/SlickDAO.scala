package persistence.databaseComponent.slick.dao

import persistence.databaseComponent.DAOInterface
import persistence.databaseComponent.slick.schema.{GameTable, PlayerTable}
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import de.github.dotsandboxes.lib._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import persistence.databaseComponent.slick.base.DBConnectorInterface

class SlickDAO(using dbConnector: DBConnectorInterface) extends DAOInterface:
  private def gameTable = TableQuery[GameTable](new GameTable(_))
  private def playerTable = TableQuery[PlayerTable](new PlayerTable(_))

  override def init: Unit =
    dbConnector.connect(
      DBIO.seq(
        playerTable.schema.dropIfExists,
        gameTable.schema.dropIfExists,
        gameTable.schema.createIfNotExists,
        playerTable.schema.createIfNotExists
      )
    )
    create

  override def create: Int =
    val gameID: Int = Await.result(
      insertGame("", BoardSize.Medium.toString, PlayerSize.Two.toString, 0, PlayerType.Human.toString),
      5.seconds
    )
    list.zipWithIndex.foreach { case (player, index) =>
      insertPlayer(index, player.points, false, gameID)
    }
    gameID

  private def insertGame(state: String, boardSize: String, playerSize: String, playerIndex: Int, playerType: String): Future[Int] =
    val insertAction = (
      gameTable.map(g => 
        (g.state, g.boardSize, g.playerSize, g.currentPlayerIndex, g.currentPlayerType)
      ) returning gameTable.map(_.gameID)
    ) += (state, boardSize, playerSize, playerIndex, playerType)
    dbConnector.db.run(insertAction)

  private def insertPlayer(playerIndex: Int, points: Int, active: Boolean, gameID: Int): Future[Int] =
    val insertAction = (
      playerTable.map(g =>
        (g.playerIndex, g.points, g.active, g.gameID)
      ) returning playerTable.map(_.playerIndex)
    ) += (playerIndex, points, active, gameID)
    dbConnector.db.run(insertAction)
    
  override def read: Unit = ???

  override def update: Unit = ???

  override def delete: Unit = ???
