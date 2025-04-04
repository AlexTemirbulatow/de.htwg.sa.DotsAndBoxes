package persistence.fileIOComponent.xmlImpl

import java.io.{File, PrintWriter}
import persistence.fileIOComponent.FileIOInterface
import scala.util.{Failure, Success, Try}
import scala.xml.{PrettyPrinter, XML}

class FileIO extends FileIOInterface:
  private val FILE_PATH = ""
  private val FILE_EXTENSION = ".xml"

  override def save(fieldValue: String, filename: String): Either[(String, String), String] =
    val fullPath = FILE_PATH + filename + FILE_EXTENSION
    Try {
      val prettyPrinter = new PrettyPrinter(120, 4)
      val printWriter = new PrintWriter(new File(fullPath))
      printWriter.write(prettyPrinter.format(XML.loadString(fieldValue)))
      printWriter.close()
    } match
      case Success(_) => Right(fullPath)
      case Failure(e) => Left((e.getMessage, fullPath))

  override def load(filename: String): Either[(String, String), (String, String)] =
    val fullPath = FILE_PATH + filename + FILE_EXTENSION
    Try(scala.xml.XML.loadFile(fullPath).toString) match
      case Success(fieldValue) => Right((fieldValue, fullPath))
      case Failure(e)          => Left((e.getMessage, fullPath))
