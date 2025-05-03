package persistence.databaseComponent.mongoDB.base.connector

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import common.config.DatabaseConfig._
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.{MongoClient, MongoDatabase, ObservableFuture}
import org.slf4j.LoggerFactory
import persistence.databaseComponent.mongoDB.base.DBConnectorInterface
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

class MongoDBConnector extends DBConnectorInterface:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-mongoDB-connection") { () =>
    disconnect
  }

  private lazy val client: MongoClient =
    logger.info("Persistence Service [Database] -- Creating MongoDB client object...")
    MongoClient(PERSISTENCE_DB_MONGO_URL)

  override val db: MongoDatabase = client.getDatabase(PERSISTENCE_DB_MONGO_DATABASE_NAME)

  override def connect: Unit =
    logger.info("Persistence Service [Database] -- Connecting to MongoDB...")
    retry(PERSISTENCE_DB_MONGO_CONN_RETRY_ATTEMPTS)

  private def retry(retries: Int): Unit =
    Try {
      Await.result(db.runCommand(Document("ping" -> 1)).toFuture(), 5.seconds)
    } match
      case Success(_) => logger.info("Persistence Service [Database] -- MongoDB connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Persistence Service [Database] -- MongoDB connection failed - retrying... (${PERSISTENCE_DB_MONGO_CONN_RETRY_ATTEMPTS - retries + 1}/$PERSISTENCE_DB_MONGO_CONN_RETRY_ATTEMPTS): ${exception.getMessage}")
        Thread.sleep(PERSISTENCE_DB_MONGO_CONN_RETRY_WAIT_TIME)
        retry(retries - 1)
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not establish a connection to MongoDB: ${exception.getMessage}")

  override def disconnect: Future[Done] =
    logger.info("Persistence Service [Database] -- Closing MongoDB connection...")
    client.close()
    Future.successful(Done)
