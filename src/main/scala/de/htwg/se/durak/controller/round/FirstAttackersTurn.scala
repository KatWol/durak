package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.RoundContext

class FirstAttackersTurn extends AttackersTurn {
  override def missTurn(round: RoundContext) = {
    if (round.players.size > 2) {
      if (round.turnMissed) changeState(round, new RoundFinishedDefenderWon)
      else updateTurn(round)
    } else changeState(round, new RoundFinishedDefenderWon)
  }

  override def updateTurn(round: RoundContext) = {
    super.updateTurn(round)
    changeState(round, new DefendersTurn)
  }

}