package de.htwg.se.durak.model.impl

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit

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

    "should throw an error if it was previously completed" in {
      the[IllegalArgumentException] thrownBy {
        attack2.defend(Card(Suit.Hearts, Rank.Ten))
      } should have message ("This attack is already completed")
    }

    "should check whether a card is a valid card for the attack" in {
      attack.isValid(Card(Suit.Hearts, Rank.Eight)) should be(true)
      attack.isValid(Card(Suit.Clubs, Rank.Eight)) should be(false)
      attack.isValid(Card(Suit.Clubs, Rank.Eight, true)) should be(true)
      attack.isValid(Card(Suit.Hearts, Rank.Seven)) should be(false)

      attack2.isValid(Card(Suit.Hearts, Rank.Eight)) should be(true)
    }

    "should throw an error if the defending card is invalid" in {
      the[IllegalArgumentException] thrownBy {
        attack.defend(Card(Suit.Clubs, Rank.Ten))
      } should have message ("This card is not valid for this attack")
    }

    "return a list of all the cards in the attack" in {
      attack.getCards should be(List[Card](Card(Suit.Hearts, Rank.Seven)))
      attack2.getCards should be(List[Card](Card(Suit.Hearts, Rank.Seven), Card(Suit.Hearts, Rank.Nine)))
    }

    "have an Xml presentation" in {
      attack.toXml.toString should be("<attack><attackingCard><card><suit>hearts</suit><rank>seven</rank><isTrump>false</isTrump></card></attackingCard><defendingCard>null</defendingCard></attack>")
    }

    "be parsed from its Xml presentation" in {
      Attack.fromXml(<attack><attackingCard><card><suit>hearts</suit><rank>seven</rank><isTrump>false</isTrump></card></attackingCard><defendingCard>null</defendingCard></attack>) should be(attack)
      Attack.fromXml(<attack><attackingCard><card><suit>hearts</suit><rank>seven</rank><isTrump>false</isTrump></card></attackingCard><defendingCard><card><suit>hearts</suit><rank>nine</rank><isTrump>false</isTrump></card></defendingCard></attack>) should be(attack2)
    }

  }
}