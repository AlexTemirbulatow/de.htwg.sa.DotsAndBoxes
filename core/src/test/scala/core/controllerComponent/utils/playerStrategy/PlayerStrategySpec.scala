package core.controllerComponent.utils.playerStrategy

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Route
import common.config.ServiceConfig.{MODEL_HOST, MODEL_PORT}
import common.model.fieldService.FieldInterface
import de.github.dotsandboxes.lib.{BoardSize, PlayerSize, PlayerType, Status}
import model.api.routes.FieldRoutes
import model.fieldComponent.fieldImpl.Field
import model.fieldComponent.parser.FieldParser
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, ExecutionContext}

class PlayerStrategySpec extends AnyWordSpec with BeforeAndAfterAll {
  private implicit val system: ActorSystem = ActorSystem("MoveStrategyTest")
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private var testModelServerBinding: Option[ServerBinding] = None
  private val modelRoutes: Route = pathPrefix("api") { pathPrefix("model") { pathPrefix("field") { new FieldRoutes().fieldRoutes } } }

  private def fieldFromJsonString(fieldValue: String): FieldInterface =
    FieldParser.fromJson(fieldValue)

  override def beforeAll(): Unit =
    testModelServerBinding = Some(Await.result(Http().bindAndHandle(modelRoutes, MODEL_HOST, MODEL_PORT), 10.seconds))

  override def afterAll(): Unit =
    testModelServerBinding.foreach(binding =>
      Await.result(binding.unbind(), 10.seconds)
    )
    Await.result(system.terminate(), 10.seconds)

  "PlayerStrategy" should {
    "add one point based on one status difference and keep current player" in {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Blue),
        Vector(Status.Empty, Status.Empty)
      )

      val updatedField: String = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPlayerPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      fieldFromJsonString(updatedField).getPlayerPoints(0) should be(1)
      fieldFromJsonString(updatedField).currentPlayerIndex should be(0)
    }
    "add two points based on two status differences and keep current player" in {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Blue),
        Vector(Status.Blue, Status.Empty)
      )

      val updatedField: String = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPlayerPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      fieldFromJsonString(updatedField).getPlayerPoints(0) should be(2)
      fieldFromJsonString(updatedField).currentPlayerIndex should be(0)
    }
    "add no points based on no status differences and change current player to next" in {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )

      val updatedField: String = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPlayerPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      fieldFromJsonString(updatedField).getPlayerPoints(0) should be(0)
      fieldFromJsonString(updatedField).currentPlayerIndex should be(1)
    }
    "add no points based on too many status differences and change current player to next" in {
      val field: FieldInterface = new Field(BoardSize.Small, Status.Empty, PlayerSize.Two, PlayerType.Human)

      val preStatus = Vector(
        Vector(Status.Empty, Status.Empty),
        Vector(Status.Empty, Status.Empty)
      )
      val postStatus = Vector(
        Vector(Status.Blue, Status.Blue),
        Vector(Status.Blue, Status.Blue)
      )

      val updatedField: String = PlayerStrategy.updatePlayer(field, preStatus, postStatus)

      field.getPlayerPoints(0) should be(0)
      field.currentPlayerIndex should be(0)

      fieldFromJsonString(updatedField).getPlayerPoints(0) should be(0)
      fieldFromJsonString(updatedField).currentPlayerIndex should be(1)
    }
  }
}
