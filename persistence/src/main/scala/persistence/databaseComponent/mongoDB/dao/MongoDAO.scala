package persistence.databaseComponent.mongoDB.dao

import common.config.DatabaseConfig.PERSISTENCE_DB_MONGO_COLLECTION_NAME
import de.github.dotsandboxes.lib._
import org.bson.BsonValue
import org.mongodb.scala._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.ReplaceOptions
import org.mongodb.scala.model.Updates._
import org.slf4j.LoggerFactory
import persistence.databaseComponent.mongoDB.base.DBConnectorInterface
import persistence.databaseComponent.{DAOInterface, GameTableData}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success, Try}
import org.bson.BsonArray
import org.mongodb.scala.bson.BsonDocument

object Mongo:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new MongoDAO(dbConnector)

  private class MongoDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    private val MONGO_COLLECTION_GAME_ID = 1

    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    private val initialGameDocument = Document(
      "_id"                 -> MONGO_COLLECTION_GAME_ID,
      "state"               -> "",
      "boardSize"           -> BoardSize.Medium.toString,
      "playerSize"          -> PlayerSize.Two.toString,
      "playerType"          -> PlayerType.Human.toString,
      "currentPlayerIndex"  -> 0,
      "playerData"          -> list.zipWithIndex.map { case (player, index) => playerDocument(index, player.points) }
    )

    dbConnector.connect

    private val gameCollection: MongoCollection[Document] = dbConnector.db.getCollection(PERSISTENCE_DB_MONGO_COLLECTION_NAME)

    create match
      case Success(_)         => logger.info(s"Persistence Service [Database] -- Initial collection successfully created")
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not create initial collection: ${exception.getMessage}")

    private def playerDocument(playerData: (Int, Int)): BsonDocument =
      BsonDocument("index" -> playerData._1, "points" -> playerData._2)

    private def playerData(playerDataValue: BsonValue): Vector[(Int, Int)] =
      playerDataValue.asArray.getValues.asScala.toVector
        .map(_.asDocument())
        .map { doc =>
          val index = doc.getInt32("index").getValue
          val points = doc.getInt32("points").getValue
          (index, points)
        }

    override def create: Try[Int] = Try {
      Await.result(gameCollection.replaceOne(
        filter = Document("_id" -> MONGO_COLLECTION_GAME_ID),
        replacement = initialGameDocument,
        options = ReplaceOptions().upsert(true)
      ).toFuture, 5.seconds)
      MONGO_COLLECTION_GAME_ID
    }

    override def read: Try[GameTableData] = Try {
      val document: Document = Await.result(
        gameCollection.find(equal("_id", MONGO_COLLECTION_GAME_ID)).first().toFuture,
        5.seconds
      )
      GameTableData(
        state = document.getString("state"),
        boardSize = document.getString("boardSize"),
        playerSize = document.getString("playerSize"),
        playerType = document.getString("playerType"),
        currPlayerIndex = document.getInteger("currentPlayerIndex"),
        playerData = playerData(document.get("playerData").get),
      )
    }

    override def update(gameTableData: GameTableData): Try[Int] = Try {
      val update = combine(
        set("state", gameTableData.state),
        set("boardSize", gameTableData.boardSize),
        set("playerSize", gameTableData.playerSize),
        set("playerType", gameTableData.playerType),
        set("currentPlayerIndex", gameTableData.currPlayerIndex),
        set("playerData", new BsonArray(gameTableData.playerData.map {
          case (index, points) => playerDocument(index, points)
        }.asJava))
      )
      Await.result(
        gameCollection.updateOne(
          filter = equal("_id", MONGO_COLLECTION_GAME_ID),
          update = update
        ).toFuture,
        5.seconds
      )
      MONGO_COLLECTION_GAME_ID
    }

    override def delete: Try[Int] = Try {
      Await.result(
        gameCollection.deleteOne(equal("_id", MONGO_COLLECTION_GAME_ID)).toFuture,
        5.seconds
      )
      MONGO_COLLECTION_GAME_ID
    }
