package de.htwg.se.util

import scala.swing.event.Event

trait Observer {
  def update
  def update(e: Event)
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def add(s: Vector[Observer]) = subscribers ++= s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers = subscribers.foreach(o => o.update)
  def notifyObservers(e: Event) = subscribers.foreach(o => o.update(e: Event))
}
