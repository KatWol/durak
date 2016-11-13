package de.htwg.se.durak.model

import org.scalatest._

class PlayerSpec extends WordSpec with Matchers {
  "A Player" should {
    val player1 = Player("Max", 1, List[Card]())
    val player2 = Player("Erika", 2, List[Card](Card(Suit.Diamonds, Rank.Nine), Card(Suit.Spades, Rank.Queen)))
    "take a card" in {
      player1.takeCard(Card(Suit.Hearts, Rank.Eight)) should be (Player("Max", 1, List[Card](Card(Suit.Hearts, Rank.Eight))))
      player2.takeCard(Card(Suit.Clubs, Rank.Jack)) should be (Player("Erika", 2, List[Card](Card(Suit.Clubs, Rank.Jack), Card(Suit.Diamonds, Rank.Nine), Card(Suit.Spades, Rank.Queen))))
    }
    
    "put down a card" in {
      player2.putDownCard(Card(Suit.Diamonds, Rank.Nine)) should be (Tuple2[Card, Player](Card(Suit.Diamonds, Rank.Nine), Player("Erika", 2, List[Card](Card(Suit.Spades, Rank.Queen)))))
      a [IllegalArgumentException] should be thrownBy {
        player2.putDownCard(Card(Suit.Clubs, Rank.Queen))
      }
      the [IllegalArgumentException] thrownBy {
        player1.putDownCard(Card(Suit.Clubs, Rank.Queen))
      } should have message ("Player does not have this card in his/her hand")
    }
    
    "should be defender or attacker or neither" in {
      player1.isDefender should be (false)
      player1.isAttacker should be (false)
      val defendingPlayer1 = player1.makeDefender
      defendingPlayer1.isDefender should be (true)
      defendingPlayer1.isAttacker should be (false)
      val attackingPlayer1 = player1.makeAttacker
      attackingPlayer1.isAttacker should be (true)
      attackingPlayer1.isDefender should be (false)
      
      a [IllegalArgumentException] should be thrownBy {
        Player("Marina", 3, List[Card](), true, true)
      }
      the [IllegalArgumentException] thrownBy {
        Player("Marina", 3, List[Card](), true, true)
      } should have message ("A player can only be attacker or defender")
    }
    
    "have a string representation" in {
      player1.toString should be ("Player [name: Max, number: 1, cards: List(), defender: false, attacker: false]")
    }
  }
}