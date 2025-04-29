package metric.api.module

import metric.databaseComponent.DAOInterface
import metric.databaseComponent.slick.base.connectors
import metric.databaseComponent.slick.dao.Slick

object MetricModule:
  given DAOInterface = Slick(new connectors.PostgresConnector)
