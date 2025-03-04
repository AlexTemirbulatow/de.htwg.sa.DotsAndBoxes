import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import CoreModule.given
import fieldComponent.FieldInterface
import fieldComponent.fieldImpl.Field
import lib.{BoardSize, PlayerSize, PlayerType, Status}
import fileIoComponent.FileIOInterface
import fileIoComponent.{jsonImpl, xmlImpl}
import computerComponent.ComputerInterface
import computerComponent.computerEasyImpl.ComputerEasy
import computerComponent.computerMediumImpl.ComputerMedium
import computerComponent.computerHardImpl.ComputerHard
import controllerComponent.ControllerInterface
import controllerComponent.controllerImpl.Controller

class CoreModuleSpec extends AnyWordSpec {
  "CoreModule" should {
    "initialize and return a correct field interface" in {
      val initField: FieldInterface = summon[FieldInterface]
      initField should not be null
      initField shouldBe a[FieldInterface]
      initField shouldBe new Field(BoardSize.Medium, Status.Empty, PlayerSize.Two, PlayerType.Human)
      initField.boardSize shouldBe BoardSize.Medium
      initField.playerSize shouldBe PlayerSize.Two
      initField.playerType shouldBe PlayerType.Human
    }
    "initialize a correct fileIO interface" in {
      val initFileIO: FileIOInterface = summon[FileIOInterface]
      initFileIO should not be null
      initFileIO shouldBe a[FileIOInterface]
      initFileIO should (be (a [jsonImpl.FileIO]) or be (a [xmlImpl.FileIO]))
    }
    "initialize a correct computer interface" in {
      val initComputer: ComputerInterface = summon[ComputerInterface]
      initComputer should not be null
      initComputer shouldBe a[ComputerInterface]
      initComputer should (be (a [ComputerMedium]) or be (a [ComputerEasy]) or be (a [ComputerHard]))
    }
    "initialize a correct controller interface" in {
      val initController: ControllerInterface = summon[ControllerInterface]
      initController should not be null
      initController shouldBe a[ControllerInterface]
    }
  }
}