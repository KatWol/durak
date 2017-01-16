package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DeckSpec extends WordSpec with Matchers {

  "A Deck" when {
    "empty" should {

      val emptyDeck = Deck(List[Card]())

      "be empty" in {
        emptyDeck.isEmpty should be(true);
      }

      "have size 0" in {
        emptyDeck.numberOfCards should be(0);
      }

      "return a tuple with an empty list and an empty deck if the deck is empty" in {
        emptyDeck.drawNCards(1) should be((List[Card](), Deck(List[Card]())))
      }
    }

    "non-empty" should {
      val deck = new Deck(Rank.Six)

      "not be empty" in {
        deck.isEmpty should be(false)
      }

      "have 36 cards if Durak Deck starts with rank six" in {
        deck.numberOfCards should be(36)
      }

      "have the correct cards if full Durak Deck starts with rank six" in {
        deck.cards.contains(Card(Suit.Spades, Rank.Six)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Seven)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Eight)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Nine)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Ten)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Jack)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Queen)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.King)) should be(true)
        deck.cards.contains(Card(Suit.Spades, Rank.Ace)) should be(true)

        deck.cards.contains(Card(Suit.Clubs, Rank.Six)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Seven)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Eight)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Nine)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Ten)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Jack)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Queen)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.King)) should be(true)
        deck.cards.contains(Card(Suit.Clubs, Rank.Ace)) should be(true)

        deck.cards.contains(Card(Suit.Hearts, Rank.Six)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Seven)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Eight)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Nine)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Ten)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Jack)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Queen)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.King)) should be(true)
        deck.cards.contains(Card(Suit.Hearts, Rank.Ace)) should be(true)

        deck.cards.contains(Card(Suit.Diamonds, Rank.Six)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Seven)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Eight)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Nine)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Ten)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Jack)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Queen)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.King)) should be(true)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Ace)) should be(true)

        deck.cards.contains(Card(Suit.Diamonds, Rank.Five)) should be(false)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Four)) should be(false)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Three)) should be(false)
        deck.cards.contains(Card(Suit.Diamonds, Rank.Two)) should be(false)

        deck.cards.contains(Card(Suit.Hearts, Rank.Five)) should be(false)
        deck.cards.contains(Card(Suit.Hearts, Rank.Four)) should be(false)
        deck.cards.contains(Card(Suit.Hearts, Rank.Three)) should be(false)
        deck.cards.contains(Card(Suit.Hearts, Rank.Two)) should be(false)

        deck.cards.contains(Card(Suit.Clubs, Rank.Five)) should be(false)
        deck.cards.contains(Card(Suit.Clubs, Rank.Four)) should be(false)
        deck.cards.contains(Card(Suit.Clubs, Rank.Three)) should be(false)
        deck.cards.contains(Card(Suit.Clubs, Rank.Two)) should be(false)

        deck.cards.contains(Card(Suit.Spades, Rank.Five)) should be(false)
        deck.cards.contains(Card(Suit.Spades, Rank.Four)) should be(false)
        deck.cards.contains(Card(Suit.Spades, Rank.Three)) should be(false)
        deck.cards.contains(Card(Suit.Spades, Rank.Two)) should be(false)
      }

      "be shuffled" in {
        val shuffledDeck = deck.shuffle
        deck.cards should not be shuffledDeck.cards
      }

      val newDeck = Deck(List(Card(Suit.Spades, Rank.Two), Card(Suit.Hearts, Rank.Three), Card(Suit.Diamonds, Rank.Seven)))

      "have the correct size" in {
        newDeck.numberOfCards should be(3)
        deck.numberOfCards should be(36)
      }

      "return the top N cards when calling drawNCards" in {
        newDeck.drawNCards(1)._1 should be(List(Card(Suit.Spades, Rank.Two)))
        newDeck.drawNCards(2)._1 should be(List(Card(Suit.Spades, Rank.Two), Card(Suit.Hearts, Rank.Three)))
      }

      "return all cards left in the deck if more cards then are left in the deck are drawn" in {
        newDeck.drawNCards(6) should be(List(Card(Suit.Spades, Rank.Two), Card(Suit.Hearts, Rank.Three), Card(Suit.Diamonds, Rank.Seven)), new Deck(List[Card]()))
      }

      "return a new Deck without the top N cards when N cards are drawn" in {
        val returnedDeck = newDeck.drawNCards(1)._2
        returnedDeck shouldBe a[Deck]
        returnedDeck.numberOfCards should be(2)
        returnedDeck.cards should be(List(Card(Suit.Hearts, Rank.Three), Card(Suit.Diamonds, Rank.Seven)))
      }

      "have a string representation" in {
        newDeck.toString should be("Deck [size: 3]")
        deck.toString should be("Deck [size: 36]")
      }

      var deck2 = Deck(List(Card(Suit.Spades, Rank.Two), Card(Suit.Hearts, Rank.Three), Card(Suit.Spades, Rank.Seven, true)))
      "define the trump suit and add the first card to the back of the deck" in {
        deck2 = deck2.defineTrumpCard
        deck2 should be(Deck(List(Card(Suit.Hearts, Rank.Three), Card(Suit.Spades, Rank.Seven, true), Card(Suit.Spades, Rank.Two, true))))
      }

      "return the current trump suit" in {
        deck2.getTrumpSuit should be(Suit.Spades)
      }
      
      "have an Xml presentation" in {
        deck2.toXml.toString should be ("<deck><card><suit>hearts</suit><rank>three</rank><isTrump>false</isTrump></card><card><suit>spades</suit><rank>seven</rank><isTrump>true</isTrump></card><card><suit>spades</suit><rank>two</rank><isTrump>true</isTrump></card></deck>")
      }
      
      "be parsed from its Xml presentation" in {
        Deck.fromXml(<deck><card><suit>hearts</suit><rank>three</rank><isTrump>false</isTrump></card><card><suit>spades</suit><rank>seven</rank><isTrump>true</isTrump></card><card><suit>spades</suit><rank>two</rank><isTrump>true</isTrump></card></deck>) should be (deck2)
      }
    }

  }
}