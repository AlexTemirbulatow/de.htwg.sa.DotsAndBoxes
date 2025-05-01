package persistence.databaseComponent

import akka.Done
import scala.concurrent.Future
import _root_.slick.dbio.{DBIOAction, Effect, NoStream}
import _root_.slick.jdbc.JdbcBackend.JdbcDatabaseDef

trait DBConnectorInterface:
  val db: JdbcDatabaseDef
  def connect(setup: DBIOAction[Unit, NoStream, Effect.Schema]): Unit
  def disconnect: Future[Done]
