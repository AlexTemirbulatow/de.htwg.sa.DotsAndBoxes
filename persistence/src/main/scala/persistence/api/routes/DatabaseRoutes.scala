package persistence.api.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import org.slf4j.LoggerFactory
import persistence.api.module.PersistenceModule.given_DAOInterface
import play.api.libs.json.{JsValue, Json}

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
        given_DAOInterface.update
        complete(StatusCodes.OK)
      }
    }
  }

  private def handleLoadRequest: Route = get {
    path("get") {
      complete(StatusCodes.OK, given_DAOInterface.read.toString)
    }
  }

  private val exceptionHandler = ExceptionHandler {
    case e: IllegalArgumentException =>
      complete(Conflict -> e.getMessage)
    case e: Throwable =>
      complete(InternalServerError -> e.getMessage)
  }
