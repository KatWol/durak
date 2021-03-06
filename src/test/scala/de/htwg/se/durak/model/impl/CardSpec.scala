package de.htwg.se.durak.model.impl

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit

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

    "have an Xml presentation" in {
      card.toXml.toString should be("<card><suit>spades</suit><rank>ace</rank><isTrump>false</isTrump></card>")
    }

    "be parse from its Xml presentation" in {
      Card.fromXml(<card><suit>spades</suit><rank>ace</rank><isTrump>false</isTrump></card>) should be(card)
    }

    "be compareable to other cards" in {
      card.compare(Card(Suit.Spades, Rank.King)) should be > 0
      card.compare(Card(Suit.Hearts, Rank.Ace)) should be(0)
      card.compare(Card(Suit.Hearts, Rank.Two, true)) should be < 0
    }

    "return a card when a suit and rank is entered as a string" in {
      Card.parseToCard("hearts", "jack") should be(Card(Suit.Hearts, Rank.Jack))
      Card.parseToCard("c", "10") should be(Card(Suit.Clubs, Rank.Ten))

      Card.parseToCard("hello", "10") should be(null)
      Card.parseToCard("Diamonds", "13") should be(null)
    }
  }
}