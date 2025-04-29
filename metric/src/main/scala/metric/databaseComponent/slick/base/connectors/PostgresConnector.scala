package metric.databaseComponent.slick.base.connectors

import common.config.DatabaseConfig._
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.{Database, JdbcDatabaseDef}
import metric.databaseComponent.slick.base.DBConnectorInterface

class PostgresConnector extends DBConnectorInterface:
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  override val db = Database.forURL(
    url = METRIC_DB_POSTGRES_URL,
    user = METRIC_DB_POSTGRES_USER,
    password = METRIC_DB_POSTGRES_PASS,
    driver = METRIC_DB_POSTGRES_DRIVER
  )

  override def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit =
    logger.info("Metric Service -- Connecting to postgres database...")
    retry(METRIC_DB_POSTGRES_CONN_RETRY_ATTEMPTS, setup)(db)

  private def retry(retries: Int, setup: DBIOAction[Unit, NoStream, Effect.Schema])(database: => JdbcDatabaseDef): Unit =
    Try(Await.result(database.run(setup), 5.seconds)) match
      case Success(_) => logger.info("Metric Service -- Postgres database connection established")
      case Failure(exception) if retries > 0 =>
        logger.warn(s"Metric Service -- Postgres database connection failed - retrying... (${METRIC_DB_POSTGRES_CONN_RETRY_ATTEMPTS - retries + 1}/$METRIC_DB_POSTGRES_CONN_RETRY_ATTEMPTS): ${exception.getMessage}")
        Thread.sleep(METRIC_DB_POSTGRES_CONN_RETRY_WAIT_TIME)
        retry(retries - 1, setup)(database)
      case Failure(exception) => logger.error(s"Metric Service -- Could not establish a connection to the postgres database: ${exception.getMessage}")

  override def disconnect: Unit =
    logger.info("Metric Service -- Closing postgres database connection...")
    db.close
