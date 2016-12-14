package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.RoundContext

class SecondAttackersTurn extends AttackersTurn {
  override def missTurn(round: RoundContext) = {
    round.turnMissed = true
    updateTurn(round)
    changeState(round, new FirstAttackersTurn)
  }

  override def updateTurn(round: RoundContext) = {
    super.updateTurn(round)
    changeState(round, new FirstAttackersTurn)
  }
}