package core.controllerComponent.controllerImpl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig._
import common.model.fieldService.FieldInterface
import computer.api.routes.ComputerRoutes
import core.controllerComponent.utils.observer.Observer
import de.github.dotsandboxes.lib._
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.Eventually
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import persistence.api.routes.FileIORoutes
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}
import core.controllerComponent.utils.observer.ObserverHttp

class ControllerIntegrationSpec extends AnyWordSpec with Eventually with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ControllerIntegrationTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  implicit override val patienceConfig: PatienceConfig = PatienceConfig(timeout = 10.seconds, interval = 200.millis)

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes }}}

  private var testPersistenceServerBinding: Option[ServerBinding] = None
  private val fileIORoutes: Route = pathPrefix("api") { pathPrefix("persistence") { pathPrefix("fileIO") { new FileIORoutes().fileIORoutes } } }

  private var testComputerServerBinding: Option[ServerBinding] = None
  private val computerRoutes: Route = pathPrefix("api") { pathPrefix("computer") { new ComputerRoutes().computerRoutes } }

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))
    testPersistenceServerBinding = Some(Await.result(Http().bindAndHandle(fileIORoutes, PERSISTENCE_HOST, PERSISTENCE_PORT), 10.seconds))
    testComputerServerBinding = Some(Await.result(Http().bindAndHandle(computerRoutes, COMPUTER_HOST, COMPUTER_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(())),
      testPersistenceServerBinding.map(_.unbind()).getOrElse(Future.successful(())),
      testComputerServerBinding.map(_.unbind()).getOrElse(Future.successful(()))
    )
    Await.result(Future.sequence(unbindFutures), 10.seconds)
    Await.result(system.terminate(), 10.seconds)

  "The Controller" should {
    "notify its observers on change and update the game" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      class TestObserver(controller: Controller) extends Observer:
        controller.add(this)
        var bing = false
        def update(e: Event) =
          bing = true
          None
        def id = "testID"
      val testObserver = TestObserver(controller)
      testObserver.bing shouldBe false
      controller.publish(controller.put, Move(1, 0, 0, true))
      testObserver.bing shouldBe true
      controller.remove("testID")
    }
    "correctly receive and update a http observer" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      val testObserverUrl = MODEL_BASE_URL.concat("api/model/field/preConnect")
      val observerHttp = new ObserverHttp(testObserverUrl)
      observerHttp.id shouldBe testObserverUrl
      observerHttp.update(Event.Move) shouldBe Some("OK")
    }
    "create a new field" in {
      val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
      val controller = new Controller(using field, FileFormat.JSON, ComputerDifficulty.Hard)

      field.boardSize shouldBe BoardSize.Small
      field.playerSize shouldBe PlayerSize.Two
      field.playerType shouldBe PlayerType.Human

      val newField = controller.newField(BoardSize.Large, Status.Empty, PlayerSize.Three, PlayerType.Computer)
      newField.boardSize shouldBe BoardSize.Large
      newField.playerSize shouldBe PlayerSize.Three
      newField.playerType shouldBe PlayerType.Computer
    }
    "return the correct fieldString representation" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.fieldString should be(
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n"
      )
      controller.toString should be(
        "\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n\n" +
        "Blues turn\n" +
        "[points: 0]\n\n" +
        "Your Move <Line><X><Y>: "
      )

      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.fieldString should be(
        "O=======O-------O-------O-------O\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "O=======O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n"
      )
      controller.toString should be(
        "\n" +
        "O=======O-------O-------O-------O\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "O=======O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n\n" +
        "Reds turn\n" +
        "[points: 1]\n\n" +
        "Your Move <Line><X><Y>: "
      )
    }
    "return the correct currentPlayer" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.currentPlayer shouldBe Player("Red", 0, Status.Red, PlayerType.Human)
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
    }
    "return the correct status" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.currentStatus shouldBe Vector(Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty))
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.currentStatus shouldBe Vector(Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.currentStatus shouldBe Vector(Vector(Status.Red, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty), Vector(Status.Empty, Status.Empty, Status.Empty, Status.Empty))
    }
    "return if a Move is Edge" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.isEdge(Move(1, 0, 0, true)) shouldBe true
      controller.isEdge(Move(1, 0, controller.field.maxPosY, true)) shouldBe true
      controller.isEdge(Move(1, controller.field.maxPosX, 0, true)) shouldBe true
      controller.isEdge(Move(2, 0, 0, true)) shouldBe true
      controller.isEdge(Move(2, 0, controller.field.maxPosY, true)) shouldBe true
      controller.isEdge(Move(2, controller.field.maxPosX, 0, true)) shouldBe true
      controller.isEdge(Move(1, 1, 1, true)) shouldBe false
    }
    "return correct fieldData" in {
      val controller = new Controller(using new Field(BoardSize.Large, Status.Empty, PlayerSize.Three, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Easy)
      controller.fieldData shouldBe FieldData(BoardSize.Large, PlayerSize.Three, PlayerType.Computer, ComputerDifficulty.Easy)
    }
    "return correct gameBoardData" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.gameBoardData shouldBe GameBoardData(
        Player("Blue", 0, Status.Blue, PlayerType.Human),
        Vector(Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false), Vector(false, false, false, false)),
        Vector(Vector(false, false, false, false, false), Vector(false, false, false, false, false), Vector(false, false, false, false, false)),
        Vector(Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"), Vector("-", "-", "-", "-"))
      )
    }
    "return correct playerGameData" in {
      val controller = new Controller(using new Field(BoardSize.Large, Status.Empty, PlayerSize.Three, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Easy)
      controller.playerGameData shouldBe PlayerGameData(
        Player("Blue", 0, Status.Blue, PlayerType.Human),
        "It's a draw!",
        "Player Blue [points: 0]\n" +
        "Player Red [points: 0]\n" +
        "Player Green [points: 0]",
        Vector(Player("Blue", 0, Status.Blue, PlayerType.Human), Player("Red", 0, Status.Red, PlayerType.Computer), Player("Green", 0, Status.Green, PlayerType.Computer))
        )
    }
    "return correct fieldSizeData" in {
      val controller = new Controller(using new Field(BoardSize.Large, Status.Empty, PlayerSize.Three, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Easy)
      controller.fieldSizeData shouldBe FieldSizeData(7, 9)  
    }
    "be able to undo and redo" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      val controller2 = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)

      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))

      controller.field.getRowCell(0, 0) shouldBe true
      controller.field.getColCell(0, 1) shouldBe true
      controller.field.getStatusCell(0, 0) should be(Status.Red)

      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 1) shouldBe true

      controller.publish(controller.undo)
      controller.field.getStatusCell(0, 0) should be(Status.Empty)
      controller.field.getColCell(0, 1) shouldBe false

      controller.field.getColCell(0, 1) shouldBe false

      controller.publish(controller.redo)
      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 1) shouldBe true

      controller.publish(controller.undo)
      controller.publish(controller.undo)
      controller.field.getColCell(0, 0) shouldBe false
      controller.field.getColCell(0, 1) shouldBe false

      controller.publish(controller.redo)
      controller.publish(controller.redo)
      controller.field.getStatusCell(0, 0) should be(Status.Red)
      controller.field.getColCell(0, 0) shouldBe true
      controller.field.getColCell(0, 1) shouldBe true

      controller2.publish(controller2.put, Move(1, 0, 0, true))
      controller2.publish(controller2.put, Move(2, 0, 0, true))

      val undoField = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(0, 0, true).nextPlayer
      val redoField = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human).putRow(0, 0, true).putCol(0, 0, true)

      controller2.undo should be(undoField)
      controller2.redo should be(redoField)
    }
    "be able to undo and redo when the game was already finished" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)

      for {
        x <- 0 until controller.field.maxPosY
        y <- 0 until controller.field.maxPosY
      } controller.publish(controller.put, Move(1, x, y, true))

      for {
        x <- 0 until controller.field.maxPosX
        y <- 0 to controller.field.maxPosY
      } controller.publish(controller.put, Move(2, x, y, true))

      controller.gameEnded shouldBe true
      controller.publish(controller.undo)
      controller.gameEnded shouldBe false
      controller.publish(controller.redo)
      controller.gameEnded shouldBe true
    }
    "handle nil undo and redo stack" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      val field = controller.field
      controller.undo should be(field)
      controller.redo should be(field)
    }
    "save and load the correct game state as JSON" in {
      val controller = Controller(using new Field(BoardSize.Medium, Status.Empty, PlayerSize.Four, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.publish(controller.put, Move(1, 1, 0, true))

      controller.publish(controller.put, Move(1, 0, 1, true))
      controller.publish(controller.put, Move(2, 0, 2, true))
      controller.publish(controller.put, Move(1, 1, 1, true))

      controller.publish(controller.put, Move(1, 0, 2, true))
      controller.publish(controller.put, Move(2, 0, 3, true))
      controller.publish(controller.put, Move(2, 1, 0, true))
      controller.publish(controller.put, Move(1, 1, 2, true))

      controller.publish(controller.put, Move(1, 0, 3, true))
      controller.publish(controller.put, Move(2, 0, 4, true))
      controller.publish(controller.put, Move(1, 1, 3, true))

      controller.save should be(controller.field)
      Thread.sleep(2000)
      controller.load should be(controller.field)
    }
    "save and load the correct game state as XML" in {
      val controller = Controller(using new Field(BoardSize.Medium, Status.Empty, PlayerSize.Four, PlayerType.Human), FileFormat.XML, ComputerDifficulty.Medium)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.publish(controller.put, Move(1, 1, 0, true))

      controller.publish(controller.put, Move(1, 0, 1, true))
      controller.publish(controller.put, Move(2, 0, 2, true))
      controller.publish(controller.put, Move(1, 1, 1, true))

      controller.publish(controller.put, Move(1, 0, 2, true))
      controller.publish(controller.put, Move(2, 0, 3, true))
      controller.publish(controller.put, Move(2, 1, 0, true))
      controller.publish(controller.put, Move(1, 1, 2, true))

      controller.publish(controller.put, Move(1, 0, 3, true))
      controller.publish(controller.put, Move(2, 0, 4, true))
      controller.publish(controller.put, Move(1, 1, 3, true))

      controller.save should be(controller.field)
      Thread.sleep(2000)
      controller.load should be(controller.field)
    }
    "return a finished game state" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      for {
        x <- 0 until controller.field.maxPosY
        y <- 0 until controller.field.maxPosY
      } controller.publish(controller.put, Move(1, x, y, true))

      for {
        x <- 0 until controller.field.maxPosX
        y <- 0 to controller.field.maxPosY
      } controller.publish(controller.put, Move(2, x, y, true))

      controller.gameEnded shouldBe true
      controller.save should be(controller.field)

      controller.load should be(controller.field)
      controller.gameEnded shouldBe true
    }
    "restart a game" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))

      controller.field.getRowCell(0, 0) should be(true)
      controller.field.getRowCell(1, 0) should be(true)
      controller.field.getColCell(0, 0) should be(true)
      controller.field.getColCell(0, 1) should be(true)

      controller.field.getStatusCell(0, 0) shouldBe Status.Red
      controller.currentPlayer shouldBe Player("Red", 1, Status.Red, PlayerType.Human)

      controller.restart

      controller.field.getRowCell(0, 0) should be(false)
      controller.field.getRowCell(1, 0) should be(false)
      controller.field.getColCell(0, 0) should be(false)
      controller.field.getColCell(0, 1) should be(false)

      controller.field.getStatusCell(0, 0) shouldBe Status.Empty
      controller.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
    }
    "init a new game" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.initGame(BoardSize.Medium, PlayerSize.Three, PlayerType.Computer, ComputerDifficulty.Easy)

      controller.field.boardSize shouldBe BoardSize.Medium
      controller.field.playerSize shouldBe PlayerSize.Three
      controller.field.playerType shouldBe PlayerType.Computer
      controller.computerDifficulty shouldBe ComputerDifficulty.Easy
    }
    "init a new game and choose computer medium if more than 2 players and computer hard is chosen" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.initGame(BoardSize.Medium, PlayerSize.Three, PlayerType.Computer, ComputerDifficulty.Hard)

      controller.field.boardSize shouldBe BoardSize.Medium
      controller.field.playerSize shouldBe PlayerSize.Three
      controller.field.playerType shouldBe PlayerType.Computer
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Hard)
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Medium)
      controller.computerDifficulty shouldBe ComputerDifficulty.Medium

      controller.initGame(BoardSize.Medium, PlayerSize.Four, PlayerType.Computer, ComputerDifficulty.Easy)
      controller.computerDifficulty shouldBe ComputerDifficulty.Easy

      controller.initGame(BoardSize.Medium, PlayerSize.Two, PlayerType.Computer, ComputerDifficulty.Hard)
      controller.computerDifficulty shouldBe ComputerDifficulty.Hard
    }
    "play against a computer opponent" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.field.getRowCell(0, 0) should be(true)

      eventually {
        val gameBoardData: GameBoardData = controller.gameBoardData
        val allCellState: Vector[Boolean] = gameBoardData.rowCells.flatten ++ gameBoardData.colCells.flatten

        allCellState.count(identity) shouldBe 2
      }
    }
    "make a computer move" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Medium)
      val initField = controller.field
      val futureField: Future[FieldInterface] = controller.computerMove(controller.field)
      val updatedField = Await.result(futureField, 5.seconds)
      updatedField should not be initField
    }
    "calculate a computer move" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Computer), FileFormat.JSON, ComputerDifficulty.Medium)
      val initField = controller.field
      val futureField: Future[FieldInterface] = controller.computerMove(controller.field)
      val updatedField = Await.result(futureField, 5.seconds)
      updatedField should not be initField

      val gameBoardData: GameBoardData = updatedField.gameBoardData
      val allCellState: Vector[Boolean] = gameBoardData.rowCells.flatten ++ gameBoardData.colCells.flatten
      allCellState.count(identity) shouldBe 1
    }
    "play a whole game correctly" in {
      val controller = Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Three, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.toString should be(
        "\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )

      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.gameEnded shouldBe false

      controller.toString should be(
        "\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 1, 1, true))
      controller.toString should be(
        "\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(2, 0, 1, true))
      controller.toString should be(
        "\n" +
          "O=======O-------O-------O-------O\n" +
          "¦   -   ‖   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O-------O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Blues turn\n" +
          "[points: 0]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.currentPlayer shouldBe Player("Blue", 0, Status.Blue, PlayerType.Human)
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.currentPlayer shouldBe Player("Red", 1, Status.Red, PlayerType.Human)
      controller.toString should be(
        "\n" +
          "O=======O-------O-------O-------O\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
          "O=======O=======O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Reds turn\n" +
          "[points: 1]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(1, 0, 1, true))
      controller.publish(controller.put, Move(1, 0, 2, true))
      controller.publish(controller.put, Move(1, 0, 3, true))
      controller.publish(controller.put, Move(1, 1, 2, true))
      controller.publish(controller.put, Move(1, 1, 3, true))
      controller.publish(controller.put, Move(2, 0, 2, true))
      controller.publish(controller.put, Move(2, 0, 4, true))
      controller.publish(controller.put, Move(2, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 3, true))
      controller.toString should be(
        "\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
          "O=======O=======O=======O=======O\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "‖   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
          "O-------O-------O-------O-------O\n\n" +
          "Greens turn\n" +
          "[points: 2]\n\n" +
          "Your Move <Line><X><Y>: "
      )
      controller.publish(controller.put, Move(2, 1, 1, true))
      controller.publish(controller.put, Move(2, 1, 2, true))
      controller.publish(controller.put, Move(1, 2, 0, true))
      controller.publish(controller.put, Move(2, 1, 3, true))
      controller.publish(controller.put, Move(2, 1, 4, true))
      controller.publish(controller.put, Move(2, 2, 0, true))
      controller.publish(controller.put, Move(2, 2, 1, true))
      controller.publish(controller.put, Move(2, 2, 2, true))
      controller.publish(controller.put, Move(2, 2, 3, true))
      controller.publish(controller.put, Move(2, 2, 4, true))
      controller.publish(controller.put, Move(1, 2, 1, true))
      controller.publish(controller.put, Move(1, 2, 2, true))
      controller.publish(controller.put, Move(1, 2, 3, true))
      controller.publish(controller.put, Move(1, 3, 0, true))
      controller.publish(controller.put, Move(1, 3, 1, true))
      controller.publish(controller.put, Move(1, 3, 2, true))
      controller.publish(controller.put, Move(1, 3, 3, true))
      controller.toString should be(
        "\n" +
        "O=======O=======O=======O=======O\n" +
        "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
        "‖   R   ‖   B   ‖   G   ‖   G   ‖\n" +
        "O=======O=======O=======O=======O\n" +
        "‖   R   ‖   G   ‖   G   ‖   G   ‖\n" +
        "‖   R   ‖   G   ‖   G   ‖   G   ‖\n" +
        "O=======O=======O=======O=======O\n" +
        "‖   G   ‖   G   ‖   G   ‖   G   ‖\n" +
        "‖   G   ‖   G   ‖   G   ‖   G   ‖\n" +
        "O=======O=======O=======O=======O\n\n" +
        "Greens turn\n" +
        "[points: 9]\n\n"
      )

      controller.gameEnded shouldBe true
    }
    "deny wrong input" in {
      val controller = new Controller(using new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human), FileFormat.JSON, ComputerDifficulty.Medium)
      controller.publish(controller.put, Move(1, 0, 0, true))
      controller.publish(controller.put, Move(1, 1, 0, true))
      controller.publish(controller.put, Move(2, 0, 0, true))
      controller.publish(controller.put, Move(2, 0, 1, true))
      /* wrong inputs */
      controller.publish(controller.put, Move(4, 0, 0, true))
      controller.publish(controller.put, Move(1, 9, 0, true))
      controller.publish(controller.put, Move(2, 0, 9, true))
      /* no change */
      controller.toString should be(
        "\n" +
        "O=======O-------O-------O-------O\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "‖   R   ‖   -   ¦   -   ¦   -   ¦\n" +
        "O=======O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "¦   -   ¦   -   ¦   -   ¦   -   ¦\n" +
        "O-------O-------O-------O-------O\n\n" +
        "Reds turn\n" +
        "[points: 1]\n\n" +
        "Your Move <Line><X><Y>: "
      )
    }
  }
}
