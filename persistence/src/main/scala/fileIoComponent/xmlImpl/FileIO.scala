package fileIoComponent.xmlImpl

import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import fieldComponent.fieldImpl.FieldParser
import fileIoComponent.FileIOInterface
import java.io.{File, PrintWriter}
import scala.util.{Failure, Success, Try}
import scala.xml.PrettyPrinter

class FileIO extends FileIOInterface:
  override def save(field: FieldInterface): Either[String, String] =
    val filename = "field.xml"
    Try {
      val prettyPrinter = new PrettyPrinter(120, 4)
      val printWriter = new PrintWriter(new File(filename))
      printWriter.write(prettyPrinter.format(FieldConverter.toXml(field)))
      printWriter.close()
    } match
      case Success(_) => Right(s"Saved as $filename")
      case Failure(e) => Left(e.getMessage)

  override def load: FieldInterface =
    val fieldValue: String = scala.xml.XML.loadFile("field.xml").toString
    FieldParser.fromXml(fieldValue)
