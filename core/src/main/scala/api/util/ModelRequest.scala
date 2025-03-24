package api.util

import java.net.{HttpURLConnection, URL}
import java.nio.charset.StandardCharsets
import play.api.libs.json.{JsObject, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Using}

object ModelRequest:
  private val MODEL_HOST = "localhost"
  private val MODEL_PORT = "8080"
  private val MODEL_BASE_URL = s"http://$MODEL_HOST:$MODEL_PORT/"

  def postRequest(endpoint: String, json: JsObject): Future[String] =
    Future {
      Using {
        val url = new URL(MODEL_BASE_URL.concat(endpoint))
        val connection = url.openConnection().asInstanceOf[HttpURLConnection]

        connection.setRequestMethod("POST")
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setDoOutput(true)

        val outputStream = connection.getOutputStream
        outputStream.write(json.toString.getBytes(StandardCharsets.UTF_8))
        outputStream.flush()
        Source.fromInputStream(connection.getInputStream)
      }(_.mkString) match {
        case Success(content)   => content
        case Failure(exception) => println(s"HTTP POST ERROR AT: $endpoint -- ${exception.getMessage}"); ""
      }
    }
