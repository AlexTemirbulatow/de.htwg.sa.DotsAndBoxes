package tui
package tuiComponent

import Default.given
import controller.controllerComponent.ControllerInterface
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}
import util.Event
import util.{Move, PackT}
import de.htwg.se.dotsandboxes.util.GameConfig
import de.htwg.se.dotsandboxes.util.GameConfig.computerImpl

class TUI(using controller: ControllerInterface) extends Template(controller):
  override def update(event: Event): Unit = event match
    case Event.Abort => sys.exit
    case Event.End   => print(finalStats)
    case Event.Move  => print(controller.toString)

  override def gameLoop: Unit =
    analyzeInput(readLine) match
      case Some(move) => controller.publish(controller.put, move)
      case None       =>
    gameLoop

  override def analyzeInput(input: String): Option[Move] = input match
    case "q" => update(Event.Abort); None
    case "z" => controller.publish(controller.undo); None
    case "y" => controller.publish(controller.redo); None
    case "s" => controller.save; None
    case "l" => controller.load; None
    case "r" => controller.restart; None
    case "h" => println(help); None
    case newGame if newGame.startsWith("NEW: ") =>
      val numbers = newGame.split(": ")(1).split(" ")
      val (boardSizeNum, playerSizeNum, playerTypeNum, computerDifficultyNum): (String, String, String, String) =
        (numbers(0), numbers(1), numbers(2), numbers(3))
      controller.initGame(
        GameConfig.boardSizes(boardSizeNum),
        GameConfig.playerSizes(playerSizeNum),
        GameConfig.playerType(playerTypeNum),
        GameConfig.computerImpl(GameConfig.computerDifficulty(computerDifficultyNum))
      )
      None
    case cheat if cheat.startsWith("CHEAT: ") =>
      val moves: List[String] = cheat.stripPrefix("CHEAT: ").split("\\s+").toList
      val pack: List[Option[Move]] = moves.map(move => {
        checkSyntax(move(0), move(1), move(2)) match
          case Success(move) => Some(Move(move(0), move(1), move(2), true))
          case Failure(_)    => None
      })
      controller.publishCheat(controller.put, PackT(pack))
      None
    case _ if input.length == 3 =>
      checkSyntax(input(0), input(1), input(2)) match
        case Success(move) => Some(Move(move(0), move(1), move(2), true))
        case Failure(_)    => print(syntaxErr); None
    case _ =>
      print(syntaxErr); None

  override def checkSyntax(vec: Char, x: Char, y: Char): Try[(Int, Int, Int)] =
    Try(vec.toString.toInt, x.toString.toInt, y.toString.toInt)

  override def finalStats: String =
    "\n" +
    controller.winner + "\n" +
    "_________________________" + "\n\n" +
    controller.stats +
    "\n"

  override def syntaxErr: String =
    "\nIncorrect syntax. Try again: "
