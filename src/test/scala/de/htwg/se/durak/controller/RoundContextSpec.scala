package de.htwg.se.durak.controller

import org.scalatest.Matchers
import org.scalatest.WordSpec

import de.htwg.se.durak.model._

class RoundContextSpec extends WordSpec with Matchers {
  val card1 = Card(Suit.Hearts, Rank.Jack, true)
  val card2 = Card(Suit.Clubs, Rank.Seven)
  val card3 = Card(Suit.Diamonds, Rank.Ten)
  val card4 = Card(Suit.Diamonds, Rank.Queen)
  val card5 = Card(Suit.Hearts, Rank.Seven, true)
  val card6 = Card(Suit.Spades, Rank.Ace)

  val card7 = Card(Suit.Hearts, Rank.Queen, true)
  val card8 = Card(Suit.Clubs, Rank.Jack)
  val card9 = Card(Suit.Hearts, Rank.Eight, true)
  val card10 = Card(Suit.Diamonds, Rank.Ace)
  val card11 = Card(Suit.Hearts, Rank.Ten, true)
  val card12 = Card(Suit.Diamonds, Rank.Jack)

  val card13 = Card(Suit.Clubs, Rank.Seven)
  val card14 = Card(Suit.Diamonds, Rank.Queen)
  val card15 = Card(Suit.Spades, Rank.Ace)
  val card16 = Card(Suit.Hearts, Rank.Nine, true)
  val card17 = Card(Suit.Spades, Rank.Nine)
  val card18 = Card(Suit.Diamonds, Rank.Nine)

  val card19 = Card(Suit.Hearts, Rank.King, true)
  val card20 = Card(Suit.Clubs, Rank.Queen)
  val card21 = Card(Suit.Hearts, Rank.Ace, true)
  val card22 = Card(Suit.Diamonds, Rank.Eight)
  val card23 = Card(Suit.Spades, Rank.Eight)
  val card24 = Card(Suit.Clubs, Rank.Eight)

  val cards = List[Card](card13, card14, card15, card16, card17, card18, card19, card20, card21, card22, card23, card24)

  var player1 = Player("Jakob", 0, List[Card](card1, card2, card3, card4, card5, card6), PlayerStatus.Attacker, true)
  var player2 = Player("Kathrin", 1, List[Card](card7, card8, card9, card10, card11, card12), PlayerStatus.Defender)
  var round = new RoundContext(Deck(cards), List[Player](player1, player2))

  def resetRound() = {
    player1 = Player("Jakob", 0, List[Card](card1, card2, card3, card4, card5, card6), PlayerStatus.Attacker, true)
    player2 = Player("Kathrin", 1, List[Card](card7, card8, card9, card10, card11, card12), PlayerStatus.Defender)
    round = new RoundContext(Deck(cards), List[Player](player1, player2))
  }

  "A Round with 2 players" when {
    "no player misses a turn" should {
      "start in the FirstAttackersFirstTurn state" in {
        round.state shouldBe a[FirstAttackersFirstTurn]
      }

      "return the current player" in {
        round.getCurrentPlayer() should be(player1)
      }

      "return the players whose turn it is after the current player" in {
        round.getNextCurrentPlayer() should be(player2)
      }

      "not let the attacker miss a turn at the beginning of the round" in {
        the[IllegalArgumentException] thrownBy {
          round.endTurn
        } should have message ("Must play a card at the beginning of a round")
      }

      "let the attacker put down a card" in {
        round.playCard(card2)
        player1 = round.getCurrentPlayer()
        player1.cards.contains(card2) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(Attack(card2)) should be(true)
      }

      "check if the card the attacker plays is valid" in {
        the[IllegalArgumentException] thrownBy {
          round.playCard(card1)
        } should have message ("The rank of this card is not on the table yet")
      }

      "continue with the attacking player playing cards until he ends his turn" in {
        round.playCard(card5)
        player1 = round.getCurrentPlayer()
        player1.name should be("Jakob")
        player1.cards.contains(card5) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(Attack(card5)) should be(true)

        round.endTurn
        player1 = round.players(0)
        player2 = round.players(1)
        player1.hisTurn should be(false)
        player2.hisTurn should be(true)
      }

      "now be in DefendersTurn state" in {
        round.state shouldBe a[DefendersTurn]
      }

      "continue with defending player defending that attack" in {
        round.playCard(card8, Attack(card2))
        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card8) should be(false)
        player2.hisTurn should be(true)
        round.attacks.contains(Attack(card2, card8)) should be(true)
      }

      "check if the card the defender plays is valid" in {
        an[IllegalArgumentException] should be thrownBy {
          round.playCard(card10, Attack(card2))
        }
      }

      "check if the attack the defender wants to finish is on the table" in {
        the[IllegalArgumentException] thrownBy {
          round.playCard(card10, Attack(card1))
        } should have message ("This attack does not exist")
      }

      "return if all attacks are defended" in {
        round.allAttacksDefended should be(false)

        round.playCard(card9, Attack(card5))
        round.attacks.contains(Attack(card5, card9)) should be(true)
        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card9) should be(false)

        round.allAttacksDefended should be(true)
      }

