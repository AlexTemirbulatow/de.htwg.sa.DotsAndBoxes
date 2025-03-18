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
  import akka.actor.ActorSystem
  import scala.concurrent.ExecutionContext
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model.HttpRequest

  implicit val system: ActorSystem = ActorSystem()
  implicit val executionContext: ExecutionContext = system.dispatcher

  override def update(event: Event): Unit =
    val requestUrl = s"$url?event=${event.toString.toLowerCase}"
    Http().singleRequest(HttpRequest(uri = requestUrl)).foreach(_ => ())
