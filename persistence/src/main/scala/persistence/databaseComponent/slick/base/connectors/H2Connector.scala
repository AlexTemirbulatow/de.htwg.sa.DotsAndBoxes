package persistence.databaseComponent.slick.base.connectors

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import common.config.DatabaseConfig._
import org.slf4j.LoggerFactory
import persistence.databaseComponent.slick.base.DBConnectorInterface
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.{Database, JdbcDatabaseDef}

class H2Connector extends DBConnectorInterface:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-postgres-connection") { () =>
    disconnect
  }

  override val db = Database.forURL(
    url = PERSISTENCE_DB_H2_URL,
    driver = PERSISTENCE_DB_H2_DRIVER
  )

  override def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    logger.info("Persistence Service [Database] -- Connecting to H2 database...")
    retry(PERSISTENCE_DB_H2_CONN_RETRY_ATTEMPTS, setup)(db)

  private def retry(retries: Int, setup: DBIOAction[Unit, NoStream, Effect.Schema])(database: => JdbcDatabaseDef): Unit =
    Try(Await.result(database.run(setup), 5.seconds)) match
      case Success(_) => logger.info("Persistence Service [Database] -- H2 database connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Persistence Service [Database] -- H2 database connection failed - retrying... (${PERSISTENCE_DB_H2_CONN_RETRY_ATTEMPTS - retries + 1}/$PERSISTENCE_DB_H2_CONN_RETRY_ATTEMPTS): ${exception.getMessage}")
        Thread.sleep(PERSISTENCE_DB_H2_CONN_RETRY_WAIT_TIME)
        retry(retries - 1, setup)(database)
      case Failure(exception) => logger.error(s"Persistence Service [Database] -- Could not establish a connection to the H2 database: ${exception.getMessage}")

  override def disconnect: Future[Done] =
    logger.info("Persistence Service [Database] -- Closing H2 database connection...")
    db.close
    Future.successful(Done)
