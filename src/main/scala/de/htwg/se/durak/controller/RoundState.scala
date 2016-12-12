package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class RoundState {
  def playCard(round: RoundContext, card: Card, attack: Attack) = {}
  def endTurn(round: RoundContext) = {}
  def setupForNextRound(round: RoundContext) = {}

  def changeState(round: RoundContext, state: RoundState) = { round.changeState(state) }
}