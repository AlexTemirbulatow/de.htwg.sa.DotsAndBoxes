package persistence.fileIOComponent

trait FileIOInterface:
  def save(field: String, filename: String): Either[(String, String), String]
  def load(filename: String): Either[(String, String), (String, String)]
