package fileIOComponent

trait FileIOInterface:
  def save(field: String): Either[(String, String), String]
  def load: Either[(String, String), (String, String)]
