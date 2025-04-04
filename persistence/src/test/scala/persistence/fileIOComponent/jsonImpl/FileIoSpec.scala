package fileIOComponent.jsonImpl

import common.config.ServiceConfig.FILEIO_FILENAME
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.parser.FieldParser
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import persistence.fileIOComponent.jsonImpl.FileIO

class FileIoSpec extends AnyWordSpec {

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

  private def fieldFromJsonString(fieldValue: String): FieldInterface =
    FieldParser.fromJson(fieldValue)

  "A game state" when {
    "saved to json" should {
      "be equal when loaded" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
        val fileIO = new FileIO()
        fileIO.save(fieldToJsonString(field), FILEIO_FILENAME)
        val fieldValue = fileIO.load(FILEIO_FILENAME) match
          case Left(value)  => value
          case Right(value) => value
        val loadedField: FieldInterface = fieldFromJsonString(fieldValue._1)
        loadedField should be(field)
      }
      "return the correct game state" in {
        val field: FieldInterface = new Field(BoardSize.Medium, Status.Empty, PlayerSize.Four, PlayerType.Computer)
          .putRow(0, 0, true).putRow(0, 4, true).putRow(4, 0, true).putRow(4, 4, true).putRow(2, 2, true)
          .putCol(0, 0, true).putCol(0, 5, true).putCol(3, 0, true).putCol(3, 5, true).putCol(2, 2, true)
          .putStatus(0, 0, Status.Blue).putStatus(0, 4, Status.Red).putStatus(3, 0, Status.Green).putStatus(3, 4, Status.Yellow).putStatus(2, 2, Status.Blue)
        val fileIO = new FileIO()
        fileIO.save(fieldToJsonString(field), FILEIO_FILENAME)

        val fieldValue = fileIO.load(FILEIO_FILENAME) match
          case Left(value)  => value
          case Right(value) => value
        val loadedField: FieldInterface = fieldFromJsonString(fieldValue._1)

        loadedField.getRowCell(0, 0) shouldBe true
        loadedField.getRowCell(0, 4) shouldBe true
        loadedField.getRowCell(4, 0) shouldBe true
        loadedField.getRowCell(4, 4) shouldBe true
        loadedField.getRowCell(2, 2) shouldBe true
        loadedField.getRowCell(1, 1) shouldBe false

        loadedField.getColCell(0, 0) shouldBe true
        loadedField.getColCell(0, 5) shouldBe true
        loadedField.getColCell(3, 0) shouldBe true
        loadedField.getColCell(3, 5) shouldBe true
        loadedField.getColCell(2, 2) shouldBe true
        loadedField.getColCell(1, 1) shouldBe false

        loadedField.getStatusCell(0, 0) shouldBe Status.Blue
        loadedField.getStatusCell(0, 4) shouldBe Status.Red
        loadedField.getStatusCell(3, 0) shouldBe Status.Green
        loadedField.getStatusCell(3, 4) shouldBe Status.Yellow
        loadedField.getStatusCell(2, 2) shouldBe Status.Blue
        loadedField.getStatusCell(1, 1) shouldBe Status.Empty

        loadedField.boardSize shouldBe BoardSize.Medium
        loadedField.playerSize shouldBe PlayerSize.Four
        loadedField.playerType shouldBe PlayerType.Computer
      }
      "return a finished game state" in {
        val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer)
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 1, true).putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 3, true).putCol(2, 4, true)
        val fileIO = new FileIO()

        field.isFinished shouldBe true
        fileIO.save(fieldToJsonString(field), FILEIO_FILENAME)
        val fieldValue = fileIO.load(FILEIO_FILENAME) match
          case Left(value)  => value
          case Right(value) => value
        val loadedField: FieldInterface = fieldFromJsonString(fieldValue._1)
        loadedField.isFinished shouldBe true
      }
      "return Left if saving went wrong" in {
        val fileIO = new FileIO()
        fileIO.save(null, FILEIO_FILENAME) should matchPattern { case Left(_) => }
      }
      "return Left if loading went wrong" in {
        val fileIO = new FileIO()
        val fieldValue = fileIO.load("InvalidFilename") match
          case Left(value)  => value
          case Right(value) => value
        fieldValue._1 should include("InvalidFilename.json")
        fieldValue._2 shouldBe "InvalidFilename.json"
      }
    }
  }
}
