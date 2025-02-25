package persistence
package fileIoComponent

import fieldComponent.FieldInterface

trait FileIOInterface:
  def save(field: FieldInterface): Either[String, String]
  def load: FieldInterface
