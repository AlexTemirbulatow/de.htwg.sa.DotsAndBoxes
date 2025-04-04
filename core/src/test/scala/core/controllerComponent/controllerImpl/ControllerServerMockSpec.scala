package core.controllerComponent.controllerImpl

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import common.config.ServiceConfig.{COMPUTER_HOST, COMPUTER_PORT}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import io.circe.generic.auto._
import io.circe.syntax._
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ControllerServerMockSpec extends AnyWordSpec with BeforeAndAfterAll {
  private val wireMockComputerServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(COMPUTER_PORT))

  override def beforeAll(): Unit =
    wireMockComputerServer.start()
    configureFor(COMPUTER_HOST, COMPUTER_PORT)

  override def afterAll(): Unit =
    wireMockComputerServer.stop()

  "The Controller" should {
    "handle a bad computer move and return the same field" in {
      val initialField: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer)
      val initialFieldJsonString: String = FieldConverter.toJson(initialField).toString

      val controller = new Controller(using initialField, FileFormat.JSON, ComputerDifficulty.Hard)

      stubFor(
        post(urlEqualTo("/api/computer/get/move"))
          .willReturn(
            aResponse()
              .withStatus(200)
              .withHeader("Content-Type", "application/json")
              .withBody(Move(9, 9, 9, true).asJson.toString)
          )
      )

      val newField: FieldInterface = Await.result(controller.computerMove(controller.field), 10.seconds)
      val gameBoardData: GameBoardData = newField.gameBoardData
      val allCellState: Vector[Boolean] = gameBoardData.rowCells.flatten ++ gameBoardData.colCells.flatten

      allCellState.count(identity) shouldBe 0
      newField shouldBe initialField
    }
  }
}
