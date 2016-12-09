package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlayerSpec extends WordSpec with Matchers {
  "A Player" should {
    val player1 = Player("Max", 1, List[Card]())
    val player2 = Player("Erika", 2, List[Card](Card(Suit.Diamonds, Rank.Nine), Card(Suit.Spades, Rank.Queen)))
    "take cards" in {
      player1.takeCards(Card(Suit.Hearts, Rank.Eight)) should be(Player("Max", 1, List[Card](Card(Suit.Hearts, Rank.Eight))))
      player2.takeCards(Card(Suit.Clubs, Rank.Jack)) should be(Player("Erika", 2, List[Card](Card(Suit.Clubs, Rank.Jack), Card(Suit.Diamonds, Rank.Nine), Card(Suit.Spades, Rank.Queen))))
      player1.takeCards(List(Card(Suit.Clubs, Rank.Jack), Card(Suit.Hearts, Rank.Seven))) should be(Player("Max", 1, List[Card](Card(Suit.Clubs, Rank.Jack), Card(Suit.Hearts, Rank.Seven))))
      player2.takeCards(List(Card(Suit.Clubs, Rank.Jack), Card(Suit.Hearts, Rank.Seven))) should be(Player("Erika", 2, List[Card](Card(Suit.Clubs, Rank.Jack), Card(Suit.Hearts, Rank.Seven), Card(Suit.Diamonds, Rank.Nine), Card(Suit.Spades, Rank.Queen))))
    }

    "put down a card" in {
      player2.putDownCard(Card(Suit.Diamonds, Rank.Nine)) should be(Tuple2[Card, Player](Card(Suit.Diamonds, Rank.Nine), Player("Erika", 2, List[Card](Card(Suit.Spades, Rank.Queen)))))
      a[IllegalArgumentException] should be thrownBy {
        player2.putDownCard(Card(Suit.Clubs, Rank.Queen))
      }
      the[IllegalArgumentException] thrownBy {
        player1.putDownCard(Card(Suit.Clubs, Rank.Queen))
      } should have message ("Player does not have this card in his/her hand")
    }

    "should be defender or attacker or inactive" in {
      player1.isDefender should be(false)
      player1.isAttacker should be(false)
      val defendingPlayer1 = player1.setStatus(PlayerStatus.Defender)
      defendingPlayer1.isDefender should be(true)
      defendingPlayer1.isAttacker should be(false)
      val attackingPlayer1 = player1.setStatus(PlayerStatus.Attacker)
      attackingPlayer1.isAttacker should be(true)
      attackingPlayer1.isDefender should be(false)
    }

    "should be checked whether (s)he has a card on hand" in {
      player1.hasCard(Card(Suit.Hearts, Rank.Seven)) should be(false)
      player2.hasCard(Card(Suit.Diamonds, Rank.Nine)) should be(true)
    }

    "have a string representation" in {
      player1.toString should be("Player [name: Max, number: 1, cards: List(), status: inactive, hisTurn: false]")
    }

    "update whether its his turn" in {
      player1.changeTurn() should be(Player("Max", 1, List[Card](), PlayerStatus.Inactive, true))
      Player("Max", 1, List[Card](), PlayerStatus.Inactive, true).changeTurn() should be(player1)
    }

    "return the number of cards in his hand" in {
      player1.numberOfCards should be(0)
      player2.numberOfCards should be(2)
    }

    var player = Player("Jakob", 3, List(Card(Suit.Clubs, Rank.Five), Card(Suit.Clubs, Rank.Nine), Card(Suit.Hearts, Rank.Two), Card(Suit.Clubs, Rank.Three)))

    "set the trump suit for all its cards" in {
      player = player.setTrumpSuit(Suit.Clubs)
      player.cards.contains(Card(Suit.Clubs, Rank.Five, true)) should be(true)
      player.cards.contains(Card(Suit.Clubs, Rank.Three, true)) should be(true)
      player.cards.contains(Card(Suit.Clubs, Rank.Nine, true)) should be(true)
      player.cards.contains(Card(Suit.Hearts, Rank.Two)) should be(true)
    }

    "return his smallest trump card" in {
      player.getSmallestTrumpCard should be(Card(Suit.Clubs, Rank.Three, true))
    }
  }
}