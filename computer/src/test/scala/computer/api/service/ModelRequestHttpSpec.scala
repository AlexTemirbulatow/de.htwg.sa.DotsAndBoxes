package computer.api.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class AModelRequestHttpSpec extends AnyWordSpec with BeforeAndAfterAll {
  private val wireMockComputerServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(MODEL_PORT))

  private val fieldJsonString: String = FieldConverter.toJson(
    new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
  ).toString
  private val endpointEndPaths = List("allAvailableCoords", "winningMoves", "saveMoves", "missingMoves", "chainsWithPointsOutcome")

  override def beforeAll(): Unit =
    wireMockComputerServer.start()
    configureFor(MODEL_HOST, MODEL_PORT)
    endpointEndPaths.foreach { endPath =>
      stubFor(
        post(urlEqualTo(s"/api/model/field/get/$endPath"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody("invalid json")
          )
      )
    }

  override def afterAll(): Unit =
    wireMockComputerServer.stop()

  "ModelRequestHttp" should {
    "throw a RuntimeException when allAvailableCoords Vector[(Int, Int, Int)] decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.allAvailableCoords(fieldJsonString)
      }
      exception.getMessage should include("Error decoding Vector[(Int, Int, Int)]: ")
    }
    "throw a RuntimeException when winningMoves Vector[Move] decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.winningMoves(fieldJsonString, Vector.empty)
      }
      exception.getMessage should include("Error decoding Vector[Move]: ")
    }
    "throw a RuntimeException when saveMoves Vector[Move] decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.saveMoves(fieldJsonString, Vector.empty)
      }
      exception.getMessage should include("Error decoding Vector[Move]: ")
    }
    "throw a RuntimeException when missingMoves Vector[(Int, Int, Int)] decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.missingMoves(fieldJsonString, 1, 0, 0)
      }
      exception.getMessage should include("Error decoding Vector[(Int, Int, Int)]: ")
    }
    "throw a RuntimeException when chainsWithPointsOutcome Vector[(Int, Vector[(Int, Int, Int)])] decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.chainsWithPointsOutcome(fieldJsonString, Vector.empty)
      }
      exception.getMessage should include("Error decoding Vector[(Int, Vector[(Int, Int, Int)])]: ")
    }
  }
}
