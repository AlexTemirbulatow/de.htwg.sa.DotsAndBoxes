package computer.computerComponent.computerEasyImpl

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

class ComputerEasySpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("ComputerEasyTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private def fieldToJsonString(field: FieldInterface): String =
    FieldConverter.toJson(field).toString

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    val unbindFutures = List(
      testModelServerBinding.map(_.unbind()).getOrElse(Future.successful(()))
    )
    Await.result(Future.sequence(unbindFutures), 10.seconds)
    Await.result(system.terminate(), 10.seconds)

  "ComputerEasy" when {
    val computerEasy = new ComputerEasy()
    val field = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)
    "there is a winning move" should {
      "always return the winning move" in {
        val winningField = field.putRow(0, 0, true).putCol(0, 0, true).putCol(0, 1, true)
        val winningMove = computerEasy.calculateMove(fieldToJsonString(winningField))

        winningMove should be(Some(Move(1, 1, 0, true)))
      }
    }
    "there is no winning move" should {
      "return a random move available move" in {
        val randomMove = computerEasy.calculateMove(fieldToJsonString(field))
        val allCoords = field.getUnoccupiedRowCoords ++ field.getUnoccupiedColCoords
        val allMoves = allCoords.map(coord => Some(Move(coord._1, coord._2, coord._3, true)))

        randomMove should be(defined)
        allMoves should contain(randomMove)
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

        computerEasy.calculateMove(fieldToJsonString(finishedField)) shouldBe None
      }
    }
  }
}
