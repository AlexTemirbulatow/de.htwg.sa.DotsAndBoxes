package metric.databaseComponent.slick.dao

import metric.databaseComponent.DAOInterface
import metric.databaseComponent.slick.base.DBConnectorInterface
import metric.databaseComponent.slick.schema.MoveTable
import org.slf4j.LoggerFactory
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Try
import slick.jdbc.PostgresProfile.api._

object Slick:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new SlickDAO(dbConnector)

  private class SlickDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    private def moveTable = TableQuery[MoveTable](new MoveTable(_))

    dbConnector.connect(
      DBIO.seq(
        moveTable.schema.dropIfExists,
        moveTable.schema.createIfNotExists
      )
    )

    override def create(timestamp: Long, playerName: String): Try[Int] = Try {
      val moveID: Int = Await.result(insertMove(timestamp, playerName), 5.seconds)
      moveID
    }

    private def insertMove(timestamp: Long, playerName: String): Future[Int] =
      val insertAction = (
        moveTable.map(g => (g.timestamp, g.playerName)) returning moveTable.map(_.moveID)
      ) += (timestamp, playerName)
      dbConnector.db.run(insertAction)

    override def getTotalGameDuration: Int =
      val lookupAction = moveTable
        .map(m => m.timestamp)
        .sortBy(_.asc)
        .take(1)
        .join(moveTable
          .map(m => m.timestamp)
          .sortBy(_.desc)
          .take(1))
        .on((a, b) => true)

      val result: Vector[(Long, Long)] = Await.result(dbConnector.db.run(lookupAction.result), 5.seconds).toVector

      result.headOption match
        case Some((firstTimestamp, lastTimestamp)) => Math.round((lastTimestamp - firstTimestamp) / 1000).toInt
        case None => 0

    override def getAvgMoveDuration(playerName: String): Int =
      val lookupAction = moveTable
        .filter(_.playerName === playerName)
        .sortBy(_.timestamp)
        .map(_.timestamp)

      val result: Vector[Long] = Await.result(dbConnector.db.run(lookupAction.result), 5.seconds).toVector

      if result.size > 1 then
        val diffs = result.sliding(2).collect {
          case Seq(prev, next) => next - prev
        }.toSeq
        Math.round(diffs.sum.toDouble / diffs.size / 1000).toInt
      else 0

    override def getMinMoveDuration(playerName: String): Int =
      val lookupAction = moveTable
        .filter(_.playerName === playerName)
        .sortBy(_.timestamp)
        .map(_.timestamp)

      val result: Vector[Long] = Await.result(dbConnector.db.run(lookupAction.result), 5.seconds).toVector

      if result.size > 1 then
        val diffs = result.sliding(2).collect {
          case Seq(prev, next) => next - prev
        }.toSeq
        Math.round(diffs.min.toDouble / 1000).toInt
      else 0

    override def getMaxMoveDuration(playerName: String): Int =
      val lookupAction = moveTable
        .filter(_.playerName === playerName)
        .sortBy(_.timestamp)
        .map(_.timestamp)

      val result: Vector[Long] = Await.result(dbConnector.db.run(lookupAction.result), 5.seconds).toVector

      if result.size > 1 then
        val diffs = result.sliding(2).collect {
          case Seq(prev, next) => next - prev
        }.toSeq
        Math.round(diffs.max.toDouble / 1000).toInt
      else 0

    override def getLongestMoveStreak(playerName: String): Int =
      val lookupAction = moveTable
        .sortBy(_.timestamp)
        .map(_.playerName)

      val result: Vector[String] =
        Await.result(dbConnector.db.run(lookupAction.result), 5.seconds).toVector

      result.foldLeft((0, 0)) { case ((maxStreak, currentStreak), name) =>
        if name == playerName then
          val newStreak = currentStreak + 1
          (math.max(maxStreak, newStreak), newStreak)
        else
          (maxStreak, 0)
      }._1

    override def getNumOfTotalMoves(playerName: String): Int =
      val lookupAction = moveTable
        .filter(_.playerName === playerName)
        .length
      Await.result(dbConnector.db.run(lookupAction.result), 5.seconds)

