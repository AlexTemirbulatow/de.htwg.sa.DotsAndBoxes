package tuiComponent

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCode}
import de.github.dotsandboxes.lib.{BoardSize, ComputerDifficulty, GameConfig, Move, PlayerSize, PlayerType, Event}
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.io.StdIn.readLine
import scala.util.{Failure, Success, Try}
import spray.json.{JsBoolean, JsNumber, JsObject, JsString}

class TUI:
  private val CORE_HOST = "localhost"
  private val CORE_PORT = "8082"
  private val CORE_BASE_URL = s"http://$CORE_HOST:$CORE_PORT/"

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  def run: Unit =
    println(welcome)
    println(help)
    update(Event.Move)
    gameLoop

  def update(event: Event): Unit = event match
    case Event.Abort => sys.exit
    case Event.End   => print(finalStatsHttp)
    case Event.Move  => print(controllerToStringHttp)

  def gameLoop: Unit =
    analyzeInput(readLine) match
      case Some(move) => controllerPublishHttp(move)
      case None       =>
    gameLoop

  def analyzeInput(input: String): Option[Move] = input match
    case "q" => update(Event.Abort); None
    case "z" => controllerPublishHttp("undo"); None
    case "y" => controllerPublishHttp("redo"); None
    case "s" => controllerPublishHttp("save"); None
    case "l" => controllerPublishHttp("load"); None
    case "r" => controllerRestartHttp; None
    case "h" => println(help); None
    case newGame if newGame.startsWith("NEW: ") =>
      val numbers = newGame.split(": ")(1).split(" ")
      val (boardSizeNum, playerSizeNum, playerTypeNum, computerDifficultyNum): (String, String, String, String) =
        (numbers(0), numbers(1), numbers(2), numbers(3))
      controllerInitGameHttp(
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

  def getRequest(endpoint: String): Future[String] =
    val request = HttpRequest(
      method = HttpMethods.GET,
      uri = CORE_BASE_URL.concat(endpoint)
    )
    val responseFuture: Future[HttpResponse] = Http().singleRequest(request)
    responseFuture.flatMap { response =>
      response.entity.toStrict(5.seconds).map(_.data.utf8String)
    }

  def postRequest(endpoint: String, json: JsObject): Future[StatusCode] =
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = CORE_BASE_URL.concat(endpoint),
      entity = HttpEntity(ContentTypes.`application/json`, json.compactPrint)
    )
    Http().singleRequest(request).map(_.status)

  def controllerToStringHttp: String = Await.result(getRequest("api/core/"), 5.seconds)

  def controllerPublishHttp(move: Move): Future[StatusCode] =
    val jsonBody = JsObject(
      "method" -> JsString("put"),
      "vec" -> JsNumber(move.vec),
      "x" -> JsNumber(move.x),
      "y" -> JsNumber(move.y),
      "value" -> JsBoolean(move.value)
    )
    postRequest("api/core/publish", jsonBody)

  def controllerPublishHttp(method: String): Future[StatusCode] =
    val jsonBody = JsObject(
      "method" -> JsString(method)
    )
    postRequest("api/core/publish", jsonBody)

  def controllerRestartHttp: Future[String] = getRequest("api/core/restart")

  def controllerInitGameHttp(
      boardSize: BoardSize,
      playerSize: PlayerSize,
      playerType: PlayerType,
      computerDifficulty: ComputerDifficulty
  ): Future[StatusCode] =
    val jsonBody = JsObject(
      "boardSize" -> JsString(boardSize.toString),
      "playerSize" -> JsString(playerSize.toString),
      "playerType" -> JsString(playerType.toString),
      "computerDifficulty" -> JsString(computerDifficulty.toString)
    )
    postRequest("api/core/publish", jsonBody)

  def finalStatsHttp: String =
    val winner = Await.result(getRequest("api/core/get/winner"), 5.seconds)
    val stats  = Await.result(getRequest("api/core/get/stats"), 5.seconds)
    "\n" +
      winner + "\n" +
      "_________________________" + "\n\n" +
      stats +
      "\n"

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

  def syntaxErr: String =
    "\nIncorrect syntax. Try again: "
