package de.htwg.se.durak.model

import org.scalatest._

class CardSpec extends WordSpec with Matchers {
  "A Card" should {
    val card = Card(Suit.Spades, Rank.Ace);
    
    "have a suit" in {
      card.suit should be(Suit.Spades)
    }
    
    "have a rank" in {
      card.rank should be(Rank.Ace)
    }
    
    "have a string presentation" in {
      card.toString should be("Card [suit: spades, rank: ace]")
    }
    
    "be compareable to other cards in rank" in {
      card > Card(Suit.Spades, Rank.King) should be(true)
      card > Card(Suit.Spades, Rank.Ace) should be(false)
      Card(Suit.Hearts, Rank.Ten) > Card(Suit.Spades, Rank.Jack) should be(false)
    }
    
    "be compareable to other cards in suit" in {
      card.isSameSuit(Card(Suit.Spades, Rank.Two)) should be(true)
      card.isSameSuit(Card(Suit.Diamonds, Rank.Three)) should be(false)
    }
  }
}