package api.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.slf4j.LoggerFactory
import play.api.libs.json.JsObject
import scala.concurrent.{ExecutionContext, Future}

object ModelClient:
  private val MODEL_HOST = "localhost"
  private val MODEL_PORT = "8080"
  private val MODEL_BASE_URL = s"http://$MODEL_HOST:$MODEL_PORT/"

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val ec: ExecutionContext = system.dispatcher

  private val logger = LoggerFactory.getLogger(getClass)
  private val http = Http(system)

  def getRequest(endpoint: String): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.GET,
        uri = MODEL_BASE_URL.concat(endpoint)
      )
    )

  def postRequest(endpoint: String, json: JsObject): Future[String] =
    sendRequest(
      HttpRequest(
        method = HttpMethods.POST,
        uri = MODEL_BASE_URL.concat(endpoint),
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

  def shutdown: Future[Unit] =
    logger.info("Shutting down Core ModelClient...")
    http.shutdownAllConnectionPools().flatMap(_ => system.terminate()).map(_ => ())
