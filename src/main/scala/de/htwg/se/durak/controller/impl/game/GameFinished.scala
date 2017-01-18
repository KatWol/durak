package de.htwg.se.durak.controller.impl.game

import de.htwg.se.durak.controller.impl.GameRound
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Deck
import de.htwg.se.durak.model.Player
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.controller.impl.Round
import com.google.inject.Guice
import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.model.PlayerFactory

class GameFinished extends GameState {
  override def updateState(game: GameRound) = {
    startNewGameRound(game)
    game.statusLine = "A new game round has started"
    game.notifyObservers
  }

  def startNewGameRound(game: GameRound) = {
    game.deck = game.getDeck(game.startWithRank)
    val indexNextStartPlayer = getNextStartPlayer(game)
    val temp = getPlayers(game.allPlayers, game.deck)
    game.activePlayers = temp._1
    game.deck = temp._2

    game.activePlayers = game.setPlayerStatusForNextRound(indexNextStartPlayer)
    game.state = new GameNotFinished
    game.round = new Round(game.deck, game.activePlayers, game.trumpSuit, game.subscribers)
  }

  def getPlayers(allPlayers: List[Player], deck: Deck): (List[Player], Deck) = {
    var temp = Tuple2(List[Card](), deck)
    var players: List[Player] =
      for (player <- allPlayers) yield {
        temp = deck.drawNCards(6)
        player.setCards(temp._1).setStatus(PlayerStatus.Inactive)
      }
    (players, temp._2)
  }

  def getNextStartPlayer(game: GameRound): Int = {
    val nextStartPlayer = getNextDefender(game) - 1
    if (nextStartPlayer >= 0) nextStartPlayer
    else nextStartPlayer + game.allPlayers.size
  }

  def getNextDefender(game: GameRound): Int = game.activePlayers(0).number

}