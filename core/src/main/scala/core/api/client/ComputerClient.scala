package core.api.client

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import common.config.ServiceConfig.COMPUTER_SERVICE_URL
import org.slf4j.LoggerFactory
import play.api.libs.json.JsObject
import scala.concurrent.{ExecutionContext, Future}

object ComputerClient:
  private[client] implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName.init)
  private implicit val ec: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass.getName.init)
  private val http = Http(system)

  CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseServiceStop, "shutdown-computer-client") { () =>
    shutdown
  }

  def getRequest(endpoint: String): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = COMPUTER_SERVICE_URL.concat(endpoint)
      )
    )

  def postRequest(endpoint: String, json: JsObject): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = COMPUTER_SERVICE_URL.concat(endpoint),
        entity = HttpEntity(ContentTypes.`application/json`, json.toString())
      )
    )

  private def sendRequest(request: HttpRequest): Future[String] =
    http.singleRequest(request).flatMap { response =>
      response.status match
        case StatusCodes.OK =>
          Unmarshal(response.entity).to[String]
        case _ =>
          Unmarshal(response.entity).to[String].flatMap { body =>
            val errorMsg = s"HTTP ERROR: ${response.status} - ${request.uri} - $body"
            logger.error(errorMsg)
            Future.failed(new RuntimeException(errorMsg))
          }
    }

  private def shutdown: Future[Done] =
    logger.info("Core Service -- Shutting Down Computer Client...")
    http.shutdownAllConnectionPools().map(_ => Done)
