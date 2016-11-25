package de.htwg.se.durak.controller

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import de.htwg.se.durak.model._

@RunWith(classOf[JUnitRunner])
class RoundSpec extends WordSpec with Matchers {  
  val card1 = Card(Suit.Hearts, Rank.Jack, true)
  val card2 = Card(Suit.Clubs, Rank.Seven)
  val card3 = Card(Suit.Diamonds, Rank.Ten)
  val card4 = Card(Suit.Clubs, Rank.Queen)
  val card5 = Card(Suit.Hearts, Rank.Seven, true)
  val card6 = Card(Suit.Spades, Rank.Ace)
  var player1 = Player("Max", 1, List[Card](card1, card2, card3), PlayerStatus.Attacker)
  var player2 = Player("Erika", 2, List[Card](card4, card5, card6), PlayerStatus.Defender)
  var round = new Round(new Deck(Rank.Seven, Suit.Hearts), List[Player](player1, player2), activePlayerNumber = 1)
    
  override def withFixture(test: NoArgTest) = {
    player1 = Player("Max", 1, List[Card](card1, card2, card3), PlayerStatus.Attacker)
    player2 = Player("Erika", 2, List[Card](card4, card5, card6), PlayerStatus.Defender)
    round = new Round(new Deck(Rank.Seven, Suit.Hearts), List[Player](player1, player2), activePlayerNumber = 1)
    test()
  }
  
  "A Round" should {
    "return the active Player" in {
      round.getActivePlayer() should be (player1)
    }
    
    "start with the active player starting an attack" in {
      round.startFirstAttack(card2)
      round.attacks should contain (Attack(card2))
      round.getActivePlayer().cards should not contain (card2)
    }
    
    "check if a value is already on the table" in {
      round.attacks = List(Attack(card2, card4))
      round.isRankOnTable(Rank.Seven) should be (true)
      round.isRankOnTable(Rank.Queen) should be (true)
      round.isRankOnTable(Rank.Six) should be (false)
      
      round.attacks = Attack(card3) :: round.attacks
      round.isRankOnTable(Rank.Two) should be (false)
    }
    
    "check if the card put on the table is a valid card" ignore {
      round.isValid(card2) should be (true)
      round.isValid(card5) should be (false)
    }
    
    "continue with the defending player defending the attack" ignore {
      round.startAttack(card2)
      round.defendAttack(card4)
      round.attacks should contain (Attack(card2, card4))
    }
  }
  
}