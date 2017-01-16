package de.htwg.se.durak.model

import scala.util.Random

case class Deck(cards: List[Card]) {
  def this(startWith: Rank) = {
    this(Deck.getInitialCards(startWith))
  }

  def isEmpty: Boolean = numberOfCards == 0
  def numberOfCards: Int = this.cards.size
  def drawNCards(n: Int): Tuple2[List[Card], Deck] = new Tuple2(cards.slice(0, Math.min(numberOfCards, n)), new Deck(cards.slice(Math.min(numberOfCards, n), numberOfCards)))
  def shuffle: Deck = Deck(Random.shuffle(cards))
  override def toString: String = "Deck [size: " + numberOfCards + "]"

  //Erste Karte wird nach Hinten geschoben, aber: da gibt´s doch sicher noch ne elegantere Methode?!?!
  //def defineTrumpCard: Deck = Deck(cards.slice(1, cards.size-2) :+ cards(cards.size-1))

  def defineTrumpCard: Deck = {
    //alte defineTrumpCard-Methode
    //Erste Karte wird nach Hinten geschoben, aber: da gibt´s doch sicher noch ne elegantere Methode?!?!
    //def defineTrumpCard: Deck = Deck(cards.slice(1, cards.size-2) :+ cards(cards.size-1))

    //aktuelles Deck wird ausgehend von zweiten Karte durchlaufen, zu neuem Deck hinzugefügt, zum Schluss die erste Karte angehängt
    //dabei wird isTrump entsprechend der Trumpfkarte (ist die ursprünglich erste Karte) neu gesetzt
    Deck(
      (for (card <- cards if card != cards(0)) yield { card.copy(isTrump = (card.suit == cards(0).suit)) }) :+ cards(0).copy(isTrump = true)
    )
  }

  //Suit der letzten Karte wird zurückgegeben, macht erst Sinn, nachdem defineTrumpCard aufgerufen wurde
  def getTrumpSuit: Suit = cards(cards.size - 1).suit

  def toXml = {
    <deck>{ cards.map(c => c.toXml) }</deck>
  }
}

object Deck {
  def getInitialCards(startWith: Rank): List[Card] = {
    val cards = for (suit <- Suit.values; rank <- Rank.values if rank >= startWith) yield Card(suit, rank, false)
    Random.shuffle(cards)
  }

  def fromXml(node: scala.xml.Node) = {
    val cardNodes = (node \\ "card")
    val cards = (for (card <- cardNodes) yield (Card.fromXml(card.head))).toList
    Deck(cards)
  }
}