package persistence.api.module

import persistence.databaseComponent.DAOInterface
import persistence.databaseComponent.slick.base.DBConnectorInterface
import persistence.databaseComponent.slick.base.connectors.PostgresConnector
import persistence.databaseComponent.slick.dao.SlickDAO

object PersistenceModule:
  given DBConnectorInterface = new PostgresConnector
  given DAOInterface = new SlickDAO
