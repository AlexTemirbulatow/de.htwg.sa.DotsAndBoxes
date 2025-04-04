package computer.computerComponent.computerHardImpl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib.{BoardSize, Move, PlayerSize, PlayerType, Status}
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext, Future}

class ComputerHardSpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ComputerHardTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    testModelServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "ComputerHard" when {
    val computerHard = new ComputerHard()
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "there is at least one winning move but still saved moves left" should {
      "always return the winning move" in {
        val winningField = field
          .putRow(0, 0, true).putRow(1, 0, true).putCol(0, 0, true)

        val winningMove = computerHard.calculateMove(fieldToJsonString(winningField))
        winningMove shouldBe Some(Move(2, 0, 1, true))
      }
    }
    "there is a save move but no winning move left" should {
      "return a save move" in {
        val saveField = field
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 3, true)
          .putCol(1, 0, true).putCol(1, 1, true)
          .putCol(2, 0, true).putCol(2, 1, true)
        val saveMove = computerHard.calculateMove(fieldToJsonString(saveField))

        saveMove should (be(Some(Move(1, 3, 3, true))) or be(Some(Move(2, 2, 4, true))))
      }
    }
    "there is at least one winning move but no more save moves left" should {
      "always return the one pointer winning move" in {
        val onePointerField = field
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true)
          .putCol(2, 0, true).putCol(2, 1, true)
        val onePointerMove = computerHard.calculateMove(fieldToJsonString(onePointerField))

        onePointerMove shouldBe Some(Move(1, 0, 3, true))
      }
      "always return all last wining chain moves if this is the last remaining chain" in {
        val lastChainField1 = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 1, true).putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 3, true).putCol(2, 4, true)
        
        val nextMove1 = computerHard.calculateMove(fieldToJsonString(lastChainField1))
        nextMove1 shouldBe Some(Move(1, 1, 0, true))

        val lastChainField2 = lastChainField1.putRow(1, 0, true)
        val nextMove2 = computerHard.calculateMove(fieldToJsonString(lastChainField2))
        nextMove2 shouldBe Some(Move(1, 2, 0, true))

        val lastChainField3 = lastChainField2.putRow(2, 0, true)
        val nextMove3 = computerHard.calculateMove(fieldToJsonString(lastChainField3))
        nextMove3 shouldBe Some(Move(1, 3, 0, true))

        val noMovesLeftField = lastChainField3.putRow(3, 0, true)
        val noMove = computerHard.calculateMove(fieldToJsonString(noMovesLeftField))
        noMove shouldBe None
      }
      "in a 4 point circle return the winning move if an equal size or smaller chain/circle exist" in {
        val circleWithSmallerChainsField1 = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 1, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 3, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 3, true)

        val nextMove1 = computerHard.calculateMove(fieldToJsonString(circleWithSmallerChainsField1))
        nextMove1 shouldBe Some(Move(2, 1, 2, true))

        val circleWithSmallerChainsField2 = circleWithSmallerChainsField1.putCol(1, 2, true)
        val nextMove2 = computerHard.calculateMove(fieldToJsonString(circleWithSmallerChainsField2))
        nextMove2 shouldBe Some(Move(1, 2, 2, true))

        val circleWithSmallerChainsField3 = circleWithSmallerChainsField2.putRow(2, 2, true)
        val nextMove3 = computerHard.calculateMove(fieldToJsonString(circleWithSmallerChainsField3))
        nextMove3 shouldBe Some(Move(2, 2, 2, true))
      }
      "in a 4 point circle return the non winning move if only bigger chains/circles exist" in {
        val circleWithOnlyBiggerSizeChainsField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 1, true).putRow(2, 3, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 3, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 3, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(circleWithOnlyBiggerSizeChainsField))
        nextMove shouldBe Some(Move(1, 2, 2, true))
      }
      "in a bigger circle return the next winning move" in {
        val biggerCircleField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 1, true).putRow(2, 2, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(biggerCircleField))
        nextMove shouldBe Some(Move(2, 1, 2, true))
      }
      "if there a more than 1 non circular winning moves return the first next winning move" in {
        val multipleNonCircularWinningMoves = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 1, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true)
          .putCol(1, 2, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(multipleNonCircularWinningMoves))
        nextMove shouldBe Some(Move(2, 0, 1, true))
      }
      "in a 2 points chain with other remaining 2 points chains return the winning move" in {
        val multipleTwoPointsChainsField = field
          .putRow(0, 0, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 2, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 2, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 2, true).putCol(2, 4, true)
        
        val nextMove = computerHard.calculateMove(fieldToJsonString(multipleTwoPointsChainsField))
        nextMove shouldBe Some(Move(2, 0, 1, true))
      }
      "in a 2 points chain with only bigger remaining chains return the non winning move" in {
        val multipleTwoPointsChainsField = field
          .putRow(0, 0, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 2, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 2, true).putCol(2, 4, true)
        
        val nextMove = computerHard.calculateMove(fieldToJsonString(multipleTwoPointsChainsField))
        nextMove shouldBe Some(Move(1, 0, 1, true))
      }
      "return the next winning move" in {
        val winningField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 2, true)
          .putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 4, true)
        
        val nextMove = computerHard.calculateMove(fieldToJsonString(winningField))
        nextMove shouldBe Some(Move(2, 0, 3, true))
      }
    }
    "there is no winning moves and no save moves left" should {
      "always return a one pointer risky move" in {
        val onePointRiskyField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 2, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(onePointRiskyField))
        nextMove shouldBe Some(Move(1, 0, 3, true))
      }
      "always take the middle line of a two points chains" in {
        val twoPointsChainMiddleField = field
          .putRow(0, 0, true).putRow(0, 1, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 2, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(twoPointsChainMiddleField))
        nextMove shouldBe Some(Move(2, 0, 3, true))
      }
      "always take the risky move with the least points" in {
        val leastRiskyPointsField = field
          .putRow(0, 0, true).putRow(0, 2, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 2, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 4, true)

        val nextMove = computerHard.calculateMove(fieldToJsonString(leastRiskyPointsField))
        nextMove shouldBe Some(Move(1, 0, 1, true))
      }
    }
    "there is no more available moves left" should {
      "return None" in {
        val finishedField = field
          .putRow(0, 0, true).putRow(0, 1, true).putRow(0, 2, true).putRow(0, 3, true)
          .putRow(1, 0, true).putRow(1, 1, true).putRow(1, 2, true).putRow(1, 3, true)
          .putRow(2, 0, true).putRow(2, 1, true).putRow(2, 2, true).putRow(2, 3, true)
          .putRow(3, 0, true).putRow(3, 1, true).putRow(3, 2, true).putRow(3, 3, true)
          .putCol(0, 0, true).putCol(0, 1, true).putCol(0, 2, true).putCol(0, 3, true).putCol(0, 4, true)
          .putCol(1, 0, true).putCol(1, 1, true).putCol(1, 2, true).putCol(1, 3, true).putCol(1, 4, true)
          .putCol(2, 0, true).putCol(2, 1, true).putCol(2, 2, true).putCol(2, 3, true).putCol(2, 4, true)

        computerHard.calculateMove(fieldToJsonString(finishedField)) shouldBe None
      }
    }
  }
}
