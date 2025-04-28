package persistence.databaseComponent.slick.base

import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.JdbcDatabaseDef

trait DBConnectorInterface:
  val db: JdbcDatabaseDef
  def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit
