package de.htwg.se.durak.controller.impl

import org.scalatest.Matchers
import org.scalatest.WordSpec

import de.htwg.se.durak.aview.tui.Tui
import de.htwg.se.durak.controller.impl.game.GameNotFinished
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.impl.Card
import de.htwg.se.util.Observer
import de.htwg.se.durak.controller.impl.round.RoundFinished
import de.htwg.se.durak.controller.impl.game.GameFinished
import com.google.inject.Guice
import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.model.PlayerFactory

class GameRoundSpec extends WordSpec with Matchers {
  def getInactivePlayer(game: GameRound) = game.round.players.filter(_.status == PlayerStatus.Inactive).head

  "A GameRound" should {
    var game = new GameRound(List[String]("Kathrin", "Jakob", "David", "Thomas"), Rank.Eight, true)
    "add and remove subscribers" in {
      val dummyObserver = new DummyObserver("1")
      game.add(dummyObserver)
      game.subscribers.contains(dummyObserver) should be(true)
      val secondDummyObserver = new DummyObserver("2")
      val thirdDummyObserver = new DummyObserver("3")
      game.add(Vector(secondDummyObserver, thirdDummyObserver))
      game.subscribers.contains(secondDummyObserver) should be(true)
      game.subscribers.contains(thirdDummyObserver) should be(true)
      game.remove(dummyObserver)
      game.subscribers.contains(dummyObserver) should be(false)
      game.addSubscriberToRound(dummyObserver)
      game.round.subscribers.contains(dummyObserver) should be(true)
    }

    "set correct start values" in {
      game.activePlayers(0).name should be("Kathrin")
      game.activePlayers(0).number should be(0)
      game.activePlayers(0).cards.length should be(6)
      game.activePlayers(1).name should be("Jakob")
      game.activePlayers(1).number should be(1)
      game.activePlayers(1).cards.length should be(6)
      game.activePlayers(2).name should be("David")
      game.activePlayers(2).number should be(2)
      game.activePlayers(2).cards.length should be(6)

      game.deck.numberOfCards should be(4)

      game.state shouldBe a[GameNotFinished]
    }

    "set the first player as start player if no one has a trump card" in {
      val injector = Guice.createInjector(new DurakModule())
      val playerFactory = injector.getInstance(classOf[PlayerFactory])

      var newGame = new GameRound(List[String]("Kathrin", "Jakob"), Rank.Jack, true)
      newGame.activePlayers = List(playerFactory.create("Kathrin", 0, List()), playerFactory.create("Jakob", 1, List()))
      newGame.getPlayerWithSmallestTrumpCard should be(0)
    }

    "not update state while round is not finished" in {
      game.updateGameRound
      game.state shouldBe a[GameNotFinished]
      game.getGameStatus should be("The round is not finished")
    }

    "return status information" in {
      game.getGameStatus should be("")
      game.getRoundStatus should be("Start of a new Round. It is " + game.round.getCurrentPlayer.name + "'s turn")
      game.playerNames.contains(game.getCurrentPlayerName) should be(true)
      game.getCurrentPlayerStatus should be("Attacker")
      game.getNumberOfCardsInDeck should be("4")
      game.getAttacksOnTable should be(List[Attack]())
      game.getAttacksOnTableString should be("")
      game.getCurrentPlayersCardsString shouldBe a[String]
      game.getCurrentPlayersCards shouldBe a[List[_]]
      game.getLastCardFromDeck.isTrump should be(true)
    }

    var defenderName = game.round.getDefender.name
    var firstAttackerName = game.round.getFirstAttacker.name
    var secondAttackerName = game.round.getSecondAttacker.name
    var inactiveName = getInactivePlayer(game).name
    var card = game.round.getFirstAttacker.cards(0)
    val cardSuit = card.suit.toString
    val cardRank = card.rank.toString

    "perform an undo" in {

      game.playCard(cardSuit, cardRank, "")
      game.undo
      game.getRoundStatus should be("The player has undone the last action")
      game.round.getCurrentPlayer.cards.contains(card) should be(true)
      game.round.attacks should be(List[Attack]())

      game.undo
    }

    "perform a redo" in {
      game.redo
      game.getRoundStatus should be(game.getCurrentPlayerName + " played the card " + card)

      game.redo
    }

    "update values when round is finished" in {
      game.endTurn
      game.endTurn

      game.state shouldBe a[GameNotFinished]
    }

    "set correct values after finishing a round" in {
      game.updateGameRound
      game.round.getFirstAttacker.name should be(secondAttackerName)
      game.round.getSecondAttacker.name should be(firstAttackerName)
      game.round.getDefender.name should be(inactiveName)

      game.statusLine should be("A new round has started")

      getInactivePlayer(game).cards.contains(card) should be(true)

      game.round.getDefender.cards.length should be(6)

      game.deck.numberOfCards should be(3)
    }

    "set the game round to finished if only one player is left" in {
      val card = game.round.getCurrentPlayer.cards(0)
      game.round.playCard(card)
      game.endTurn
      game.endTurn
      game.updateGameRound

      val secondCard = game.round.getCurrentPlayer.cards(0)
      game.round.playCard(secondCard)
      game.endTurn
      game.endTurn
      game.updateGameRound

      val thirdCard = game.round.getCurrentPlayer.cards(0)
      game.round.playCard(thirdCard)
      game.endTurn
      game.endTurn
      game.updateGameRound
      game.deck.numberOfCards should be(0)

      game.round.players = List(game.round.getDefender(), game.round.getFirstAttacker().setCards(List()))
      game.round.state = new RoundFinished

      game.updateGameRound
      game.state shouldBe a[GameFinished]
    }

    "start a new game round after the first one has ended" in {
      game.updateGameRound
      game.state shouldBe a[GameNotFinished]
    }

    "set correct player statuses for next round" in {
      game = new GameRound(List[String]("Kathrin", "Jakob", "David", "Thomas"), Rank.Eight, true)
      game.round.playCard(game.round.getCurrentPlayer.cards(0))
      game.endTurn
      game.endTurn
      game.updateGameRound

      game.round.playCard(game.round.getCurrentPlayer.cards(0))
      game.endTurn
      game.endTurn
      game.updateGameRound

      game.round.playCard(game.round.getCurrentPlayer.cards(0))
      game.endTurn
      game.endTurn
      game.updateGameRound

      game.round.playCard(game.round.getCurrentPlayer.cards(0))
      game.endTurn
      game.endTurn
      game.updateGameRound

      game.deck.numberOfCards should be(0)

      game.round.players = (List(game.round.getDefender(), game.round.getFirstAttacker().setCards(List())))
      game.round.defenderWon = true
      game.round.state = new RoundFinished

      game.updateGameRound
      game.state shouldBe a[GameFinished]
    }

    "should be created by a GameRoundFactory" in {
      (new GameRoundFactory).create(List("Kathrin", "Jakob"), "seven", true) shouldBe a[GameRound]
    }

  }

}

class DummyObserver(val key: String) extends Observer {
  def update(): Unit = {}
}