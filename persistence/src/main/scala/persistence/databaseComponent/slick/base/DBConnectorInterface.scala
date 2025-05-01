package persistence.databaseComponent.slick.base

import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.JdbcDatabaseDef
import scala.concurrent.Future
import akka.Done

trait DBConnectorInterface:
  val db: JdbcDatabaseDef
  def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit
  def disconnect: Future[Done]
