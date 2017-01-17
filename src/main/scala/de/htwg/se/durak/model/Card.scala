package de.htwg.se.durak.model

trait Card extends Ordered[Card] {
  def toString: String
  val suit: Suit
  val rank: Rank
  val isTrump: Boolean
  def toXml: scala.xml.Node
  def setTrump: Card
}

trait CardFactory {
  def create(suit: String, rank: String, isTrump: Boolean = false): Card
}

