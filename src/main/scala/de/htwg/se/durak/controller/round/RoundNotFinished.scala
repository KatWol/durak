package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.controller.Round

abstract class RoundNotFinished extends RoundState {
  def updateTurn(round: Round) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
  }

  def putDownCard(round: Round, card: Card, attack: Attack = null) = {
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card))
  }

}