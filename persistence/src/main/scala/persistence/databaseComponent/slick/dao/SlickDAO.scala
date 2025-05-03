package persistence.databaseComponent.slick.dao

import de.github.dotsandboxes.lib._
import org.slf4j.LoggerFactory
import persistence.databaseComponent.slick.schema.{GameTable, PlayerTable}
import persistence.databaseComponent.{DAOInterface, GameTableData}
import persistence.databaseComponent.slick.base.DBConnectorInterface
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import scala.util.{Failure, Success}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery

object Slick:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new SlickDAO(dbConnector)

  private class SlickDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    private val initialGameTableData = GameTableData(
      "",
      BoardSize.Medium.toString,
      PlayerSize.Two.toString,
      PlayerType.Human.toString,
      0,
      list.zipWithIndex.map((player, index) => (index, player.points))
    )

    private def gameTable = TableQuery[GameTable](new GameTable(_))
    private def playerTable = TableQuery[PlayerTable](new PlayerTable(_))

    dbConnector.connect(
      DBIO.seq(
        playerTable.schema.dropIfExists,
        gameTable.schema.dropIfExists,
        gameTable.schema.createIfNotExists,
        playerTable.schema.createIfNotExists
      )
    )
    create match
      case Success(id)        => logger.info(s"Persistence Service [Database] -- Initial table successfully created [ID: $id]")
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not create initial table: ${exception.getMessage}")

    override def create: Try[Int] = Try {
      val gameID: Int = Await.result(insertGame(initialGameTableData), 5.seconds)
      initialGameTableData.playerData.foreach(data => insertPlayer(data._1, data._2, false, gameID))
      gameID
    }

    private def insertGame(gameTableData: GameTableData): Future[Int] =
      val insertAction = (
        gameTable.map(g => 
          (g.state, g.boardSize, g.playerSize, g.playerType, g.currentPlayerIndex)
        ) returning gameTable.map(_.gameID)
      ) += (gameTableData.state, gameTableData.boardSize, gameTableData.playerSize, gameTableData.playerType, gameTableData.currPlayerIndex)
      dbConnector.db.run(insertAction)

    private def insertPlayer(playerIndex: Int, points: Int, active: Boolean, gameID: Int): Future[Int] =
      val insertAction = (
        playerTable.map(g =>
          (g.playerIndex, g.points, g.active, g.gameID)
        ) returning playerTable.map(_.playerIndex)
      ) += (playerIndex, points, active, gameID)
      dbConnector.db.run(insertAction)

    override def read: Try[GameTableData] = Try {
      val gameID: Int = Await.result(getLatestGameID, 5.seconds).get
      val game = Await.result(dbConnector.db.run(
        gameTable.filter(_.gameID === gameID).result.headOption
      ), 5.seconds).get

      val playerList = Await.result(dbConnector.db.run(
        playerTable
          .filter(_.gameID === gameID)
          .filter(_.active)
          .sortBy(_.playerIndex)
          .result
      ), 5.seconds)

      val (state, boardSize, playerSize, currPlayerIndex, currPlayerType) =
        (game._2, game._3, game._4, game._5, game._6)
      val playerData = playerList.map(p => (p._1, p._2)).toVector

      GameTableData(state, boardSize, playerSize, currPlayerIndex, currPlayerType, playerData)
    }

    override def update(gameTableData: GameTableData): Try[Int] = Try {
      val gameID: Int = Await.result(getLatestGameID, 5.seconds).get
      Await.result(deactivateAllPlayers(gameID), 5.seconds)
      updateGame(gameID, gameTableData)
      gameTableData.playerData.foreach(data => updatePlayer(data._1, data._2, true, gameID))
      gameID
    }

    private def updateGame(gameID: Int, gameTableData: GameTableData): Future[Int] =
      val updateAction = gameTable.filter(_.gameID === gameID)
        .map(g => (g.state, g.boardSize, g.playerSize, g.playerType, g.currentPlayerIndex))
        .update((gameTableData.state, gameTableData.boardSize, gameTableData.playerSize, gameTableData.playerType, gameTableData.currPlayerIndex))
      dbConnector.db.run(updateAction)

    private def updatePlayer(playerIndex: Int, points: Int, active: Boolean, gameID: Int): Future[Int] =
      val updateAction = playerTable.filter(p => p.playerIndex === playerIndex && p.gameID === gameID)
        .map(p => (p.points, p.active))
        .update((points, active))
      dbConnector.db.run(updateAction)

    private def deactivateAllPlayers(gameID: Int): Future[Int] =
      val updateAction = playerTable.filter(_.gameID === gameID)
        .map(_.active)
        .update(false)
      dbConnector.db.run(updateAction)

    private def getLatestGameID: Future[Option[Int]] =
      val lookupAction = gameTable
        .sortBy(_.gameID.desc)
        .map(_.gameID)
        .result
        .headOption
      dbConnector.db.run(lookupAction)

    override def delete: Try[Int] = Try {
      val gameID: Int = Await.result(getLatestGameID, 5.seconds).get
      Await.result(dbConnector.db.run(
        DBIO.seq(
          playerTable.filter(_.gameID === gameID).delete,
          gameTable.filter(_.gameID === gameID).delete
        )
      ), 5.seconds)
      gameID
    }
