package de.htwg.se.durak.controller.impl.game

import de.htwg.se.durak.controller.impl.GameRound
import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.controller.impl.round.RoundFinished
import de.htwg.se.durak.controller.impl.round.RoundNotFinished

class GameNotFinished extends GameState {
  override def updateState(game: GameRound) = {
    game.round.state match {
      case notFinished: RoundNotFinished => game.statusLine = "The round is not finished"
      case finished: RoundFinished =>
        {
          setupForNextRound(game)
          game.statusLine = "A new round has started"
          if (isGameRoundFinished(game)) {
            game.changeState(new GameFinished)
            game.statusLine = "The game round is finished. \n******Durak: " + getDurak(game) + "\nStart a new game by entering s"
          }
        }
    }
    game.notifyObservers
  }

  def setupForNextRound(game: GameRound) = {
    dealCards(game.round)
    game.deck = game.round.deck
    game.activePlayers = game.round.players
    removeFinishedPlayers(game)
    val indexOfDefender = game.round.getIndexOfPlayer(game.round.getDefender)
    if (game.round.defenderWon) game.activePlayers = game.setPlayerStatusForNextRound(indexOfDefender)
    else game.activePlayers = game.setPlayerStatusForNextRound(indexOfDefender + 1)

    game.round = new Round(game.deck, game.activePlayers, game.trumpSuit, game.subscribers)
  }

  def dealCards(round: Round) = {
    round.drawNCards(round.getFirstAttacker, Math.max(6 - round.getFirstAttacker.numberOfCards, 0))
    if (round.getSecondAttacker != null) round.drawNCards(round.getSecondAttacker, Math.max(6 - round.getSecondAttacker.numberOfCards, 0))
    if (round.defenderWon) round.drawNCards(round.getDefender, Math.max(6 - round.getDefender.numberOfCards, 0))
    else pickUpAllCardsOnTable(round)
  }

  //Defender gets all cards on the table
  def pickUpAllCardsOnTable(round: Round) = round.updatePlayer(round.getDefender, round.getDefender.takeCards(round.getCardsOnTable))

  //Removes all players without cards
  def removeFinishedPlayers(game: GameRound) = game.activePlayers = for (player <- game.round.players; if player.numberOfCards > 0) yield player

  def isGameRoundFinished(game: GameRound): Boolean = {
    if (game.deck.isEmpty && game.activePlayers.size < 2) true
    else false
  }

  //Macht nur Sinn, wenn die GameRound beendet wurde
  def getDurak(game: GameRound): String = game.activePlayers(0).name

}