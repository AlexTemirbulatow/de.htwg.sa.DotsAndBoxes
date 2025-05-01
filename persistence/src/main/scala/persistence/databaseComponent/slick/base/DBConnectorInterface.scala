package persistence.databaseComponent.slick.base

import akka.Done
import scala.concurrent.Future
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcBackend.JdbcDatabaseDef

trait DBConnectorInterface:
  val db: JdbcDatabaseDef
  def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit
  def disconnect: Future[Done]
