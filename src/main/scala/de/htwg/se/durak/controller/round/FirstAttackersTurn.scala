package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.Round

class FirstAttackersTurn extends AttackersTurn {
  override def missTurn(round: Round) = {
    if (round.players.size > 2) {
      if (round.turnMissed) {
        round.defenderWon = true
        changeState(round, new RoundFinished)
        round.statusLine = "The round is finished and the defender won. Start a new round by entering r"
      } else updateTurn(round)
    } else {
      round.defenderWon = true
      changeState(round, new RoundFinished)
      round.statusLine = "The round is finished and the defender won. Start a new round by entering r"
    }
  }

  override def updateTurn(round: Round) = {
    super.updateTurn(round)
    changeState(round, new DefendersTurn)
  }

}