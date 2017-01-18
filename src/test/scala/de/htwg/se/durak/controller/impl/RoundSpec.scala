package de.htwg.se.durak.controller.impl

import org.scalatest.Matchers
import org.scalatest.WordSpec

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.round._
import de.htwg.se.util.Observer
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Player
import com.google.inject.Guice
import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.model.Player
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Deck

class RoundSpec extends WordSpec with Matchers {
  val injector = Guice.createInjector(new DurakModule())
  val cardFactory = injector.getInstance(classOf[CardFactory])
  val playerFactory = injector.getInstance(classOf[PlayerFactory])
  val attackFactory = injector.getInstance(classOf[AttackFactory])
  val deckFactory = injector.getInstance(classOf[DeckFactory])

  val card1 = cardFactory.create("Hearts", "Jack", true)
  val card2 = cardFactory.create(Suit.Clubs.toString, Rank.Seven.toString)
  val card3 = cardFactory.create(Suit.Diamonds.toString, Rank.Ten.toString)
  val card4 = cardFactory.create(Suit.Diamonds.toString, Rank.Queen.toString)
  val card5 = cardFactory.create(Suit.Hearts.toString, Rank.Seven.toString, true)
  val card6 = cardFactory.create(Suit.Spades.toString, Rank.Ace.toString)

  val card7 = cardFactory.create(Suit.Hearts.toString, Rank.Queen.toString, true)
  val card8 = cardFactory.create(Suit.Clubs.toString, Rank.Jack.toString)
  val card9 = cardFactory.create(Suit.Hearts.toString, Rank.Eight.toString, true)
  val card10 = cardFactory.create(Suit.Diamonds.toString, Rank.Ace.toString)
  val card11 = cardFactory.create(Suit.Hearts.toString, Rank.Ten.toString, true)
  val card12 = cardFactory.create(Suit.Diamonds.toString, Rank.Jack.toString)

  val card13 = cardFactory.create(Suit.Clubs.toString, Rank.Seven.toString)
  val card14 = cardFactory.create(Suit.Diamonds.toString, Rank.Queen.toString)
  val card15 = cardFactory.create(Suit.Spades.toString, Rank.Ace.toString)
  val card16 = cardFactory.create(Suit.Hearts.toString, Rank.Nine.toString, true)
  val card17 = cardFactory.create(Suit.Spades.toString, Rank.Nine.toString)
  val card18 = cardFactory.create(Suit.Diamonds.toString, Rank.Nine.toString)

  val card19 = cardFactory.create(Suit.Hearts.toString, Rank.King.toString, true)
  val card20 = cardFactory.create(Suit.Clubs.toString, Rank.Queen.toString)
  val card21 = cardFactory.create(Suit.Hearts.toString, Rank.Ace.toString, true)
  val card22 = cardFactory.create(Suit.Diamonds.toString, Rank.Eight.toString)
  val card23 = cardFactory.create(Suit.Spades.toString, Rank.Eight.toString)
  val card24 = cardFactory.create(Suit.Clubs.toString, Rank.Eight.toString)

  val cards = List[Card](card13, card14, card15, card16, card17, card18, card19, card20, card22, card23, card24, card21)

  var player1 = playerFactory.create("Jakob", 0, List[Card](card1, card2, card3, card4, card5, card6)).setStatus(PlayerStatus.Attacker).setTurn(true)
  var player2 = playerFactory.create("Kathrin", 1, List[Card](card7, card8, card9, card10, card11, card12)).setStatus(PlayerStatus.Defender)
  var round = new Round(deckFactory.create(Rank.Seven), List[Player](player1, player2), Suit.Hearts, Vector())

  def resetRound() = {
    player1 = playerFactory.create("Jakob", 0, List[Card](card1, card2, card3, card4, card5, card6)).setStatus(PlayerStatus.Attacker).setTurn(true)
    player2 = playerFactory.create("Kathrin", 1, List[Card](card7, card8, card9, card10, card11, card12)).setStatus(PlayerStatus.Defender)
    round = new Round(deckFactory.create(Rank.Seven), List[Player](player1, player2), Suit.Hearts, Vector())
  }

