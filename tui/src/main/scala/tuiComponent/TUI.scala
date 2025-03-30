package tuiComponent

import api.service.CoreRequestHttp
import de.github.dotsandboxes.lib._
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}

class TUI:
  def run: Unit =
    println(welcome)
    println(help)
    update(Event.Move)
    gameLoop

  def update(event: Event): Unit = event match
    case Event.Abort => sys.exit
    case Event.End   => print(finalStats)
    case Event.Move  => print(fieldString)

  def gameLoop: Unit =
    analyzeInput(readLine) match
      case Some(move) => CoreRequestHttp.publish(move)
      case None       =>
    gameLoop

  def analyzeInput(input: String): Option[Move] = input match
    case "q" => update(Event.Abort); None
    case "z" => CoreRequestHttp.publish("undo"); None
    case "y" => CoreRequestHttp.publish("redo"); None
    case "s" => CoreRequestHttp.publish("save"); None
    case "l" => CoreRequestHttp.publish("load"); None
    case "r" => CoreRequestHttp.restart; None
    case "h" => println(help); None
    case newGame if newGame.startsWith("NEW: ") =>
      val numbers = newGame.split(": ")(1).split(" ")
      val (boardSizeNum, playerSizeNum, playerTypeNum, computerDifficultyNum): (String, String, String, String) =
        (numbers(0), numbers(1), numbers(2), numbers(3))
      CoreRequestHttp.initGame(
        GameConfig.boardSizes(boardSizeNum),
        GameConfig.playerSizes(playerSizeNum),
        GameConfig.playerType(playerTypeNum),
        GameConfig.computerDifficulty(computerDifficultyNum)
      )
      None
    case _ if input.length == 3 =>
      checkSyntax(input(0), input(1), input(2)) match
        case Success(move) => Some(Move(move(0), move(1), move(2), true))
        case Failure(_)    => print(syntaxErr); None
    case _ =>
      print(syntaxErr); None

  def checkSyntax(vec: Char, x: Char, y: Char): Try[(Int, Int, Int)] =
    Try(vec.toString.toInt, x.toString.toInt, y.toString.toInt)

  def welcome: String =
    "\n" +
      "---------------------------------\n" +
      "| Welcome to Dots And Boxes TUI |\n" +
      "---------------------------------\n"

  def help: String =
    "--Note\n\n" +
      "A move consists of:\n\n" +
      "<Line> index: (1) for horizontally, (2) for vertically\n" +
      "<X> coordinate: starting at (0)\n" +
      "<Y> coordinate: starting at (0)\n" +
      "e.g., 132\n\n" +
      "You can type (q) to quit, (z) to undo (y) to redo, (r) to restart,\n" +
      "(h) for help, (s) to save the current game state or (l) to load it.\n\n" +
      "If you want to start a new game with different settings you can type 'NEW: '\n" +
      "followed by this space separated options:\n\n" +
      "<Board size>:          (1) for 4x3, (2) for 5x4, (3) for 8x6\n" +
      "<Player size>:         (2), (3), (4)\n" +
      "<Player type>:         (1) for humans, (2) for computers\n" +
      "<Computer difficulty>: (1) for easy, (2) for medium, (3) for hard\n" +
      "e.g., NEW: 2 3 2 1\n"

  def fieldString: String = CoreRequestHttp.toString

  def finalStats: String =
    val playerGameData: PlayerGameData = CoreRequestHttp.playerGameData
    "\n" +
      playerGameData.winner + "\n" +
      "_________________________" + "\n\n" +
      playerGameData.stats +
      "\n"

  def syntaxErr: String =
    "\nIncorrect syntax. Try again: "
