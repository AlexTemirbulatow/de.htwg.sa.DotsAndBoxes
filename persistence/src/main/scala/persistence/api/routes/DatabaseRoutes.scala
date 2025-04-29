package persistence.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import org.slf4j.LoggerFactory
import persistence.api.module.PersistenceModule.given_DAOInterface as daoInterface
import persistence.databaseComponent.GameTableData
import play.api.libs.json.{JsValue, Json}
import scala.util.{Failure, Success}

class DatabaseRoutes:
  private val logger = LoggerFactory.getLogger(getClass.getName.init)

  def databaseRoutes: Route = handleExceptions(exceptionHandler) {
    concat(
      handlePreConnectRequest,
      handleSaveRequest,
      handleLoadRequest
    )
  }

  private def handlePreConnectRequest: Route = get {
    path("preConnect") {
      complete(StatusCodes.OK)
    }
  }

  private def handleSaveRequest: Route = post {
    path("save") {
      entity(as[String]) { json =>
        val jsonValue: JsValue = Json.parse(json)
        val fieldValue: JsValue = Json.parse((jsonValue \ "field").as[String])

        val boardSize = (fieldValue \ "field" \ "boardSize").as[String]
        val playerSize = (fieldValue \ "field" \ "playerSize").as[String]
        val playerType = (fieldValue \ "field" \ "playerType").as[String]
        val currPlayerIndex = (fieldValue \ "field" \ "currPlayerIndex").as[Int]

        val playerData = (fieldValue \ "field" \ "playerList").as[Seq[JsValue]].map { playerJson =>
          val index = (playerJson \ "index").as[Int]
          val points = (playerJson \ "points").as[Int]
          (index, points)
        }.toVector

        val status = (fieldValue \ "field" \ "status").get
        val rows = (fieldValue \ "field" \ "rows").get
        val cols = (fieldValue \ "field" \ "cols").get

        val state = Json.obj(
          "status" -> status,
          "rows" -> rows,
          "cols" -> cols
        )

        daoInterface.update(GameTableData(Json.stringify(state), boardSize, playerSize, playerType, currPlayerIndex, playerData)) match
          case Success(gameID) =>
            logger.info(s"Persistence Service [Database] -- Field successfully saved to database [ID: $gameID]")
            complete(StatusCodes.OK)
          case Failure(exception) =>
            logger.error(s"Persistence Service [Database] -- Failed to save Field to database: ${exception.getMessage}")
            complete(StatusCodes.InternalServerError)
      }
    }
  }

  private def handleLoadRequest: Route = get {
    path("load") {
      val gameTableData: GameTableData = daoInterface.read match
        case Success(data) =>
          logger.info(s"Persistence Service [Database] -- Field successfully loaded from database")
          data
        case Failure(exception) =>
          logger.error(s"Persistence Service [Database] -- Failed to load field from database: ${exception.getMessage}")
          throw new RuntimeException
      
      val stateJson = Json.parse(gameTableData.state)
      val status = (stateJson \ "status").get
      val rows = (stateJson \ "rows").get
      val cols = (stateJson \ "cols").get
      val fieldValue = Json.obj(
        "field" -> Json.obj(
          "boardSize" -> gameTableData.boardSize,
          "playerSize" -> gameTableData.playerSize,
          "playerType" -> gameTableData.playerType,
          "currPlayerIndex" -> gameTableData.currPlayerIndex,
          "status" -> status,
          "rows" -> rows,
          "cols" -> cols,
          "playerList" -> gameTableData.playerData.map(player =>
            Json.obj("index" -> player._1, "points" -> player._2)
          )
        )
      )
      complete(Json.stringify(fieldValue))
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