  "A Round with 2 players" when {
    "no player misses a turn" should {
      "start in the FirstAttackersFirstTurn state" in {
        round.state shouldBe a[FirstAttackersFirstTurn]
        round.statusLine should be("Start of a new Round. It is " + round.getCurrentPlayer.name + "'s turn")
      }

      "return the current player" in {
        round.getCurrentPlayer() should be(player1)
      }

      "return the players whose turn it is after the current player" in {
        round.getNextCurrentPlayer() should be(player2)
      }

      "return null if asking for the second attacker" in {
        round.getSecondAttacker should be(null)
      }

      "not let the attacker miss a turn at the beginning of the round" in {
        round.endTurn
        round.statusLine should be("You must play a card at the beginning of a round")
      }

      "let the attacker put down a card" in {
        round.playCard(card2) //Attacker
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card2)

        player1 = round.getCurrentPlayer()
        player1.cards.contains(card2) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(attackFactory.create(card2)) should be(true)
      }

      "return a formatted string of the attacks on the table" in {
        round.getAttacksOnTableString should be("\n0: " + round.attacks(0).toString)
      }

      "return the attack at a given index" in {
        round.getAttackByIndex(0) should be(attackFactory.create(card2))
      }

      "check if the card the attacker plays is valid" in {
        round.playCard(card1)
        round.statusLine should be("The rank of this card is not on the table yet")

        round.playCard(card13)
        round.statusLine should be("Player does not have this card in his/her hand")

        round.playCard("hello", "world", "")
        round.statusLine should be("False input!")

        round.playCard("hello", "world", "0")
        round.statusLine should be("False input!")
      }

      "continue with the attacking player playing cards until he ends his turn" in {
        round.playCard(card5) //Attacker
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card5)

        player1 = round.getCurrentPlayer()
        player1.name should be("Jakob")
        player1.cards.contains(card5) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(attackFactory.create(card5)) should be(true)

        round.endTurn //Attacker
        round.statusLine should be("Jakob's round is finished. It is Kathrin's turn")

        player1 = round.players(0)
        player2 = round.players(1)
        player1.hisTurn should be(false)
        player2.hisTurn should be(true)
      }

      "now be in DefendersTurn state" in {
        round.state shouldBe a[DefendersTurn]
      }

      "check if the card the defender plays is valid" in {
        round.playCard("diamonds", "ace", "1")
        round.statusLine should be("This card is not valid for this attack")
        round.getCurrentPlayer.cards.contains(card10) should be(true)
      }

