package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.RoundContext

abstract class RoundState {
  def playCard(round: RoundContext, card: Card, attack: Attack) = {}
  def endTurn(round: RoundContext) = {}
  def setupForNextRound(round: RoundContext) = {}

  def changeState(round: RoundContext, state: RoundState) = round.changeState(state)
}