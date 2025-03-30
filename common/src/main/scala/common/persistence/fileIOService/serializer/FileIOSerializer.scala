package common.persistence.fileIOService.serializer

import common.model.fieldService.FieldInterface
import common.model.fieldService.converter.FieldConverter
import de.github.dotsandboxes.lib.FileFormat

object FileIOSerializer:
  def serialize(field: FieldInterface, fileFormat: FileFormat): String = fileFormat match
    case FileFormat.JSON => FieldConverter.toJson(field).toString
    case FileFormat.XML  => FieldConverter.toXml(field).toString
