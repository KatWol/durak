package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.controller.RoundContext

abstract class RoundNotFinished extends RoundState {
  def updateTurn(round: RoundContext) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
  }

  def putDownCard(round: RoundContext, card: Card, attack: Attack = null) = {
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card))
  }

}