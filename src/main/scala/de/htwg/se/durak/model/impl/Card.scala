package de.htwg.se.durak.model.impl

import de.htwg.se.durak.model.{ Card => CardTrait }
import de.htwg.se.durak.model.{ CardFactory => CardFactoryTrait }
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit
import scala.language.implicitConversions

case class Card(suit: Suit, rank: Rank, isTrump: Boolean = false) extends CardTrait {
  override def toString(): String = {
    "Card [suit: " + suit + ", rank: " + rank + ", trump: " + isTrump + "]"
  }

  override def compare(that: CardTrait) = {
    val internalCard = that.asInstanceOf[Card]
    if (isSameSuit(internalCard)) this.rank.pointValue - internalCard.rank.pointValue
    else {
      if (this.isTrump && !internalCard.isTrump) 1
      else if (!this.isTrump && internalCard.isTrump) -1
      else 0
    }
  }

  def isSameSuit(that: Card): Boolean = this.suit == that.suit

  override def setTrump: Card = this.copy(isTrump = true)
  override def toXml: scala.xml.Node = {
    <card><suit>{ suit.toString }</suit><rank>{ rank.toString }</rank><isTrump>{ isTrump }</isTrump></card>
  }
}

object Card {
  def parseToCard(suit: String, rank: String, isTrump: Boolean = false): Card = {
    var newRank: Rank = Rank.parseFromString(rank)

    var newSuit: Suit = Suit.parseFromString(suit)

    if (newRank != null && newSuit != null) Card(newSuit, newRank, isTrump)
    else null
  }

  def fromXml(node: scala.xml.Node): Card = {
    val suit = (node \ "suit").text
    val rank = (node \ "rank").text
    val isTrump = (node \ "isTrump").text.toBoolean
    parseToCard(suit, rank, isTrump)
  }

  //Aus irgendwelchen Gründen kann man nicht auf die min Methoden von Ordered drauf zugreifen, wenn man eine List[Card] hat
  def min(cards: List[Card]): Card = {
    var minCard = cards(0)
    for (card <- cards) {
      if (card < minCard) {
        minCard = card
      }
    }
    minCard
  }
}

class CardFactory extends CardFactoryTrait {
  override def create(suit: String, rank: String, isTrump: Boolean = false): Card = Card.parseToCard(suit, rank, isTrump)
}

