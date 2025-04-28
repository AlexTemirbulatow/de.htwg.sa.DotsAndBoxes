package persistence.databaseComponent

import scala.concurrent.Future

trait DAOInterface:
  def init: Unit
  def create: Int
  def read: Unit
  def update: Unit
  def delete: Unit
