package controllerComponent.controllerImpl.observer

import de.github.dotsandboxes.lib.Event

trait Observer:
  def update(event: Event): Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(newSub: Observer) = subscribers = subscribers :+ newSub
  def remove(oldSub: Observer) = subscribers = subscribers.filterNot(o => o == oldSub)
  def notifyObservers(event: Event) = subscribers.foreach(o => o.update(event))

class ObserverHttp(url: String) extends Observer:
  import scala.io.Source
  import scala.util.Using

  override def update(event: Event): Unit =
    val requestUrl = s"$url?event=${event.toString.toLowerCase}"
    Using.resource(Source.fromURL(requestUrl)) { _ => () }
