package de.htwg.se.durak.model.impl

import org.junit.runner.RunWith
import org.scalatest.Matchers
import org.scalatest.WordSpec
import org.scalatest.junit.JUnitRunner

import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit

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
      player2.putDownCard(Card(Suit.Diamonds, Rank.Nine)) should be(Player("Erika", 2, List[Card](Card(Suit.Spades, Rank.Queen))))
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
      player1.toString should be("Player [name: Max, number: 1, cards: List(), status: Inactive, hisTurn: false]")
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

    "set the turn correct" in {
      player = player.setTurn(true)
      player.hisTurn should be(true)
      player = player.setTurn(false)
      player.hisTurn should be(false)
    }

    "have a Xml presentation" in {
      val player1 = Player("Max", 1, List[Card]())
      player1.toXml.toString should be("<player><name>Max</name><number>1</number><cards></cards><status>Inactive</status><hisTurn>false</hisTurn></player>")
    }

    "be parsed from its Xml presentation" in {
      Player.fromXml(<player><name>Max</name><number>1</number><cards></cards><status>Inactive</status><hisTurn>false</hisTurn></player>) should be(player1)
      Player.fromXml(<player><name>Max</name><number>1</number><cards><card><suit>hearts</suit><rank>nine</rank><isTrump>false</isTrump></card></cards><status>Inactive</status><hisTurn>false</hisTurn></player>) should be(Player("Max", 1, List[Card](Card(Suit.Hearts, Rank.Nine))))
    }
  }
}