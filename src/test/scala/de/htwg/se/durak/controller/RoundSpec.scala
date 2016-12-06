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
  var round = new Round(new Deck(Rank.Seven), List[Player](player1, player2), activePlayerNumber = 1)

  override def withFixture(test: NoArgTest) = {
    player1 = Player("Max", 1, List[Card](card1, card2, card3), PlayerStatus.Attacker)
    player2 = Player("Erika", 2, List[Card](card4, card5, card6), PlayerStatus.Defender)
    round = new Round(new Deck(Rank.Seven), List[Player](player1, player2), activePlayerNumber = 1)
    test()
  }

  "A Round" should {
    "return the active Player" in {
      round.getActivePlayer() should be(player1)
    }

    "start with an attack and only allow attacks according to the rules" in {
      round.startAttack(card2, player1)
      round.attacks should contain(Attack(card2))
      round.getActivePlayer().cards should not contain (card2)

      the[IllegalArgumentException] thrownBy {
        round.startAttack(card3, player1)
      } should have message ("The rank of this card is not on the table yet")

      the[IllegalArgumentException] thrownBy {
        round.startAttack(card4, player2)
      } should have message ("The player is currently not allowed to attack")
    }

    "check if a value is already on the table" in {
      round.attacks = List(Attack(card2, card4))
      round.isRankOnTable(Rank.Seven) should be(true)
      round.isRankOnTable(Rank.Queen) should be(true)
      round.isRankOnTable(Rank.Six) should be(false)

      round.attacks = Attack(card3) :: round.attacks
      round.isRankOnTable(Rank.Two) should be(false)
    }

    "continue with the defending player defending the attack and only allow defences according to the rules" in {
      round.startAttack(card2, player1)

      the[IllegalArgumentException] thrownBy {
        round.defendAttack(card4, player2, Attack(card3))
      } should have message ("This attack does not exist")

      the[IllegalArgumentException] thrownBy {
        round.defendAttack(card1, player1, Attack(card2))
      } should have message ("This player is currently not allowed to defend")

      the[IllegalArgumentException] thrownBy {
        round.defendAttack(card6, player2, Attack(card2))
      } should have message ("Invalid card for this attack")

      round.defendAttack(card4, player2, Attack(card2))
      round.attacks should contain(Attack(card2, card4))
    }
  }

}