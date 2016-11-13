package de.htwg.se.durak.model

case class Card(suit: Suit, rank: Rank) {
  override def toString():String = {
    "Card [suit: " + suit + ", rank: " + rank + "]"
  }
  
  def >(that: Card):Boolean = this.rank > that.rank
  def <(that: Card):Boolean = this.rank < that.rank
  
  def isSameSuit(that: Card):Boolean = this.suit == that.suit
}

