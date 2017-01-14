package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.Round

trait RoundState {
  def playCard(round: Round, card: Card, attack: Attack) 
  def endTurn(round: Round)

  def changeState(round: Round, state: RoundState) = round.changeState(state)
}