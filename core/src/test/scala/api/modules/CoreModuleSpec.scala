package api.module

import CoreModule.given
import common.model.fieldService.FieldInterface
import controllerComponent.ControllerInterface
import de.github.dotsandboxes.lib._
import model.fieldComponent.fieldImpl.Field
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

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
    "initialize a correct fileFormat" in {
      val fileFormat: FileFormat = summon[FileFormat]
      fileFormat should not be null
      fileFormat shouldBe a[FileFormat]
      fileFormat should (be(FileFormat.JSON) or be(FileFormat.XML))
    }
    "initialize a correct computerDifficulty" in {
      val initComputer: ComputerDifficulty = summon[ComputerDifficulty]
      initComputer should not be null
      initComputer shouldBe a[ComputerDifficulty]
      initComputer should (be(ComputerDifficulty.Easy) or be(ComputerDifficulty.Medium) or be(ComputerDifficulty.Hard))
    }
    "initialize a correct controller interface" in {
      val initController: ControllerInterface = summon[ControllerInterface]
      initController should not be null
      initController shouldBe a[ControllerInterface]
    }
  }
}
