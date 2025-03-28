package fileIoComponent.jsonImpl

import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import fieldComponent.fieldImpl.FieldParser
import fileIoComponent.FileIOInterface
import java.io.{File, PrintWriter}
import play.api.libs.json.Json
import scala.io.Source
import scala.util.{Failure, Success, Try}

class FileIO extends FileIOInterface:
  override def save(field: FieldInterface): Either[String, String] =
    val filename = "field.json"
    Try {
      val printWriter = new PrintWriter(new File(filename))
      printWriter.write(Json.prettyPrint(FieldConverter.toJson(field)))
      printWriter.close()
    } match
      case Success(_) => Right(s"Saved as $filename")
      case Failure(e) => Left(e.getMessage)

  override def load: FieldInterface =
    val fieldValue: String = Source.fromFile("field.json").getLines.mkString
    FieldParser.fromJson(fieldValue)
