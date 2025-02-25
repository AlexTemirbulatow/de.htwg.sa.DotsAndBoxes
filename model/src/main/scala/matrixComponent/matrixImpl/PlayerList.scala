package model
package matrixComponent.matrixImpl

import de.htwg.se.dotsandboxes.util.PlayerType

case class Player(playerId: String, points: Int, status: Status, playerType: PlayerType)

case class PlayerList(playerList: Vector[Player]):
  def this(playerSize: Int = 2, playerType: PlayerType) =
    this(Vector.tabulate(playerSize)(i =>
      list(i).copy(playerType = if i == 0 then PlayerType.Human else playerType)
    ))

val list = List(
  Player("Blue", 0, Status.Blue, PlayerType.Human),
  Player("Red", 0, Status.Red, PlayerType.Human),
  Player("Green", 0, Status.Green, PlayerType.Human),
  Player("Yellow", 0, Status.Yellow, PlayerType.Human)
)
