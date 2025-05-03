package metric.databaseComponent.mongoDB.dao

import common.config.DatabaseConfig.METRIC_DB_MONGO_COLLECTION_NAME
import java.util.UUID
import metric.databaseComponent.DAOInterface
import metric.databaseComponent.mongoDB.base.DBConnectorInterface
import org.bson.Document
import org.mongodb.scala.MongoCollection
import org.mongodb.scala._
import org.mongodb.scala.model.Accumulators._
import org.mongodb.scala.model.Aggregates._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Sorts._
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.util.Try

object Mongo:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new MongoDAO(dbConnector)

  private class MongoDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    dbConnector.connect

    private val moveCollection: MongoCollection[Document] = dbConnector.db.getCollection(METRIC_DB_MONGO_COLLECTION_NAME)

    Await.result(moveCollection.deleteMany(new Document()).toFuture, 5.seconds)

    override def create(timestamp: Long, playerName: String): Try[String] = Try {
      val uuid = UUID.randomUUID().toString
      val doc = new Document()
        .append("timestamp", timestamp)
        .append("playerName", playerName)
      Await.result(moveCollection.insertOne(doc).toFuture, 5.seconds)
      uuid
    }

    override def getTotalGameDuration: Int = {
      val sortedTimestamps = Await.result(
        moveCollection.find()
          .sort(ascending("timestamp"))
          .projection(fields(include("timestamp"), excludeId()))
          .map(_.getLong("timestamp"))
          .toFuture(),
        5.seconds
      )
      if sortedTimestamps.size >= 2 then
        val duration = sortedTimestamps.last - sortedTimestamps.head
        (duration / 1000).toInt
      else 0
    }

    override def getAvgMoveDuration(playerName: String): Int = {
      val timestamps = Await.result(
        moveCollection.find(equal("playerName", playerName))
          .sort(ascending("timestamp"))
          .projection(fields(include("timestamp"), excludeId()))
          .map(_.getLong("timestamp"))
          .toFuture(),
        5.seconds
      )
      if timestamps.size > 1 then
        val diffs = timestamps.sliding(2).collect { case Seq(a, b) => b - a }.toSeq
        (diffs.sum.toDouble / diffs.size / 1000).round.toInt
      else 0
    }

    override def getMinMoveDuration(playerName: String): Int = {
      val timestamps = Await.result(
        moveCollection.find(equal("playerName", playerName))
          .sort(ascending("timestamp"))
          .projection(fields(include("timestamp"), excludeId()))
          .map(_.getLong("timestamp"))
          .toFuture(),
        5.seconds
      )
      if timestamps.size > 1 then
        val diffs = timestamps.sliding(2).collect { case Seq(a, b) => b - a }.toSeq
        (diffs.min.toDouble / 1000).round.toInt
      else 0
    }

    override def getMaxMoveDuration(playerName: String): Int = {
      val timestamps = Await.result(
        moveCollection.find(equal("playerName", playerName))
          .sort(ascending("timestamp"))
          .projection(fields(include("timestamp"), excludeId()))
          .map(_.getLong("timestamp"))
          .toFuture(),
        5.seconds
      )
      if timestamps.size > 1 then
        val diffs = timestamps.sliding(2).collect { case Seq(a, b) => b - a }.toSeq
        (diffs.max.toDouble / 1000).round.toInt
      else 0
    }

    override def getLongestMoveStreak(playerName: String): Int = {
      val players = Await.result(
        moveCollection.find()
          .sort(ascending("timestamp"))
          .projection(fields(include("playerName"), excludeId()))
          .map(_.getString("playerName"))
          .toFuture(),
        5.seconds
      )

      players.foldLeft((0, 0)) { case ((maxStreak, currentStreak), name) =>
        if name == playerName then
          val newStreak = currentStreak + 1
          (math.max(maxStreak, newStreak), newStreak)
        else
          (maxStreak, 0)
      }._1
    }

    override def getNumOfTotalMoves(playerName: String): Int = {
      Await.result(
        moveCollection.countDocuments(equal("playerName", playerName)).toFuture(),
        5.seconds
      ).toInt
    }
