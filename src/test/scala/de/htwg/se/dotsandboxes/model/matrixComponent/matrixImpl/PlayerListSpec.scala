package de.htwg.se.dotsandboxes.model
package matrixComponent.matrixImpl

import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.dotsandboxes.util.PlayerType
import de.htwg.se.dotsandboxes.util.GameConfig.playerType

class PlayerListSpec extends AnyWordSpec {
  "Player" when {
    "creating a Player" should {
      "return the correct data" in {
        val player = Player("player1", 100, Status.Blue, PlayerType.Computer)
        player.playerId shouldBe "player1"
        player.points shouldBe 100
        player.status shouldBe Status.Blue
        player.playerType shouldBe PlayerType.Computer
      }
    }
    "accessing list" should {
      "return the correct player and list" in {
        list should be(List(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human), Player("Yellow", 0, Status.Yellow, PlayerType.Human)))
        list shouldBe a[List[Player]]
        list.size should be(4)

        list.head should be(Player("Blue", 0, Status.Blue, PlayerType.Human))
        list.last should be(Player("Yellow", 0, Status.Yellow, PlayerType.Human))

        list(0) should be(Player("Blue", 0, Status.Blue, PlayerType.Human))
        list(1) should be(Player("Red", 0, Status.Red, PlayerType.Human))
        list(2) should be(Player("Green", 0, Status.Green, PlayerType.Human))
        list(3) should be(Player("Yellow", 0, Status.Yellow, PlayerType.Human))
      }
      "return the correct player initials" in {
        val player1 = Player("Player1", 10, Status.Blue, PlayerType.Human)
        val player2 = Player(playerId = "Player2", 1, status = Status.Yellow, PlayerType.Human)
        val player3 = Player(playerId = "Player3", points = 5, status = Status.Green, PlayerType.Computer)

        player1.playerId should be("Player1")
        player1.points should be(10)
        player1.status should be(Status.Blue)
        player1.playerType should be(PlayerType.Human)

        player2.playerId should be("Player2")
        player2.points should be(1)
        player2.status should be(Status.Yellow)
        player2.playerType should be(PlayerType.Human)

        player3.playerId should be("Player3")
        player3.points should be(5)
        player3.status should be(Status.Green)
        player3.playerType should be(PlayerType.Computer)

        list(0).playerId should be("Blue")
        list(0).points should be(0)
        list(0).status should be(Status.Blue)
        list(0).playerType should be(PlayerType.Human)

        list(1).playerId should be("Red")
        list(1).points should be(0)
        list(1).status should be(Status.Red)
        list(1).playerType should be(PlayerType.Human)

        list(2).playerId should be("Green")
        list(2).points should be(0)
        list(2).status should be(Status.Green)
        list(2).playerType should be(PlayerType.Human)

        list(3).playerId should be("Yellow")
        list(3).points should be(0)
        list(3).status should be(Status.Yellow)
        list(3).playerType should be(PlayerType.Human)
      }
    }
    "creating a player list" should {
      "create a list with the correct players" in {
        val list0 = new PlayerList(0, PlayerType.Human)
        val list1 = new PlayerList(1, PlayerType.Human)
        val list2 = new PlayerList(2, PlayerType.Human)
        val list3 = new PlayerList(3, PlayerType.Human)
        val list4 = new PlayerList(4, PlayerType.Human)
        val listDefault = new PlayerList(playerType = PlayerType.Human)

        list0.playerList shouldBe empty
        list1.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human)))
        list2.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))
        list3.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human)))
        list4.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human), Player("Yellow", 0, Status.Yellow, PlayerType.Human)))
        list4.playerList should be(list)
        listDefault.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))
      }
      "create a proper human player list" in {
        val playerList = new PlayerList(playerType = PlayerType.Human)
        playerList should be(PlayerList(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human))))
        playerList.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human)))

        playerList.playerList.size shouldBe 2
        playerList.playerList(0).playerType shouldBe PlayerType.Human
        playerList.playerList(1).playerType shouldBe PlayerType.Human

        val playerList2 = new PlayerList(3, PlayerType.Human)
        playerList2 should be(PlayerList(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human))))
        playerList2.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Human), Player("Green", 0, Status.Green, PlayerType.Human)))
      }
      "create a proper computer player list" in {
        val playerList = new PlayerList(4, PlayerType.Computer)
        playerList should be(PlayerList(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Computer), Player("Green", 0, Status.Green, PlayerType.Computer), Player("Yellow", 0, Status.Yellow, PlayerType.Computer))))
        playerList.playerList should be(Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Computer), Player("Green", 0, Status.Green, PlayerType.Computer), Player("Yellow", 0, Status.Yellow, PlayerType.Computer)))
      
        playerList.playerList.size shouldBe 4
        playerList.playerList(0).playerType shouldBe PlayerType.Human
        playerList.playerList(1).playerType shouldBe PlayerType.Computer
        playerList.playerList(2).playerType shouldBe PlayerType.Computer
        playerList.playerList(3).playerType shouldBe PlayerType.Computer
      }
      "take a playerList" in {
        val list: Vector[Player] = Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Computer), Player("Green", 0, Status.Green, PlayerType.Computer), Player("Yellow", 0, Status.Yellow, PlayerType.Computer))
        val playerList = PlayerList(list)
        playerList._1.size shouldBe 4
        playerList.playerList(0).playerType shouldBe PlayerType.Human
        playerList.playerList(1).playerType shouldBe PlayerType.Computer
        playerList.playerList(2).playerType shouldBe PlayerType.Computer
        playerList.playerList(3).playerType shouldBe PlayerType.Computer
      }
      "create a player list with no players" in {
        val playerList = new PlayerList(0, PlayerType.Computer)
        playerList.playerList.size shouldBe 0
      }
      "create a PlayerList with only one player" in {
        val playerList = new PlayerList(1, PlayerType.Computer)
        playerList.playerList.size shouldBe 1
        playerList.playerList(0).playerType shouldBe PlayerType.Human
      }
    }
  }
}
