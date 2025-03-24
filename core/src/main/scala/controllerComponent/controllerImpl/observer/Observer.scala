package controllerComponent.controllerImpl.observer

import de.github.dotsandboxes.lib.Event

trait Observer:
  def update(event: Event): Unit
  def url: String

class ObserverHttp(observerUrl: String) extends Observer:
  import scala.io.Source
  import scala.util.Using

  override def url: String = observerUrl
  override def update(event: Event): Unit =
    val requestUrl = s"$url?event=${event.toString.toLowerCase}"
    Using.resource(Source.fromURL(requestUrl)) { _ => () }

trait Observable:
  var subscribers: Map[String, Observer] = Map()
  def add(newSub: Observer) = subscribers += (newSub.url -> newSub) 
  def remove(observerUrl: String) = subscribers -= observerUrl
  def notifyObservers(event: Event) = subscribers.values.foreach(_.update(event))
