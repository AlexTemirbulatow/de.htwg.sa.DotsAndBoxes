package metric.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.github.dotsandboxes.lib.{GameStats, PlayerStats}
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._
import metric.api.module.MetricModule.given_DAOInterface as daoInterface
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsValue, Json, OFormat}
import scala.util.{Failure, Success}

class MetricRoutes:
  implicit val playerStatsFormat: OFormat[PlayerStats] = Json.format[PlayerStats]
  implicit val gameStatsFormat: OFormat[GameStats] = Json.format[GameStats]

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
        val jsonValue: JsValue = Json.parse(json)
        val timestamp: Long = (jsonValue \ "timestamp").as[Long]
        val playerName: String = (jsonValue \ "playerName").as[String]

        daoInterface.create(timestamp, playerName) match
          case Success(moveID) =>
            logger.info(s"Metric Service -- Move successfully inserted into database [ID: $moveID]")
            complete(StatusCodes.OK)
          case Failure(exception) =>
            logger.error(s"Metric Service -- Failed to insert Move into database: ${exception.getMessage}")
            complete(StatusCodes.InternalServerError)
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
