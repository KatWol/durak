package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack

trait RoundState {
  def playCard(round: Round, card: Card, attack: Attack)
  def endTurn(round: Round)

  def changeState(round: Round, state: RoundState) = round.changeState(state)
}