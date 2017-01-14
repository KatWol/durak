package de.htwg.se.durak.model

case class Card(suit: Suit, rank: Rank, isTrump: Boolean = false) extends Ordered[Card] {
  override def toString(): String = {
    "Card [suit: " + suit + ", rank: " + rank + ", trump: " + isTrump + "]"
  }

  override def compare(that: Card) = {
    if (isSameSuit(that)) this.rank.pointValue - that.rank.pointValue
    else {
      if (this.isTrump && !that.isTrump) 1
      else if (!this.isTrump && that.isTrump) -1
      else 0
    }
  }

  def isSameSuit(that: Card): Boolean = this.suit == that.suit

}

object Card {
  def parseToCard(suit: String, rank: String): Card = {
    var newRank: Rank = null
    rank match {
      case "Ace" | "ace" | "a" => newRank = Rank.Ace
      case "King" | "king" | "k" => newRank = Rank.King
      case "Queen" | "queen" | "q" => newRank = Rank.Queen
      case "Jack" | "jack" | "j" => newRank = Rank.Jack
      case "Ten" | "ten" | "10" => newRank = Rank.Ten
      case "Nine" | "nine" | "9" => newRank = Rank.Nine
      case "Eight" | "eight" | "8" => newRank = Rank.Eight
      case "Seven" | "seven" | "7" => newRank = Rank.Seven
      case "Six" | "six" | "6" => newRank = Rank.Six
      case "Five" | "five" | "5" => newRank = Rank.Five
      case "Four" | "four" | "4" => newRank = Rank.Four
      case "Three" | "three" | "3" => newRank = Rank.Three
      case "Two" | "two" | "2" => newRank = Rank.Two
      case _ => newRank
    }

    var newSuit: Suit = null
    suit match {
      case "Hearts" | "hearts" | "h" => newSuit = Suit.Hearts
      case "Diamonds" | "diamonds" | "d" => newSuit = Suit.Diamonds
      case "Spades" | "spades" | "s" => newSuit = Suit.Spades
      case "Clubs" | "clubs" | "c" => newSuit = Suit.Clubs
      case _ => newSuit
    }

    if (newRank != null && newSuit != null) Card(newSuit, newRank)
    else null
  }
}

