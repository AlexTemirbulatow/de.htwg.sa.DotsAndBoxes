package lib

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

class GameConfigSpec extends AnyWordSpec {
  "GameConfig" when {
    "accessing enums" should {
      "have the right dimensions for board sizes" in {
        BoardSize.Small.dimensions shouldBe (4, 3)
        BoardSize.Medium.dimensions shouldBe (5, 4)
        BoardSize.Large.dimensions shouldBe (8, 6)
      }
      "have the right sizes for player size" in {
        PlayerSize.Two.size shouldBe 2
        PlayerSize.Three.size shouldBe 3
        PlayerSize.Four.size shouldBe 4
      }
      "have the right player type number" in {
        PlayerType.Human.number shouldBe 1
        PlayerType.Computer.number shouldBe 2
      }
      "have the right computer difficulty number" in {
        ComputerDifficulty.Easy.number shouldBe 1
        ComputerDifficulty.Medium.number shouldBe 2
        ComputerDifficulty.Hard.number shouldBe 3
      }
    }
    "converting" should {
      "have the right dimension" in {
        lib.GameConfig.boardSizes("1") shouldBe BoardSize.Small
        lib.GameConfig.boardSizes("2") shouldBe BoardSize.Medium
        lib.GameConfig.boardSizes("3") shouldBe BoardSize.Large
        lib.GameConfig.boardSizes("4") shouldBe BoardSize.Medium
      }
      "have the right player size" in {
        lib.GameConfig.playerSizes("2") shouldBe PlayerSize.Two
        lib.GameConfig.playerSizes("3") shouldBe PlayerSize.Three
        lib.GameConfig.playerSizes("4") shouldBe PlayerSize.Four
        lib.GameConfig.playerSizes("5") shouldBe PlayerSize.Two
      }
      "have the right player type" in {
        lib.GameConfig.playerType("1") shouldBe PlayerType.Human
        lib.GameConfig.playerType("2") shouldBe PlayerType.Computer
        lib.GameConfig.playerType("3") shouldBe PlayerType.Human
      }
      "have the right computer difficulty based on string" in {
        lib.GameConfig.computerDifficulty("1") shouldBe ComputerDifficulty.Easy
        lib.GameConfig.computerDifficulty("2") shouldBe ComputerDifficulty.Medium
        lib.GameConfig.computerDifficulty("3") shouldBe ComputerDifficulty.Hard
        lib.GameConfig.computerDifficulty("4") shouldBe ComputerDifficulty.Medium
      }
    }
  }
}
