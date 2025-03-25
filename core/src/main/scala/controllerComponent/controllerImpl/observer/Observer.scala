package controllerComponent.controllerImpl.observer

import de.github.dotsandboxes.lib.Event

trait Observer:
  def id: String
  def update(event: Event): Unit

class ObserverHttp(observerUrl: String) extends Observer:
  import scala.io.Source
  import scala.util.Using

  override def id: String = observerUrl
  override def update(event: Event): Unit =
    val baseUrl = id
    val requestUrl = s"$baseUrl?event=${event.toString.toLowerCase}"
    Using.resource(Source.fromURL(requestUrl)) { _ => () }

trait Observable:
  var subscribers: Map[String, Observer] = Map()
  def add(newSub: Observer) = subscribers += (newSub.id -> newSub) 
  def remove(observerUrl: String) = subscribers -= observerUrl
  def notifyObservers(event: Event) = subscribers.values.foreach(_.update(event))
