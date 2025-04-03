package core.api.service

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class ModelRequestHttpSpec extends AnyWordSpec with BeforeAndAfterAll {
  val wireMockModelServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(MODEL_PORT))

  val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
  val endpointEndPaths = List("fieldData", "gameBoardData", "playerGameData", "fieldSizeData", "currentStatus", "currentPlayer")

  override def beforeAll(): Unit =
    wireMockModelServer.start()
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
    wireMockModelServer.stop()

  "ModelRequestHttp" should {
    "throw a RuntimeException when FieldData decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.fieldData(ComputerDifficulty.Easy, field)
      }
      exception.getMessage should include("Error decoding FieldData: ")
    }
    "throw a RuntimeException when GameBoardData decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.gameBoardData(field)
      }
      exception.getMessage should include("Error decoding GameBoardData: ")
    }
    "throw a RuntimeException when PlayerGameData decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.playerGameData(field)
      }
      exception.getMessage should include("Error decoding PlayerGameData: ")
    }
    "throw a RuntimeException when FieldSizeData decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.fieldSizeData(field)
      }
      exception.getMessage should include("Error decoding FieldSizeData: ")
    }
    "throw a RuntimeException when currentStatus 'Vector[Vector[Status]]' decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.currentStatus(field)
      }
      exception.getMessage should include("Error decoding Vector[Vector[Status]]: ")
    }
    "throw a RuntimeException when Player decoding fails" in {
      val exception = intercept[RuntimeException] {
        ModelRequestHttp.currentPlayer(field)
      }
      exception.getMessage should include("Error decoding Player: ")
    }
  }
}
