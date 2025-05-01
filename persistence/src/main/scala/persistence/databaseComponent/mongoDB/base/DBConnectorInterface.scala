package persistence.databaseComponent.mongoDB.base

import akka.Done
import scala.concurrent.Future
import org.mongodb.scala.MongoDatabase

trait DBConnectorInterface:
  val db: MongoDatabase
  def connect: Unit
  def disconnect: Future[Done]
