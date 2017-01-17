package de.htwg.se.durak.model

trait Deck {
  val startWith: Rank = null
  def isEmpty: Boolean
  def numberOfCards: Int
  def drawNCards(n: Int): Tuple2[List[Card], Deck]
  def getCards: List[Card]
  def toString: String
  def defineTrumpCard: Deck
  def getTrumpSuit: Suit
  def toXml: scala.xml.Node
}

trait DeckFactory {
  def create(startWith: Rank): Deck
}