      "not update player status if round is not finished yet" in {
        val players = round.players
        round.setupForNextRound
        round.players should be(players)
      }

      "continue the defenders round until the defender ends his round" in {
        round.endTurn
        player1 = round.getCurrentPlayer
        player1.name should be("Jakob")
        player2 = round.players(1)
        player2.name should be("Kathrin")
        player2.hisTurn should be(false)
      }

      "continue with the round being in state FirstAttackersTurn state" in {
        round.state shouldBe a[FirstAttackersTurn]
      }

      "end when the maximum number of attacks are reached and all attacks are defended" in {
        round.playCard(card1)
        round.endTurn
        round.playCard(card7, Attack(card1))
        round.endTurn
        round.playCard(card4)
        round.endTurn
        round.playCard(card10, Attack(card4))
        round.endTurn
        round.playCard(card6)
        round.endTurn
        round.playCard(card11, Attack(card6))
        round.endTurn
        round.playCard(card3)
        round.endTurn
        round.playCard(card12, Attack(card3))
        round.endTurn
      }

      "change to state RoundFinished when maximum number of attacks are reached!" in {
        round.state shouldBe a[RoundFinished]
      }

      "set correct player statuses for the next round after the round is finished" in {
        round.setupForNextRound
        player1 = round.players(0)
        player1.name should be("Jakob")
        player1.status should be(PlayerStatus.Defender)
        player1.hisTurn should be(false)

        player2 = round.players(1)
        player2.name should be("Kathrin")
        player2.status should be(PlayerStatus.Attacker)
        player2.hisTurn should be(true)
      }

