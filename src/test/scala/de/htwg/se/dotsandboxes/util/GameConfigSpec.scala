package de.htwg.se.dotsandboxes.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._

import de.htwg.se.dotsandboxes.model.computerComponent.computerEasyImpl.ComputerEasy
import de.htwg.se.dotsandboxes.model.computerComponent.computerMediumImpl.ComputerMedium
import de.htwg.se.dotsandboxes.model.computerComponent.computerHardImpl.ComputerHard

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
        GameConfig.boardSizes("1") shouldBe BoardSize.Small
        GameConfig.boardSizes("2") shouldBe BoardSize.Medium
        GameConfig.boardSizes("3") shouldBe BoardSize.Large
        GameConfig.boardSizes("4") shouldBe BoardSize.Medium
      }
      "have the right player size" in {
        GameConfig.playerSizes("2") shouldBe PlayerSize.Two
        GameConfig.playerSizes("3") shouldBe PlayerSize.Three
        GameConfig.playerSizes("4") shouldBe PlayerSize.Four
        GameConfig.playerSizes("5") shouldBe PlayerSize.Two
      }
      "have the right player type" in {
        GameConfig.playerType("1") shouldBe PlayerType.Human
        GameConfig.playerType("2") shouldBe PlayerType.Computer
        GameConfig.playerType("3") shouldBe PlayerType.Human
      }
      "have the right computer difficulty based on string" in {
        GameConfig.computerDifficulty("1") shouldBe ComputerDifficulty.Easy
        GameConfig.computerDifficulty("2") shouldBe ComputerDifficulty.Medium
        GameConfig.computerDifficulty("3") shouldBe ComputerDifficulty.Hard
        GameConfig.computerDifficulty("4") shouldBe ComputerDifficulty.Medium
      }
    }
  }
}
