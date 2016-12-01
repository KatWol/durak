package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class AttackSpec extends WordSpec with Matchers {
  "An Attack" should {
    val attack = Attack(Card(Suit.Hearts, Rank.Seven))
    val attack2 = Attack(Card(Suit.Hearts, Rank.Seven), Card(Suit.Hearts, Rank.Nine))

    "be checked whether it is completed" in {
      attack.isCompleted should be(false)
      attack2.isCompleted should be(true)
    }

    "should be defended" in {
      attack.defend(Card(Suit.Hearts, Rank.Nine)) should be(attack2)
    }

    "should throw an error if it was previous completed" in {
      an[IllegalArgumentException] should be thrownBy {
        attack2.defend(Card(Suit.Hearts, Rank.Ten))
      }
      the[IllegalArgumentException] thrownBy {
        attack2.defend(Card(Suit.Hearts, Rank.Ten))
      } should have message ("This attack is already completed")
    }

    "should check whether a card is a valid card for the attack" in {
      attack.isValid(Card(Suit.Hearts, Rank.Eight)) should be(true)
      attack.isValid(Card(Suit.Clubs, Rank.Eight)) should be(false)
      attack.isValid(Card(Suit.Clubs, Rank.Eight, true)) should be(true)
      attack.isValid(Card(Suit.Hearts, Rank.Seven)) should be(false)

      attack2.isValid(Card(Suit.Hearts, Rank.Eight)) should be(false)
    }

    "should throw an error if the defending card is invalid" in {
      an[IllegalArgumentException] should be thrownBy {
        attack.defend(Card(Suit.Clubs, Rank.Ten))
      }
      the[IllegalArgumentException] thrownBy {
        attack.defend(Card(Suit.Clubs, Rank.Ten))
      } should have message ("Invalid card for this attack")
    }

  }
}