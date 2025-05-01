package persistence.api.module

import persistence.databaseComponent.DAOInterface
import persistence.databaseComponent.slick.dao.Slick
import persistence.databaseComponent.slick.connector

object PersistenceModule:
  given DAOInterface = Slick(new connector.PostgresConnector)
