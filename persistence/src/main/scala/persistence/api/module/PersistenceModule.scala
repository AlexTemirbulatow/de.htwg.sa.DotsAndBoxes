package persistence.api.module

import persistence.databaseComponent.DAOInterface
import persistence.databaseComponent.slick.connector
import persistence.databaseComponent.slick.dao.Slick

object PersistenceModule:
  given DAOInterface = Slick(new connector.PostgresConnector)
