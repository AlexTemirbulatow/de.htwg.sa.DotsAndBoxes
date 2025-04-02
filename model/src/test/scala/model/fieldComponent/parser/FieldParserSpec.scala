package model.fieldComponent.parser

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field

class FieldParserSpec extends AnyWordSpec {
  "A FieldParser" when {
    "converting from json" should {
      "return the correct field" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Computer)
          .putRow(0, 0, true).putRow(1, 0, true)
          .putCol(0, 0, true).putCol(0, 1, true)
          .putStatus(0, 0, Status.Blue)
          .putStatus(0, 1, Status.Red)
          .putStatus(0, 2, Status.Green)
          .putStatus(0, 3, Status.Yellow)

        val fieldAsJsonString: String = FieldConverter.toJson(field).toString
        val parsedField: FieldInterface = FieldParser.fromJson(fieldAsJsonString)

        parsedField.getRowCell(0, 0) shouldBe true
        parsedField.getRowCell(1, 1) shouldBe false
        parsedField.getColCell(0, 0) shouldBe true
        parsedField.getColCell(1, 1) shouldBe false
        parsedField.getStatusCell(0, 0) shouldBe Status.Blue
        parsedField.getStatusCell(0, 1) shouldBe Status.Red
        parsedField.getStatusCell(0, 2) shouldBe Status.Green
        parsedField.getStatusCell(0, 3) shouldBe Status.Yellow
        parsedField.getStatusCell(1, 1) shouldBe Status.Empty

        parsedField.boardSize shouldBe BoardSize.Small
        parsedField.playerSize shouldBe PlayerSize.Three
        parsedField.playerType shouldBe PlayerType.Computer

        parsedField shouldBe field
      }
      "throw exceptions on incorrect values" in {
        val invalidBoardSizeJson = """{"field": {"boardSize": "InvalidBoardSize"}}"""
        an[RuntimeException] should be thrownBy FieldParser.fromJson(invalidBoardSizeJson)
        val exception1: RuntimeException = the [RuntimeException] thrownBy FieldParser.fromJson(invalidBoardSizeJson)
        exception1.getMessage shouldBe "Invalid Board Size."

        val invalidPlayerSizeJson = """{"field":{"boardSize":"Small","playerSize":"InvalidPlayerSize"}}"""
        an[RuntimeException] should be thrownBy FieldParser.fromJson(invalidPlayerSizeJson)
        val exception2: RuntimeException = the [RuntimeException] thrownBy FieldParser.fromJson(invalidPlayerSizeJson)
        exception2.getMessage shouldBe "Invalid Player Size."

        val invalidPlayerTypeJson = """{"field":{"boardSize":"Small","playerSize":"Three","playerType":"InvalidPlayerType"}}"""
        an[RuntimeException] should be thrownBy FieldParser.fromJson(invalidPlayerTypeJson)
        val exception3: RuntimeException = the [RuntimeException] thrownBy FieldParser.fromJson(invalidPlayerTypeJson)
        exception3.getMessage shouldBe "Invalid Player Type."
      }
    }
    "converting from xml" should {
      "return the correct field" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Computer)
          .putRow(0, 0, true).putRow(1, 0, true)
          .putCol(0, 0, true).putCol(0, 1, true)
          .putStatus(0, 0, Status.Blue)
          .putStatus(0, 1, Status.Red)
          .putStatus(0, 2, Status.Green)
          .putStatus(0, 3, Status.Yellow)

        val fieldAsXmlString: String = FieldConverter.toXml(field).toString
        val parsedField: FieldInterface = FieldParser.fromXml(fieldAsXmlString)

        parsedField.getRowCell(0, 0) shouldBe true
        parsedField.getRowCell(1, 1) shouldBe false
        parsedField.getColCell(0, 0) shouldBe true
        parsedField.getColCell(1, 1) shouldBe false
        parsedField.getStatusCell(0, 0) shouldBe Status.Blue
        parsedField.getStatusCell(0, 1) shouldBe Status.Red
        parsedField.getStatusCell(0, 2) shouldBe Status.Green
        parsedField.getStatusCell(0, 3) shouldBe Status.Yellow
        parsedField.getStatusCell(1, 1) shouldBe Status.Empty

        parsedField.boardSize shouldBe BoardSize.Small
        parsedField.playerSize shouldBe PlayerSize.Three
        parsedField.playerType shouldBe PlayerType.Computer

        parsedField shouldBe field
      }
      "throw exceptions on incorrect values" in {
        val invalidBoardSizeXml = """<field rowSize="4" colSize="3"><playerList boardSize="InvalidBoardSize"/></field>"""
        an[RuntimeException] should be thrownBy FieldParser.fromXml(invalidBoardSizeXml)
        val exception1 = the [RuntimeException] thrownBy FieldParser.fromXml(invalidBoardSizeXml)
        exception1.getMessage shouldBe "Invalid Board Size."

        val invalidPlayerSizeXml = """<field rowSize="4" colSize="3"><playerList boardSize="Small" playerSize="InvalidPlayerSize"/></field>"""
        an[RuntimeException] should be thrownBy FieldParser.fromXml(invalidPlayerSizeXml)
        val exception2 = the [RuntimeException] thrownBy FieldParser.fromXml(invalidPlayerSizeXml)
        exception2.getMessage shouldBe "Invalid Player Size."

        val invalidPlayerTypeXml = """<field rowSize="4" colSize="3"><playerList boardSize="Small" playerSize="Three" playerType="InvalidPlayerType"/></field>"""
        an[RuntimeException] should be thrownBy FieldParser.fromXml(invalidPlayerTypeXml)
        val exception3 = the [RuntimeException] thrownBy FieldParser.fromXml(invalidPlayerTypeXml)
        exception3.getMessage shouldBe "Invalid Player Type."
      }
    }
  }
}
