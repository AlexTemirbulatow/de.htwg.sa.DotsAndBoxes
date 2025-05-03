package metric.api.module

import metric.databaseComponent.DAOInterface
import metric.databaseComponent.mongoDB.base.connector.MongoDBConnector
import metric.databaseComponent.mongoDB.dao.Mongo
import metric.databaseComponent.slick.base.connectors.PostgresConnector
import metric.databaseComponent.slick.dao.Slick

object MetricModule:
  //given DAOInterface = Slick(new PostgresConnector)
  given DAOInterface = Mongo(new MongoDBConnector)
