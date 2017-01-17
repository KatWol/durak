package de.htwg.se.durak.model.impl

import scala.util.Random
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit
import de.htwg.se.durak.model.{ Deck => DeckTrait }
import de.htwg.se.durak.model.{ DeckFactory => DeckFactoryTrait }

case class Deck(override val startWith: Rank) extends DeckTrait {
  var cards = Deck.getInitialCards(startWith)

  def this(cards: List[Card]) = {
    this(Rank.Seven)
    this.cards = cards
  }

  override def isEmpty: Boolean = numberOfCards == 0
  override def numberOfCards: Int = this.cards.size
  override def drawNCards(n: Int): Tuple2[List[Card], Deck] = new Tuple2(cards.slice(0, Math.min(numberOfCards, n)), new Deck(cards = cards.slice(Math.min(numberOfCards, n), numberOfCards)))
  override def getCards: List[Card] = cards
  def shuffle: Deck = new Deck(Random.shuffle(cards))
  override def toString: String = "Deck [size: " + numberOfCards + "]"

  //Erste Karte wird nach Hinten geschoben, aber: da gibt´s doch sicher noch ne elegantere Methode?!?!
  //def defineTrumpCard: Deck = Deck(cards.slice(1, cards.size-2) :+ cards(cards.size-1))

  override def defineTrumpCard: Deck = {
    //alte defineTrumpCard-Methode
    //Erste Karte wird nach Hinten geschoben, aber: da gibt´s doch sicher noch ne elegantere Methode?!?!
    //def defineTrumpCard: Deck = Deck(cards.slice(1, cards.size-2) :+ cards(cards.size-1))

    //aktuelles Deck wird ausgehend von zweiten Karte durchlaufen, zu neuem Deck hinzugefügt, zum Schluss die erste Karte angehängt
    //dabei wird isTrump entsprechend der Trumpfkarte (ist die ursprünglich erste Karte) neu gesetzt
    new Deck(
      (for (card <- cards if card != cards(0)) yield { card.copy(isTrump = (card.suit == cards(0).suit)) }) :+ cards(0).copy(isTrump = true)
    )
  }

  //Suit der letzten Karte wird zurückgegeben, macht erst Sinn, nachdem defineTrumpCard aufgerufen wurde
  override def getTrumpSuit: Suit = cards(cards.size - 1).suit

  override def toXml: scala.xml.Node = {
    <deck>{ cards.map(c => c.toXml) }</deck>
  }
}

object Deck {
  def getInitialCards(startWith: Rank): List[Card] = {
    val cards = for (suit <- Suit.values; rank <- Rank.values if rank >= startWith) yield Card(suit, rank, false)
    Random.shuffle(cards)
  }

  def fromXml(node: scala.xml.Node): Deck = {
    val cardNodes = (node \\ "card")
    val cards = (for (card <- cardNodes) yield (Card.fromXml(card.head))).toList
    new Deck(cards)
  }
}

class DeckFactory extends DeckFactoryTrait {
  override def create(startWith: Rank) = new Deck(startWith)
}