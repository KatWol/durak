package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AttackSpec extends WordSpec with Matchers{
  "An Attack" should {
    val attack = Attack(attackingCard = Card(Suit.Clubs, Rank.Seven))
    val completedAttack = Attack(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Nine))
    
    "indicate whether it is completed" in {
      attack.isCompleted should be (false)
      completedAttack.isCompleted should be (true)
    }
    
    "be defended" in {
      attack.defend(Card(Suit.Clubs, Rank.Nine)) should be (Attack(Card(Suit.Clubs, Rank.Seven), Card(Suit.Clubs, Rank.Nine)))
    }
    
    "only allow valid cards for the defence" in {
      an [IllegalArgumentException] should be thrownBy {
        attack.defend(Card(Suit.Clubs, Rank.Six))
      }
      the [IllegalArgumentException] thrownBy {
        attack.defend(Card(Suit.Clubs, Rank.Six))
      } should have message ("This is not a legal card")
    }
    
    "be checked for whether both cards have the same suit" in {
      completedAttack.isSameSuit should be (true)
      attack.isSameSuit should be (false)
      Attack(Card(Suit.Clubs, Rank.Seven), Card(Suit.Hearts, Rank.Nine)).isSameSuit should be (false)
    }
    
    "only allow a defence if the attack is not completed" in {
      an [IllegalArgumentException] should be thrownBy {
        completedAttack.defend(Card(Suit.Clubs, Rank.Six))
      }
      the [IllegalArgumentException] thrownBy {
        completedAttack.defend(Card(Suit.Clubs, Rank.Six))
      } should have message ("This attack is already completed")
    }
    
    "have a string representation" in {
      attack.toString should be ("Attack [attackingCard: Card [suit: clubs, rank: seven], defendingCard: null]")
      completedAttack.toString should be ("Attack [attackingCard: Card [suit: clubs, rank: seven], defendingCard: Card [suit: clubs, rank: nine]]")
    }
  }
}