      "give all the players the correct cards after the round is finished" in {
        round.players(0).cards should be(List[Card](card13, card14, card15, card16, card17, card18))
        round.players(1).cards should be(List[Card](card19, card20, card21, card22, card23, card24))
        round.deck should be(Deck(List[Card]()))
      }

    }

    "the defending player misses a turn" should {
      "end when the defending player misses a turn" in {
        resetRound
        round.playCard(card2)
        round.playCard(card5)
        round.endTurn
        round.playCard(card8, Attack(card2))
        round.endTurn

        round.state shouldBe a[RoundFinished]

      }

      "set correct player statuses for the next round after the round is finished" in {
        round.setupForNextRound
        player1 = round.players.filter(_.name == "Jakob")(0)
        player2 = round.players.filter(_.name == "Kathrin")(0)

        player1.hisTurn should be(true)
        player1.status should be(PlayerStatus.Attacker)

        player2.hisTurn should be(false)
        player2.status should be(PlayerStatus.Defender)
      }

      "give all the players the correct cards after the round is finished" in {
        player1.cards should be(List[Card](card13, card14, card1, card3, card4, card6))
        player2.cards should be(List[Card](card5, card2, card8, card7, card9, card10, card11, card12))
      }
    }

    "the attacking player misses a turn" should {
      "end when the attacking player misses a turn" in {
        resetRound
        round.playCard(card2)
        round.endTurn
        round.playCard(card8, Attack(card2))
        round.endTurn
        round.endTurn

        round.state shouldBe a[RoundFinished]
      }

      "set correct player statuses for the next round after the round is finished" in {
        round.setupForNextRound
        player1 = round.players.filter(_.name == "Jakob")(0)
        player2 = round.players.filter(_.name == "Kathrin")(0)

        player1.hisTurn should be(false)
        player1.status should be(PlayerStatus.Defender)

        player2.hisTurn should be(true)
        player2.status should be(PlayerStatus.Attacker)
      }

      "give all the players the correct cards after the round is finished" in {
        player1.cards should be(List[Card](card13, card1, card3, card4, card5, card6))
        player2.cards should be(List[Card](card14, card7, card9, card10, card11, card12))
      }
    }
  }

  val card1a = Card(Suit.Hearts, Rank.Seven, true)
  val card2a = Card(Suit.Hearts, Rank.Jack, true)
  val card3a = Card(Suit.Diamonds, Rank.Ten)
  val card4a = Card(Suit.Clubs, Rank.King)
  val card5a = Card(Suit.Diamonds, Rank.King)
  val card6a = Card(Suit.Spades, Rank.Ten)

  val card7a = Card(Suit.Hearts, Rank.Eight, true)
  val card8a = Card(Suit.Clubs, Rank.Jack)
  val card9a = Card(Suit.Hearts, Rank.Queen, true)
  val card10a = Card(Suit.Diamonds, Rank.Ace)
  val card11a = Card(Suit.Hearts, Rank.Ten, true)
  val card12a = Card(Suit.Diamonds, Rank.Jack)

  val card13a = Card(Suit.Clubs, Rank.Seven)
  val card14a = Card(Suit.Diamonds, Rank.Queen)
  val card15a = Card(Suit.Spades, Rank.Ace)
  val card16a = Card(Suit.Hearts, Rank.Nine, true)
  val card17a = Card(Suit.Spades, Rank.Nine)
  val card18a = Card(Suit.Diamonds, Rank.Nine)

  val card19a = Card(Suit.Hearts, Rank.King, true)
  val card20a = Card(Suit.Clubs, Rank.Queen)
  val card21a = Card(Suit.Hearts, Rank.Ace, true)
  val card22a = Card(Suit.Diamonds, Rank.Eight)
  val card23a = Card(Suit.Spades, Rank.Eight)
  val card24a = Card(Suit.Clubs, Rank.Eight)

  var player1a = Player("Jakob", 0, List(card1a, card2a, card3a, card4a, card5a, card6a), PlayerStatus.Attacker, true)
  var player2a = Player("Kathrin", 1, List(card7a, card8a, card9a, card10a, card11a, card12a), PlayerStatus.Defender, false)
  var player3a = Player("David", 2, List(card13a, card14a, card15a, card16a, card17a, card18a), PlayerStatus.Attacker, false)
  var player4a = Player("Thomas", 3, List(card19a, card20a, card21a, card22a, card23a, card24a))

  var rounda = new RoundContext(Deck(cards), List(player1a, player2a, player3a, player4a))

  "A Round with 4 Players" should {
    "when no player misses a turn that ends the round" should {
      "start in the FirstAttackersTurn state" in {
        rounda.state shouldBe a[FirstAttackersTurn]
      }

      "return the current player" in {
        rounda.getCurrentPlayer should be(player1a)
      }

      "return the players whose turn it is after the current player" in {
        rounda.getNextCurrentPlayer should be(player2a)
      }

      "start with the current player starting an attack until the player ends the attack" in {
        rounda.playCard(card1a) //1. Attacker
        rounda.attacks.contains(Attack(card1a)) should be(true)
        rounda.getCurrentPlayer.cards.contains(card1a) should be(false)

        rounda.endTurn //1. Attacker
        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
      }

      "now be in DefendersTurn state" in {
        rounda.state shouldBe a[DefendersTurn]
      }

      "continue with defending player defending that attack until the player ends his turn" in {
        rounda.playCard(card7a, Attack(card1a)) //Defender

        rounda.endTurn //Defender
        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
      }

      "now be in SecondAttackersTurn state" in {
        rounda.state shouldBe a[SecondAttackersTurn]
      }

      "continue with the second attacker playing cards until the player ends his turn" in {
        rounda.playCard(card13a) //2. Attacker
        rounda.endTurn //2. Attacker
        rounda.players.filter(_.number == 0)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
      }

      "set turnMissed to true only if the second attacker misses a turn" in {
        rounda.endTurn //1. Attacker
        rounda.turnMissed should be(false)

        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)

        rounda.state shouldBe a[DefendersTurn]

        rounda.playCard(card8a, Attack(card13a)) //Defender
        rounda.endTurn //Defender

        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)

        rounda.state shouldBe a[SecondAttackersTurn]
        rounda.getCurrentPlayer should be(Player("David", 2, List(card14a, card15a, card16a, card17a, card18a), PlayerStatus.Attacker, true))
        rounda.endTurn //2. Attacker
        rounda.turnMissed should be(true)

        rounda.state shouldBe a[FirstAttackersTurn]
        rounda.players.filter(_.number == 0)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)

        rounda.playCard(card2a) //1. Attacker
        rounda.endTurn //1. Attacker
        rounda.turnMissed should be(false)
      }

      "end turn if an attacker starts more than the maximum number of attacks" in {
        rounda.playCard(card9a, Attack(card2a)) //Defender
        rounda.endTurn //Defender

        rounda.playCard(card14a) //2. Attacker
        rounda.endTurn //2. Attacker

        rounda.endTurn // 1. Attacker

        rounda.playCard(card10a, Attack(card14a)) //Defender
        rounda.endTurn //Defender

        rounda.playCard(card15a) //2. Attacker
        rounda.endTurn //2. Attacker

        rounda.endTurn //1. Attacker

        rounda.playCard(card11a, Attack(card15a)) //Defender
        rounda.endTurn //Defender

        rounda.endTurn //2. Attacker

        //rounda.getCurrentPlayer should be (Player("Jakob", 0, List(card3a, card4a, card5a, card6a), PlayerStatus.Attacker, true))

        rounda.playCard(card3a) //1. Attacker
        rounda.playCard(card6a) //1. Attacker

        rounda.state shouldBe a[DefendersTurn]
      }

      "end when the maximum number of attacks is reached and all attacks are defended" in {
        rounda.playCard(card12a, Attack(card3a))
        rounda.endTurn

        rounda.state shouldBe a[RoundFinished]
      }

      "set correct player statuses for the next round after the round is finished" in {
        rounda.setupForNextRound

        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 0)(0).status should be(PlayerStatus.Inactive)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 1)(0).status should be(PlayerStatus.Attacker)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).status should be(PlayerStatus.Defender)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).status should be(PlayerStatus.Attacker)
      }

      "give all players the correct cards after a round is finished" in {
        rounda.players.filter(_.number == 0).head.cards should be(List[Card](card13, card14, card15, card4a, card5a, card6a))
        rounda.players.filter(_.number == 1).head.cards should be(List[Card](card19, card20, card21, card22, card23, card24))
        rounda.players.filter(_.number == 2).head.cards should be(List[Card](card16, card17, card18, card16a, card17a, card18a))
        rounda.players.filter(_.number == 3).head.cards should be(List[Card](card19a, card20a, card21a, card22a, card23a, card24a))
      }
    }

    "both attackers miss a turn in the same round" should {
      "end if both attackers miss a turn in the same round" in {
        rounda = new RoundContext(new Deck(cards), List(player1a, player2a, player3a, player4a))
        rounda.playCard(card1a) //1. Attacker
        rounda.endTurn //1. Attacker
        rounda.playCard(card7a, Attack(card1a)) //Defender
        rounda.endTurn //Defender
        rounda.endTurn // 2. Attacker
        rounda.endTurn // 1. Attacker

        rounda.state shouldBe a[RoundFinished]
      }

      "set correct player statuses for the next round after the round is finished" in {
        rounda.setupForNextRound

        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 0)(0).status should be(PlayerStatus.Inactive)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 1)(0).status should be(PlayerStatus.Attacker)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).status should be(PlayerStatus.Defender)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).status should be(PlayerStatus.Attacker)
      }

      "give all players the correct cards after a round is finished" in {
        rounda.players.filter(_.number == 0).head.cards should be(List[Card](card13, card2a, card3a, card4a, card5a, card6a))
        rounda.players.filter(_.number == 1).head.cards should be(List[Card](card14, card8a, card9a, card10a, card11a, card12a))
        rounda.players.filter(_.number == 2).head.cards should be(List[Card](card13a, card14a, card15a, card16a, card17a, card18a))
        rounda.players.filter(_.number == 3).head.cards should be(List[Card](card19a, card20a, card21a, card22a, card23a, card24a))
      }
    }

    "the defender misses a turn" should {
      "end when the defender misses a turn" in {
        rounda = new RoundContext(new Deck(cards), List(player1a, player2a, player3a, player4a))
        rounda.playCard(card1a) //1. Attacker
        rounda.endTurn //1. Attacker

        rounda.endTurn //Defender
        rounda.state shouldBe a[RoundFinished]
      }

      "don't let any player put down a card when in RoundFinished state" in {
        rounda.playCard(card13a)
        rounda.players.filter(_.number == 2).head.cards.contains(card13a) should be(true)
      }

      "don't let any player change the state when in RoundFinished state" in {
        rounda.endTurn

        rounda.state shouldBe a[RoundFinished]
      }

      "set correct player statuses for the next round after the round is finished" in {
        rounda.setupForNextRound
        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 0)(0).status should be(PlayerStatus.Attacker)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).status should be(PlayerStatus.Inactive)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 2)(0).status should be(PlayerStatus.Attacker)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 3)(0).status should be(PlayerStatus.Defender)
      }

      "give all players the correct cards after a round is finished" in {
        rounda.players.filter(_.number == 0).head.cards should be(List[Card](card13, card2a, card3a, card4a, card5a, card6a))
        rounda.players.filter(_.number == 1).head.cards should be(List[Card](card1a, card7a, card8a, card9a, card10a, card11a, card12a))
        rounda.players.filter(_.number == 2).head.cards should be(List[Card](card13a, card14a, card15a, card16a, card17a, card18a))
        rounda.players.filter(_.number == 3).head.cards should be(List[Card](card19a, card20a, card21a, card22a, card23a, card24a))
      }
    }
  }
}