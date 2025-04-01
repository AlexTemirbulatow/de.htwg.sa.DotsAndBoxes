package api.service

import api.client.PersistenceClient
import de.github.dotsandboxes.lib.FileFormat
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object PersistenceRequestHttp:
  def saveFileIO(fieldValue: String, fileFormat: FileFormat): Future[String] =
    PersistenceClient.postRequest("api/persistence/fileIO/save", Json.obj(
      "field"      -> fieldValue,
      "fileFormat" -> fileFormat.toString
    ))

  def loadFileIO(fileFormat: FileFormat): String =
    Await.result(PersistenceClient.postRequest("api/persistence/fileIO/load", Json.obj(
      "fileFormat" -> fileFormat.toString
    )), 5.seconds)
