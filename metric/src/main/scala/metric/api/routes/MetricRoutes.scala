package metric.api.routes

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.scaladsl.{Flow, Keep, RunnableGraph, Sink, Source}
import de.github.dotsandboxes.lib.{GameStats, PlayerStats}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import metric.api.module.MetricModule.given_DAOInterface as daoInterface
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json, OFormat}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class MetricRoutes:
  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val executionContext: ExecutionContext = system.dispatcher

  private implicit val playerStatsFormat: OFormat[PlayerStats] = Json.format[PlayerStats]
  private implicit val gameStatsFormat: OFormat[GameStats] = Json.format[GameStats]

  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def metricRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleInsertNewMoveRequest,
      handleGetStatsRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handleInsertNewMoveRequest: Route = post {
    path("insertMove") {
      entity(as[String]) { json =>
        val source: Source[String, NotUsed] = Source.single(json)

        val parseFlow = Flow[String].map { jsonString =>
          val jsonValue: JsValue = Json.parse(jsonString)
          val timestamp = (jsonValue \ "timestamp").as[Long]
          val playerName = (jsonValue \ "playerName").as[String]
          (timestamp, playerName)
        }

        val dbInsertFlow = Flow[(Long, String)].map { case (timestamp, playerName) =>
          daoInterface.create(timestamp, playerName) match
            case Success(moveID) =>
              logger.info(s"Metric Service -- Move successfully inserted into database [ID: $moveID]")
              StatusCodes.OK
            case Failure(ex) =>
              logger.error(s"Metric Service -- Failed to insert Move: ${ex.getMessage}")
              StatusCodes.InternalServerError
        }

        val sink: Sink[StatusCode, Future[HttpResponse]] =
          Sink.head.mapMaterializedValue(_.map {
            case StatusCodes.OK                  => HttpResponse(StatusCodes.OK)
            case StatusCodes.InternalServerError => HttpResponse(StatusCodes.InternalServerError)
            case _                               => HttpResponse(StatusCodes.InternalServerError)
          })

        val runnable: RunnableGraph[Future[HttpResponse]] =
          source.via(parseFlow).via(dbInsertFlow).toMat(sink)(Keep.right)

        complete(runnable.run())
      }
    }
  }

  private def handleGetStatsRequest: Route = post {
    path("getStats") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val playerNames = (jsonValue \ "playerNames").as[Seq[String]].toVector

        val statsJson = Json.obj(
          "totalDuration" -> daoInterface.getTotalGameDuration,
          "playerStats" -> Json.obj(
            playerNames.map { name =>
              name -> Json.obj(
                "avgMoveDuration" -> daoInterface.getAvgMoveDuration(name),
                "minMoveDuration" -> daoInterface.getMinMoveDuration(name),
                "maxMoveDuration" -> daoInterface.getMaxMoveDuration(name),
                "longestMoveStreak" -> daoInterface.getLongestMoveStreak(name),
                "numOfTotalMoves" -> daoInterface.getNumOfTotalMoves(name)
              )
            }*
          )
        )
        val gameStats: GameStats = statsJson.as[GameStats]
        complete(gameStats.asJson.toString)
      }
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
