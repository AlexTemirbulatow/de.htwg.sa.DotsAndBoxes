package core.api.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import common.config.ServiceConfig.{COMPUTER_HOST, COMPUTER_PORT}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ComputerRequestHttpSpec extends AnyWordSpec with BeforeAndAfterAll {
  private val wireMockComputerServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(COMPUTER_PORT))

  private def fieldToJson(field: FieldInterface) =
    FieldConverter.toJson(field)

  override def beforeAll(): Unit =
    wireMockComputerServer.start()
    configureFor(COMPUTER_HOST, COMPUTER_PORT)

  override def afterAll(): Unit =
    wireMockComputerServer.stop()

  "ComputerRequestHttp" should {
    "throw a RuntimeException when Move decoding fails" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val fieldValue = fieldToJson(field).toString

      stubFor(
        post(urlEqualTo("/api/computer/get/move"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody("invalid json")
          )
      )

      val exception = intercept[RuntimeException] {
        ComputerRequestHttp.calculateMove(fieldValue, ComputerDifficulty.Easy)
      }

      exception.getMessage should include("Error decoding Move: ")
    }
  }
}
