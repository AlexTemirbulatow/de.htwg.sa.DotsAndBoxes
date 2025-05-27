package persistence.gatling.databasePerformanceTest.stressTest

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import persistence.gatling.GatlingSimulationConfig
import play.api.libs.json.Json
import scala.concurrent.duration._

class DatabaseStressTest extends GatlingSimulationConfig:
  private val USERS: Int = 3000
  private val USER_SPREAD_TIME = 20.seconds

  override val operations: List[ChainBuilder] = List(
    buildOperation(
      "save field",
      "POST",
      "/api/persistence/database/save",
      StringBody(Json.obj("field" -> fieldAsJsonString).toString)
    ),
    buildOperation(
      "load field",
      "GET",
      "/api/persistence/database/load"
    )
  )

  override def executeOperations(): Unit =
    val scn = buildScenario("Database Stress Test Scenario")
    setUp(
      scn.inject(
        stressPeakUsers(USERS) during (USER_SPREAD_TIME)
      )
    ).protocols(httpProtocol)

  executeOperations()
