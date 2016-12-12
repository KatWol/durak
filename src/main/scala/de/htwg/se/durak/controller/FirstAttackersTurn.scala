package de.htwg.se.durak.controller

class FirstAttackersTurn extends AttackersTurn {
  override def missTurn(round: RoundContext) = {
    if (round.players.size > 2) {
      if (round.turnMissed) changeState(round, new RoundFinished)
      else updateTurn(round)
    } else changeState(round, new RoundFinished)
  }

  override def updateTurn(round: RoundContext) = {
    super.updateTurn(round)
    changeState(round, new DefendersTurn)
  }

}