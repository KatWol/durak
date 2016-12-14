package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.RoundContext

class RoundFinishedDefenderLost extends RoundFinished {
  override def setupForNextRound(round: RoundContext) = {
    dealCards(round)
    updatePlayerStatuses(round, round.players(round.getIndexOfPlayer(round.getDefender)).number + 1)
  }

  override def dealCards(round: RoundContext) {
    super.dealCards(round)
    pickUpAllCardsOnTable(round)
  }

  //Defender gets all cards on the table
  def pickUpAllCardsOnTable(round: RoundContext) = round.updatePlayer(round.getDefender, round.getDefender.takeCards(round.getCardsOnTable))
}