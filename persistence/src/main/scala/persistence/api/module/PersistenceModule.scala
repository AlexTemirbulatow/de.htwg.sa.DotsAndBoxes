package persistence.api.module

import persistence.databaseComponent.DAOInterface
import persistence.databaseComponent.mongoDB.base.connector.MongoDBConnector
import persistence.databaseComponent.mongoDB.dao.Mongo
import persistence.databaseComponent.slick.base.connector.{H2Connector, PostgresConnector}
import persistence.databaseComponent.slick.dao.Slick

object PersistenceModule:
  //given DAOInterface = Slick(new PostgresConnector)
  given DAOInterface = Mongo(new MongoDBConnector)
