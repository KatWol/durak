package de.htwg.se.durak.controller.game

import de.htwg.se.durak.controller.GameRound
import de.htwg.se.durak.controller.Round
import de.htwg.se.durak.model.Player

class StartOfRound extends GameState {
  override def updateState(game: GameRound): Boolean = {
    setupForNextRound(game)
    if(isGameRoundFinished(game)) game.changeState(new GameFinished)
    else game.changeState(new InRound)
    true
  }
  
  def setupForNextRound(game: GameRound) = {
    dealCards(game.round)
    game.deck = game.round.deck
    game.players = game.round.players
    if (game.round.defenderWon) game.players = game.setPlayerStatusForNextRound(game.round.players(game.round.getIndexOfPlayer(game.round.getDefender)).number)
    else game.players = game.setPlayerStatusForNextRound(game.round.players(game.round.getIndexOfPlayer(game.round.getDefender)).number + 1)   
    
    removeFinishedPlayers(game)
    game.round = new Round(game.deck, game.players)
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
  def removeFinishedPlayers(game: GameRound) = game.players = for (player <- game.round.players; if player.numberOfCards > 0) yield player
  
  def isGameRoundFinished(game: GameRound): Boolean = {
    if(game.deck.isEmpty && game.players.size < 2) true
    else false
  }
}