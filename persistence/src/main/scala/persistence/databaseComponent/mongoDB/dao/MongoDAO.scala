package persistence.databaseComponent.mongoDB.dao

import common.config.DatabaseConfig.PERSISTENCE_DB_MONGO_COLLECTION_NAME
import org.mongodb.scala._
import org.mongodb.scala.bson.Document
import org.slf4j.LoggerFactory
import persistence.databaseComponent.mongoDB.base.DBConnectorInterface
import persistence.databaseComponent.{DAOInterface, GameTableData}
import scala.util.{Failure, Success, Try}
import org.mongodb.scala.Document
import de.github.dotsandboxes.lib._
import scala.concurrent.Await
import scala.concurrent.duration._
import org.mongodb.scala.model.ReplaceOptions

object Mongo:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new MongoDAO(dbConnector)

  private class MongoDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    private val MONGO_COLLECTION_GAME_ID = 1

    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    private val initialPlayerDocument: Seq[Document] = list.zipWithIndex.map { case (player, index) =>
      Document("index" -> index, "points" -> player.points, "active" -> false)
    }
    private val initialGameDocument = Document(
      "_id"                 -> MONGO_COLLECTION_GAME_ID,
      "state"               -> "",
      "boardSize"           -> BoardSize.Medium.toString,
      "playerSize"          -> PlayerSize.Two.toString,
      "playerType"          -> PlayerType.Human.toString,
      "currentPlayerIndex"  -> 0,
      "playerData"          -> initialPlayerDocument.toList
    )

    dbConnector.connect

    private val gameCollection: MongoCollection[Document] = dbConnector.db.getCollection(PERSISTENCE_DB_MONGO_COLLECTION_NAME)

    create match
      case Success(_)         => logger.info(s"Persistence Service [Database] -- Initial collection successfully created")
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not create initial collection: ${exception.getMessage}")

    override def create: Try[Int] = Try {
      Await.result(gameCollection.replaceOne(
        filter = Document("_id" -> MONGO_COLLECTION_GAME_ID),
        replacement = initialGameDocument,
        options = ReplaceOptions().upsert(true)
      ).toFuture, 5.seconds)
      MONGO_COLLECTION_GAME_ID
    }

    override def read: Try[GameTableData] = ???

    override def update(gameTableData: GameTableData): Try[Int] = ???

    override def delete: Try[Int] = ???
