package persistence.fileIOComponent.jsonImpl

import java.io.{File, PrintWriter}
import persistence.fileIOComponent.FileIOInterface
import play.api.libs.json.Json
import scala.io.Source
import scala.util.{Failure, Success, Try}

class FileIO extends FileIOInterface:
  private val FILE_PATH = ""
  private val FILE_EXTENSION = ".json"

  override def save(fieldValue: String, filename: String): Either[(String, String), String] =
    val fullPath = FILE_PATH + filename + FILE_EXTENSION
    Try {
      val printWriter = new PrintWriter(new File(fullPath))
      printWriter.write(Json.prettyPrint(Json.parse(fieldValue)))
      printWriter.close()
    } match
      case Success(_) => Right(fullPath)
      case Failure(e) => Left((e.getMessage, fullPath))

  override def load(filename: String): Either[(String, String), (String, String)] =
    val fullPath = FILE_PATH + filename + FILE_EXTENSION
    Try(Source.fromFile(fullPath).getLines.mkString) match
      case Success(fieldValue) => Right((fieldValue, fullPath))
      case Failure(e)          => Left((e.getMessage, fullPath))
