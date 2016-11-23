package de.htwg.se.durak.model

case class Card(suit: Suit, rank: Rank, isTrump: Boolean = false) extends Ordered[Card] {
  override def toString(): String = {
    "Card [suit: " + suit + ", rank: " + rank + "]"
  }

  override def compare(that: Card) = {
    if (this.suit == that.suit) this.rank.pointValue - that.rank.pointValue
    else {
      if (this.isTrump && !that.isTrump) 1
      else if (!this.isTrump && that.isTrump) -1
      else 1
    }
  }
}

