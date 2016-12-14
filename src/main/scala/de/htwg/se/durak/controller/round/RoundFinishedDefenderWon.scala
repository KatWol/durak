package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.RoundContext

class RoundFinishedDefenderWon extends RoundFinished {
  override def setupForNextRound(round: RoundContext) = {
    dealCards(round)
    updatePlayerStatuses(round, round.players(round.getIndexOfPlayer(round.getDefender)).number)
  }

  override def dealCards(round: RoundContext) = {
    super.dealCards(round)
    round.drawNCards(round.getDefender, Math.max(6 - round.getDefender.numberOfCards, 0))
  }
}