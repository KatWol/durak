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
  val card4 = Card(Suit.Diamonds, Rank.Queen)
  val card5 = Card(Suit.Hearts, Rank.Seven, true)
  val card6 = Card(Suit.Spades, Rank.Ace)

  val card7 = Card(Suit.Hearts, Rank.Queen, true)
  val card8 = Card(Suit.Clubs, Rank.Jack)
  val card9 = Card(Suit.Hearts, Rank.Eight, true)
  val card10 = Card(Suit.Diamonds, Rank.Ace)
  val card11 = Card(Suit.Hearts, Rank.Ten, true)
  val card12 = Card(Suit.Diamonds, Rank.Jack)

  var player1 = Player("Jakob", 1, List[Card](card1, card2, card3, card4, card5, card6), PlayerStatus.Attacker, true)
  var player2 = Player("Kathrin", 2, List[Card](card7, card8, card9, card10, card11, card12), PlayerStatus.Defender)
  var round = new Round(new Deck(Rank.Seven), List[Player](player1, player2))

  def resetRound() = {
    player1 = Player("Jakob", 1, List[Card](card1, card2, card3, card4, card5, card6), PlayerStatus.Attacker, true)
    player2 = Player("Kathrin", 2, List[Card](card7, card8, card9, card10, card11, card12), PlayerStatus.Defender)
    round = new Round(new Deck(Rank.Seven), List(player1, player2))
  }

  "A Round with 2 Players" when {
    "no player misses a turn" should {
      "return the current player" in {
        round.getCurrentPlayer() should be(player1)
      }

      "return the players whose turn it is after the current player" in {
        round.getNextCurrentPlayer() should be(player2)
      }

      "start with the current player starting an attack" in {
        round.startAttack(card2)
        player1 = round.getCurrentPlayer()
        player1.cards.contains(card2) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(Attack(card2)) should be(true)
      }

      "throw an exception if the attacker defends an attack" in {
        the[IllegalArgumentException] thrownBy {
          round.defendAttack(card5, Attack(card2))
        } should have message ("This player is not allowed to perform this action")
      }

      "check if the card the attacker plays is valid" in {
        the[IllegalArgumentException] thrownBy {
          round.startAttack(card1)
        } should have message ("The rank of this card is not on the table yet")
      }

      "continue with the attacking player playing cards until he ends his turn" in {
        round.startAttack(card5)
        player1 = round.getCurrentPlayer()
        player1.name should be("Jakob")
        player1.cards.contains(card5) should be(false)
        player1.hisTurn should be(true)
        round.attacks.contains(Attack(card5)) should be(true)

        round.endTurn()
        player1 = round.players(0)
        player2 = round.players(1)
        player1.hisTurn should be(false)
        player2.hisTurn should be(true)
      }

      var attack1: Attack = null

      "return an attack by index" in {
        attack1 = round.getAttackByIndex(1);
        attack1 should be(Attack(card5))

        a[IndexOutOfBoundsException] should be thrownBy {
          round.getAttackByIndex(3)
        }
      }

      "continue with defending player defending that attack" in {
        round.defendAttack(card8, Attack(card2))
        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card8) should be(false)
        player2.hisTurn should be(true)
        round.attacks.contains(Attack(card2, card8)) should be(true)

        round.defendersTurnOver should be(false)
      }

      "check if the card the defender plays is valid" in {
        an[IllegalArgumentException] should be thrownBy {
          round.defendAttack(card10, round.getAttackByIndex(1))
        }
      }

      "check if the attack the defender wants to finish is on the table" in {
        the[IllegalArgumentException] thrownBy {
          round.defendAttack(card10, Attack(card1))
        } should have message ("This attack does not exist")
      }

      "return if all attacks are defended" in {
        round.allAttacksDefended should be(false)

        round.defendAttack(card9, round.getAttackByIndex(1))
        round.attacks.contains(Attack(card5, card9)) should be(true)
        player2 = round.getCurrentPlayer()
        player2.name should be("Kathrin")
        player2.cards.contains(card9) should be(false)

        round.allAttacksDefended should be(true)
      }

      "check whether the defenders turn is over" in {
        round.defendersTurnOver should be(true)
      }

      "continue the defenders round until the defender ends his round" in {
        round.endTurn()
        player1 = round.getCurrentPlayer()
        player1.name should be("Jakob")
        player2 = round.players(1)
        player2.name should be("Kathrin")
        player2.hisTurn should be(false)
      }

      "check whether the defender has won" in {
        round.hasDefenderWon should be(false)
      }

      "end when the maximum number of attacks are reached and all attacks are defended" in {
        round.startAttack(card1)
        round.endTurn()
        round.defendAttack(card7, round.getAttackByIndex(1))
        round.endTurn()
        round.startAttack(card4)
        round.endTurn()
        round.defendAttack(card10, round.getAttackByIndex(1))
        round.endTurn()
        round.startAttack(card6)
        round.endTurn()
        round.defendAttack(card11, round.getAttackByIndex(1))
        round.endTurn()
        round.startAttack(card3)
        round.endTurn()
        round.defendAttack(card12, round.getAttackByIndex(1))
        round.endTurn()

        round.defendersTurnOver should be(true)
        round.isRoundFinished should be(true)
        round.hasDefenderWon should be(true)
      }

      "set correct player statuses for the next round after the round is finished" in {
        val newPlayers = round.updatePlayerStatusForNextRound
        player1 = newPlayers(0)
        player1.name should be("Jakob")
        player1.status should be(PlayerStatus.Defender)
        player1.hisTurn should be(false)

        player2 = newPlayers(1)
        player2.name should be("Kathrin")
        player2.status should be(PlayerStatus.Attacker)
        player2.hisTurn should be(true)
      }

      "give all the players the correct cards after the round is finished" in {

      }
    }

    //    "the defending player misses a turn" should {
    //       "end when the defending player misses a turn" in {
    //        
    //      }
    //    }
    //    
    //    "the attacking players miss a turn" should {
    //       "end if both attackers miss a turn in the same round" in {
    //        
    //      }
    //    }

  }

  //  "A Round with  Players" should {
  //    "return the current player" in {
  //      
  //    }
  //    
  //    "return the players whose turn it is after the current player" in {
  //      
  //    }
  //    
  //    "start with the current player starting an attack until the player ends the attack" in {
  //      
  //    }
  //    
  //    "continue with defending player defending that attack" in {
  //      
  //    }
  //    
  //    "continue with the defending round until that player ends the rounds or all attacks are defended" in {
  //      
  //    }
  //    
  //    "end when the maximum number of attacks are reached and all attacks are defended" in {
  //      
  //    }
  //    
  //    "end when the defending player misses a turn" in {
  //      
  //    }
  //    
  //    "end when the defending player is not able to defend all attacks" in {
  //      
  //    }
  //    
  //    "end if both attackers miss a turn in the same round" in {
  //      
  //    }
  //    
  //    "set correct player statuses for the next round after the round is finished" in {
  //      
  //    }
  //    
  //    "give all the players the correct cards after the round is finished" in {
  //      
  //    }
  //  }

  //  "A Round" should {
  //    "return the current Player" in {
  //      round.getCurrentPlayer() should be(player1)
  //    }
  //
  //    "start with an attack and only allow attacks according to the rules" in {
  //      round.startAttack(card2, player1)
  //      round.attacks should contain(Attack(card2))
  //      round.getCurrentPlayer().cards should not contain (card2)
  //
  //      the[IllegalArgumentException] thrownBy {
  //        round.startAttack(card3, player1)
  //      } should have message ("The rank of this card is not on the table yet")
  //
  //      the[IllegalArgumentException] thrownBy {
  //        round.startAttack(card4, player2)
  //      } should have message ("The player is currently not allowed to attack")
  //    }
  //
  //    "check if a value is already on the table" in {
  //      round.attacks = List(Attack(card2, card4))
  //      round.isRankOnTable(Rank.Seven) should be(true)
  //      round.isRankOnTable(Rank.Queen) should be(true)
  //      round.isRankOnTable(Rank.Six) should be(false)
  //
  //      round.attacks = Attack(card3) :: round.attacks
  //      round.isRankOnTable(Rank.Two) should be(false)
  //    }
  //
  //    "continue with the defending player defending the attack and only allow defences according to the rules" in {
  //      round.startAttack(card2, player1)
  //
  //      the[IllegalArgumentException] thrownBy {
  //        round.defendAttack(card4, player2, Attack(card3))
  //      } should have message ("This attack does not exist")
  //
  //      the[IllegalArgumentException] thrownBy {
  //        round.defendAttack(card1, player1, Attack(card2))
  //      } should have message ("The player is currently not allowed to defend")
  //
  //      the[IllegalArgumentException] thrownBy {
  //        round.defendAttack(card6, player2, Attack(card2))
  //      } should have message ("Invalid card for this attack")
  //
  //      round.defendAttack(card4, player2, Attack(card2))
  //      round.attacks should contain(Attack(card2, card4))
  //    }
  //    
  //    "return the player whos turn it is next" in {
  //      round.getNextCurrentPlayer() should be (player2)
  //    }
  //    
  //    "update whos turn it is after a player is finished with his turn" in {
  //      round.updateTurn() 
  //      round.getCurrentPlayer() should be (player2.changeTurn())
  //      round.players(1) should be (player1.changeTurn())
  //    }
  //    
  //    "return an attack at the specified index" in {
  //       round.startAttack(card2, player1)
  //       round.getAttackByIndex(1) should be (Attack(card2))
  //       
  //       the[IndexOutOfBoundsException] thrownBy {
  //         round.getAttackByIndex(2)
  //       } should have message ("Not enough attacks on the table")
  //    }
  //    
  //    "check if a round is finished when player missed turn" in {
  //      round.missTurn() should be (false)
  //      round.turnMissed should be (true)
  //      round.updateTurn()
  //      round.missTurn() should be (true)
  //    }
  //  }

}