package de.htwg.se.durak.controller

class FirstAttackersFirstTurn extends FirstAttackersTurn {
  override def endTurn(round: RoundContext) = {
    if (!playedCard) throw new IllegalArgumentException("Must play a card at the beginning of a round")
    else updateTurn(round)
  }
}