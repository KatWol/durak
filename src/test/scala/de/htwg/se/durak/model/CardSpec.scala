package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
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
      card.toString should be("Card [suit: spades, rank: ace, trump: false]")
    }

    "be compareable to other cards" in {
      card should be > Card(Suit.Spades, Rank.King)
      card should be >= Card(Suit.Hearts, Rank.Ace)
      card should be < Card(Suit.Hearts, Rank.Two, true)
    }

    "return a card when a suit and rank is entered as a string" in {
      Card.parseToCard("hearts", "jack") should be(Card(Suit.Hearts, Rank.Jack))
      Card.parseToCard("c", "10") should be(Card(Suit.Clubs, Rank.Ten))

      Card.parseToCard("hello", "10") should be(null)
      Card.parseToCard("Diamonds", "13") should be(null)
    }
  }
}