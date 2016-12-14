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
      card.toString should be("Card [suit: spades, rank: ace]")
    }

    "be compareable to other cards" in {
      card should be > Card(Suit.Spades, Rank.King)
      card should be >= Card(Suit.Hearts, Rank.Ace)
      card should be < Card(Suit.Hearts, Rank.Two, true)
    }
  }
}