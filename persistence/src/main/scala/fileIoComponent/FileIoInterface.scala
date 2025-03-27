package fileIoComponent

import common.model.fieldService.FieldInterface

trait FileIOInterface:
  def save(field: FieldInterface): Either[String, String]
  def load: FieldInterface
