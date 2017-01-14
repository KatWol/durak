package de.htwg.se.util

trait Observer {
  def update
}

trait Observable {
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def add(s: Vector[Observer]) = subscribers ++= s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => 0 == s)
  def notifyObservers = subscribers.foreach(o => o.update)
}
