package common.config

import com.typesafe.config.{Config, ConfigFactory}

object DatabaseConfig:
  private val config: Config = ConfigFactory.load()

  val PERSISTENCE_DB_POSTGRES_URL                   = config.getString("db.postgresUrl")
  val PERSISTENCE_DB_POSTGRES_USER                  = config.getString("db.postgresUser")
  val PERSISTENCE_DB_POSTGRES_PASS                  = config.getString("db.postgresPass")
  val PERSISTENCE_DB_POSTGRES_DRIVER                = config.getString("db.postgresDriver")
  val PERSISTENCE_DB_POSTGRES_CONN_RETRY_ATTEMPTS   = config.getInt("db.postgresConnRetryAttempts")
  val PERSISTENCE_DB_POSTGRES_CONN_RETRY_WAIT_TIME  = config.getInt("db.postgresConnRetryWaitTime")
