package persistence.gatling

import common.config.ServiceConfig.PERSISTENCE_BASE_URL
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import model.fieldComponent.fieldImpl.Field
import scala.concurrent.duration._

abstract class GatlingSimulationConfig extends Simulation:
  val operations: List[ChainBuilder]
  def executeOperations(): Unit

  val fieldAsJsonString = FieldConverter
    .toJson(new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Computer))
    .toString

  val httpProtocol = http
    .baseUrl(PERSISTENCE_BASE_URL)
    .inferHtmlResources(
      AllowList(),
      DenyList(
        """.*\.js""",
        """.*\.css""",
        """.*\.gif""",
        """.*\.jpeg""",
        """.*\.jpg""",
        """.*\.ico""",
        """.*\.woff""",
        """.*\.woff2""",
        """.*\.(t|o)tf""",
        """.*\.png""",
        """.*detectportal\.firefox\.com.*"""
      )
    )
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("en,de-DE;q=0.9,de;q=0.8,en-US;q=0.7")
    .doNotTrackHeader("1")
    .upgradeInsecureRequestsHeader("1")
    .userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36")

  private val header = Map(
    "Content-Type" -> "application/json",
    "Sec-Fetch-Dest" -> "document",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "none",
    "Sec-Fetch-User" -> "?1",
    "sec-ch-ua" -> """Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99""",
    "sec-ch-ua-mobile" -> "?0",
    "sec-ch-ua-platform" -> "Windows"
  )

  def buildOperation(name: String, method: String, endpoint: String, body: Body): ChainBuilder =
    exec(
      http(name)
        .httpRequest(method, endpoint)
        .headers(header)
        .body(body)
    )

  def buildOperation(name: String, method: String, endpoint: String): ChainBuilder =
    exec(
      http(name)
        .httpRequest(method, endpoint)
        .headers(header)
    )

  def buildScenario(name: String) =
    scenario(name)
      .exec(
        operations.reduce((a, b) => a.pause(1.second).exec(b))
      )
