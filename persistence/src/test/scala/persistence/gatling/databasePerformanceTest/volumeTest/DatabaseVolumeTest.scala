package persistence.gatling.databasePerformanceTest.volumeTest

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import persistence.gatling.GatlingSimulationConfig
import play.api.libs.json.Json
import scala.concurrent.duration._

class DatabaseVolumeTest extends GatlingSimulationConfig:
  private val USERS: Int = 10
  private val REPEAT_COUNT = 10000

  override val operations: List[ChainBuilder] = List(
    repeat(REPEAT_COUNT) {
      buildOperation(
        "save field (volume)",
        "POST",
        "/api/persistence/database/save",
        StringBody(Json.obj("field" -> fieldAsJsonString).toString)
      )
    },
    repeat(REPEAT_COUNT) {
      buildOperation(
        "load field (volume)",
        "GET",
        "/api/persistence/database/load"
      )
    }
  )

  override def executeOperations(): Unit =
    val scn = buildScenario("Database Volume Test Scenario")
    setUp(
      scn.inject(atOnceUsers(USERS))
    ).protocols(httpProtocol)

  executeOperations()
