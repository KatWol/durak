package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round

class SecondAttackersTurn extends AttackersTurn {
  override def missTurn(round: Round) = {
    round.turnMissed = true
    updateTurn(round)
    changeState(round, new FirstAttackersTurn)
  }

  override def updateTurn(round: Round) = {
    super.updateTurn(round)
    changeState(round, new FirstAttackersTurn)
  }
}