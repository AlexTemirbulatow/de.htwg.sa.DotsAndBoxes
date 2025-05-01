package persistence.databaseComponent.mongoDB.dao

import org.slf4j.LoggerFactory
import persistence.databaseComponent.mongoDB.base.DBConnectorInterface
import persistence.databaseComponent.{DAOInterface, GameTableData}
import scala.util.{Failure, Success, Try}

object Mongo:
  def apply(dbConnector: DBConnectorInterface): DAOInterface = new MongoDAO(dbConnector)

  private class MongoDAO(dbConnector: DBConnectorInterface) extends DAOInterface:
    private val logger = LoggerFactory.getLogger(getClass.getName.init)

    dbConnector.connect
    create match
      case Success(_)         => logger.info(s"Persistence Service [Database] -- Initial collection successfully created")
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not create initial collection: ${exception.getMessage}")

    override def create: Try[Int] = ???

    override def read: Try[GameTableData] = ???

    override def update(gameTableData: GameTableData): Try[Int] = ???

    override def delete: Try[Int] = ???
