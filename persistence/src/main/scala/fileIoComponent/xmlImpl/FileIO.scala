package fileIOComponent.xmlImpl

import fileIOComponent.FileIOInterface
import java.io.{File, PrintWriter}
import scala.util.{Failure, Success, Try}
import scala.xml.{PrettyPrinter, XML}

class FileIO extends FileIOInterface:
  private val FILE_NAME = "field.xml"

  override def save(fieldValue: String): Either[(String, String), String] =
    Try {
      val prettyPrinter = new PrettyPrinter(120, 4)
      val printWriter = new PrintWriter(new File(FILE_NAME))
      printWriter.write(prettyPrinter.format(XML.loadString(fieldValue)))
      printWriter.close()
    } match
      case Success(_) => Right(FILE_NAME)
      case Failure(e) => Left((e.getMessage, FILE_NAME))

  override def load: Either[(String, String), (String, String)] =
    Try(scala.xml.XML.loadFile(FILE_NAME).toString) match
      case Success(fieldValue) => Right((fieldValue, FILE_NAME))
      case Failure(e)          => Left((e.getMessage, FILE_NAME))
