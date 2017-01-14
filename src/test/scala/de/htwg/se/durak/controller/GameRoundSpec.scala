package de.htwg.se.durak.controller

import org.scalatest.Matchers
import org.scalatest.WordSpec

import de.htwg.se.durak.controller.game.GameNotFinished
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Deck
import de.htwg.se.durak.model.Card

class GameRoundSpec extends WordSpec with Matchers {
  def getInactivePlayer(game: GameRound) = game.round.players.filter(_.status == PlayerStatus.Inactive).head

  "A GameRound" should {
    var game = new GameRound(List[String]("Kathrin", "Jakob", "David", "Thomas"), Rank.Seven, true)

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

      game.deck.numberOfCards should be(8)

      game.state shouldBe a[GameNotFinished]
    }

    "not update state while round is not finished" in {
      game.state shouldBe a[GameNotFinished]
    }

    var defenderName = game.round.getDefender.name
    var firstAttackerName = game.round.getFirstAttacker.name
    var secondAttackerName = game.round.getSecondAttacker.name
    var inactiveName = getInactivePlayer(game).name
    var card = game.round.getFirstAttacker.cards(0)

    "update values when round is finished" in {
      game.round.playCard(card)
      game.round.endTurn

      game.round.endTurn
      game.round.defenderWon should be(false)

      game.state shouldBe a[GameNotFinished]
    }

    "set correct values after finishing a round" in {
      game.updateState
      game.round.getFirstAttacker.name should be(secondAttackerName)
      game.round.getSecondAttacker.name should be(firstAttackerName)
      game.round.getDefender.name should be(inactiveName)

      game.statusLine should be("A new round has started")

      getInactivePlayer(game).cards.contains(card) should be(true)

      game.round.getDefender.cards.length should be(6)

      game.deck.numberOfCards should be(7)
    }
  }
}