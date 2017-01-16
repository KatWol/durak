package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round

class FirstAttackersTurn extends AttackersTurn {
  override def missTurn(round: Round) = {
    if (round.players.size > 2) {
      if (round.turnMissed) {
        round.defenderWon = true
        changeState(round, new RoundFinished)
        round.statusLine = "The round is finished and the defender won. Start a new round by entering r"
        round.commandManager = round.commandManager.reset
      } else updateTurn(round)
    } else {
      round.defenderWon = true
      changeState(round, new RoundFinished)
      round.statusLine = "The round is finished and the defender won. Start a new round by entering r"
      round.commandManager = round.commandManager.reset
    }
  }

  override def updateTurn(round: Round) = {
    super.updateTurn(round)
    changeState(round, new DefendersTurn)
  }

}