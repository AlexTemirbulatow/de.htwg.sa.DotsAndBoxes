package fileIoComponent.jsonImpl

import fileIoComponent.FileIOInterface
import java.io.{File, PrintWriter}
import play.api.libs.json.Json
import scala.io.Source
import scala.util.{Failure, Success, Try}

class FileIO extends FileIOInterface:
  private val FILE_NAME = "field.json"

  override def save(fieldValue: String): Either[(String, String), String] =
    Try {
      val printWriter = new PrintWriter(new File(FILE_NAME))
      printWriter.write(Json.prettyPrint(Json.parse(fieldValue)))
      printWriter.close()
    } match
      case Success(_) => Right(FILE_NAME)
      case Failure(e) => Left((e.getMessage, FILE_NAME))

  override def load: Either[(String, String), (String, String)] =
    Try(Source.fromFile(FILE_NAME).getLines.mkString) match
      case Success(fieldValue) => Right((fieldValue, FILE_NAME))
      case Failure(e)          => Left((e.getMessage, FILE_NAME))