      "continue with defending player defending that attack" in {
        round.playCard(card8, attackFactory.create(card2)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card8)

        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card8) should be(false)
        player2.hisTurn should be(true)
        round.attacks.contains(attackFactory.create(card2, card8)) should be(true)
      }

      "check if the attack the defender wants to finish is on the table" in {
        round.playCard(card10, attackFactory.create(card1))
        round.statusLine should be("This attack does not exist")
      }

      "return if all attacks are defended" in {
        round.allAttacksDefended should be(false)

        round.playCard(card9, attackFactory.create(card5)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card9)

        round.attacks.contains(attackFactory.create(card5, card9)) should be(true)
        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card9) should be(false)

        round.allAttacksDefended should be(true)
      }

      /*"not update player status if round is not finished yet" in {
        val players = round.players
        round.setupForNextRound
        round.players should be(players)
      }*/

      "continue the defenders round until the defender ends his round" in {
        round.endTurn //Defender
        round.statusLine should be("Kathrin's round is finished. It is Jakob's turn")

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
        round.playCard(card6) //Attacker
        round.statusLine should be("The rank of this card is not on the table yet")

        round.playCard(card13)
        round.statusLine should be("Player does not have this card in his/her hand")

        round.playCard(card1) //Attacker
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card1)

        round.endTurn //Attacker
        round.statusLine should be("Jakob's round is finished. It is Kathrin's turn")

        round.playCard(card7, attackFactory.create(card1)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card7)

        round.endTurn //Defender
        round.statusLine should be("Kathrin's round is finished. It is Jakob's turn")

        round.playCard(card4) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card4)

        round.endTurn //Attacker
        round.statusLine should be("Jakob's round is finished. It is Kathrin's turn")

        println(round.getCurrentPlayer.cards)
        round.playCard(card10, attackFactory.create(card4)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card10)

        round.endTurn //Defender
        round.statusLine should be("Kathrin's round is finished. It is Jakob's turn")

        round.playCard(card6) //Attacker
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card6)

        round.endTurn //Attacker
        round.statusLine should be("Jakob's round is finished. It is Kathrin's turn")

        round.playCard(card11, attackFactory.create(card6)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card11)

        round.endTurn //Defender
        round.statusLine should be("Kathrin's round is finished. It is Jakob's turn")

        round.playCard(card3) //Attacker
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card3)

        round.endTurn //Attacker
        round.statusLine should be("Jakob's round is finished. It is Kathrin's turn")

        round.playCard(card12, attackFactory.create(card3)) //Defender
        round.statusLine should be(round.getCurrentPlayer.name + " played the card " + card12)

        round.endTurn //Defender
        round.statusLine should be("The round is finished and the defender has won the round. Start a new round by entering s")
      }

      "change to state RoundFinished when maximum number of attacks are reached" in {
        round.state shouldBe a[RoundFinished]
        round.defenderWon should be(true)
      }

      /*"set correct player statuses for the next round after the round is finished" in {
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
      }*/

    }

    "the defending player misses a turn" should {
      "end when the defending player misses a turn" in {
        resetRound
        round.playCard(card2)
        round.playCard(card5)
        round.endTurn
        round.playCard(card8, attackFactory.create(card2))
        round.endTurn

        round.state shouldBe a[RoundFinished]

      }

      /*"set correct player statuses for the next round after the round is finished" in {
        round.setupForNextRound
        player1 = round.players.filter(_.name == "Jakob")(0)
        player2 = round.players.filter(_.name == "Kathrin")(0)

        player1.hisTurn should be(true)
        player1.status should be(PlayerStatus.Attacker)

        player2.hisTurn should be(false)
        player2.status should be(PlayerStatus.Defender)
      }*/

      /*"give all the players the correct cards after the round is finished" in {
        player1.cards should be(List[Card](card13, card14, card1, card3, card4, card6))
        player2.cards should be(List[Card](card5, card2, card8, card7, card9, card10, card11, card12))
      }*/
    }

    "the attacking player misses a turn" should {
      "end when the attacking player misses a turn" in {
        resetRound
        round.playCard(card2)
        round.endTurn
        round.playCard(card8, attackFactory.create(card2))
        round.endTurn
        round.endTurn

        round.state shouldBe a[RoundFinished]
      }

      /*"set correct player statuses for the next round after the round is finished" in {
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
      }*/
    }
  }

  val card1a = cardFactory.create(Suit.Hearts.toString, Rank.Seven.toString, true)
  val card2a = cardFactory.create(Suit.Hearts.toString, Rank.Jack.toString, true)
  val card3a = cardFactory.create(Suit.Diamonds.toString, Rank.Ten.toString)
  val card4a = cardFactory.create(Suit.Clubs.toString, Rank.King.toString)
  val card5a = cardFactory.create(Suit.Diamonds.toString, Rank.King.toString)
  val card6a = cardFactory.create(Suit.Spades.toString, Rank.Ten.toString)

  val card7a = cardFactory.create(Suit.Hearts.toString, Rank.Eight.toString, true)
  val card8a = cardFactory.create(Suit.Clubs.toString, Rank.Jack.toString)
  val card9a = cardFactory.create(Suit.Hearts.toString, Rank.Queen.toString, true)
  val card10a = cardFactory.create(Suit.Diamonds.toString, Rank.Ace.toString)
  val card11a = cardFactory.create(Suit.Hearts.toString, Rank.Ten.toString, true)
  val card12a = cardFactory.create(Suit.Diamonds.toString, Rank.Jack.toString)

  val card13a = cardFactory.create(Suit.Clubs.toString, Rank.Seven.toString)
  val card14a = cardFactory.create(Suit.Diamonds.toString, Rank.Queen.toString)
  val card15a = cardFactory.create(Suit.Spades.toString, Rank.Ace.toString)
  val card16a = cardFactory.create(Suit.Hearts.toString, Rank.Nine.toString, true)
  val card17a = cardFactory.create(Suit.Spades.toString, Rank.Nine.toString)
  val card18a = cardFactory.create(Suit.Diamonds.toString, Rank.Nine.toString)

  val card19a = cardFactory.create(Suit.Hearts.toString, Rank.King.toString, true)
  val card20a = cardFactory.create(Suit.Clubs.toString, Rank.Queen.toString)
  val card21a = cardFactory.create(Suit.Hearts.toString, Rank.Ace.toString, true)
  val card22a = cardFactory.create(Suit.Diamonds.toString, Rank.Eight.toString)
  val card23a = cardFactory.create(Suit.Spades.toString, Rank.Eight.toString)
  val card24a = cardFactory.create(Suit.Clubs.toString, Rank.Eight.toString)

  var player1a = playerFactory.create("Jakob", 0, List(card1a, card2a, card3a, card4a, card5a, card6a)).setStatus(PlayerStatus.Attacker).setTurn(true)
  var player2a = playerFactory.create("Kathrin", 1, List(card7a, card8a, card9a, card10a, card11a, card12a)).setStatus(PlayerStatus.Defender)
  var player3a = playerFactory.create("David", 2, List(card13a, card14a, card15a, card16a, card17a, card18a)).setStatus(PlayerStatus.Attacker)
  var player4a = playerFactory.create("Thomas", 3, List(card19a, card20a, card21a, card22a, card23a, card24a))

  var rounda = new Round(deckFactory.create(Rank.Seven), List(player1a, player2a, player3a, player4a), Suit.Hearts, Vector())

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
        rounda.playCard("hearts", "seven", "") //1. Attacker
        rounda.attacks.contains(attackFactory.create(card1a)) should be(true)
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
        rounda.playCard(card7a, attackFactory.create(card1a)) //Defender

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

        rounda.playCard(card8a, attackFactory.create(card13a)) //Defender
        rounda.endTurn //Defender

        rounda.players.filter(_.number == 0)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 1)(0).hisTurn should be(false)
        rounda.players.filter(_.number == 2)(0).hisTurn should be(true)
        rounda.players.filter(_.number == 3)(0).hisTurn should be(false)

        rounda.state shouldBe a[SecondAttackersTurn]
        rounda.getCurrentPlayer should be(playerFactory.create("David", 2, List(card14a, card15a, card16a, card17a, card18a)).setStatus(PlayerStatus.Attacker).setTurn(true))
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
        rounda.playCard(card9a, attackFactory.create(card2a)) //Defender
        rounda.endTurn //Defender

        rounda.playCard(card14a) //2. Attacker
        rounda.endTurn //2. Attacker

        rounda.endTurn // 1. Attacker

        rounda.playCard(card10a, attackFactory.create(card14a)) //Defender
        rounda.endTurn //Defender

        rounda.playCard(card15a) //2. Attacker
        rounda.endTurn //2. Attacker

        rounda.endTurn //1. Attacker

        rounda.playCard(card11a, attackFactory.create(card15a)) //Defender
        rounda.endTurn //Defender

        rounda.endTurn //2. Attacker

        rounda.playCard(card3a) //1. Attacker
        rounda.playCard(card6a) //1. Attacker

        rounda.state shouldBe a[DefendersTurn]

        val cardA = cardFactory.create(Suit.Diamonds.toString, Rank.Seven.toString)
        val playerA = playerFactory.create("Kathrin", 0, List[Card](cardA, cardA, cardA, cardA, cardA, cardA)).setStatus(PlayerStatus.Attacker).setTurn(true)
        val playerB = playerFactory.create("Kathrin", 1, List[Card](card7, card8, card9, card10, card11, card12)).setStatus(PlayerStatus.Defender)

        val newRound = new Round(deckFactory.create(Rank.Seven), List[Player](playerA, playerB), Suit.Hearts, Vector())

        newRound.playCard(cardA)
        newRound.playCard(cardA)
        newRound.playCard(cardA)
        newRound.playCard(cardA)
        newRound.playCard(cardA)
        newRound.playCard(cardA)
        newRound.playCard(cardA)

        newRound.state shouldBe a[DefendersTurn]
      }

      "end when the maximum number of attacks is reached and all attacks are defended" in {
        rounda.playCard(card12a, attackFactory.create(card3a))
        rounda.endTurn

        rounda.state shouldBe a[RoundFinished]
      }

      /*"set correct player statuses for the next round after the round is finished" in {
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
      }*/
    }

    "both attackers miss a turn in the same round" should {
      "end if both attackers miss a turn in the same round" in {
        rounda = new Round(deckFactory.create(Rank.Seven), List(player1a, player2a, player3a, player4a), Suit.Hearts, Vector())
        rounda.playCard(card1a) //1. Attacker
        rounda.endTurn //1. Attacker
        rounda.playCard(card7a, attackFactory.create(card1a)) //Defender
        rounda.endTurn //Defender
        rounda.endTurn // 2. Attacker
        rounda.endTurn // 1. Attacker

        rounda.state shouldBe a[RoundFinished]
      }

      /*"set correct player statuses for the next round after the round is finished" in {
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
      }*/
    }

    "the defender misses a turn" should {
      "end when the defender misses a turn" in {
        rounda = new Round(deckFactory.create(Rank.Seven), List(player1a, player2a, player3a, player4a), Suit.Hearts, Vector())
        rounda.playCard(card1a) //1. Attacker
        rounda.endTurn //1. Attacker

        rounda.endTurn //Defender
        rounda.state shouldBe a[RoundFinished]
      }

      "return a message if a player trys to play a card after the round is finished" in {
        rounda.playCard(card13a)
        rounda.statusLine should be("The round is finished. Start a new round by entering s")

      }

      "return a message if a player trys to end a turn after the round is finished" in {
        rounda.endTurn
        rounda.statusLine should be("The round is finished. Start a new round by entering s")
      }

      /*"set correct player statuses for the next round after the round is finished" in {
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
      }*/
    }
  }
}