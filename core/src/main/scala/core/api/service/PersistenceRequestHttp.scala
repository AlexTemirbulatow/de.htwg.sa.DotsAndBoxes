package core.api.service

import core.api.client.PersistenceClient
import de.github.dotsandboxes.lib.FileFormat
import play.api.libs.json.Json
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object PersistenceRequestHttp:
  def saveFileIO(fieldValue: String, fileFormat: FileFormat, filename: String): Future[String] =
    PersistenceClient.postRequest("api/persistence/fileIO/save", Json.obj(
      "field"      -> fieldValue,
      "fileFormat" -> fileFormat.toString,
      "filename"   -> filename
    ))

  def loadFileIO(fileFormat: FileFormat, filename: String): String =
    Await.result(PersistenceClient.postRequest("api/persistence/fileIO/load", Json.obj(
      "fileFormat" -> fileFormat.toString,
      "filename"   -> filename
    )), 5.seconds)
