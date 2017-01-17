package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.controller.impl.Round

abstract class RoundNotFinished extends RoundState {
  def updateTurn(round: Round) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.statusLine = round.getCurrentPlayer.name + "'s round is finished. It is " + nextCurrentPlayer.name + "'s turn"
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
    round.commandManager = round.commandManager.reset
  }

}