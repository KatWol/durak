package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack

class RoundFinished extends RoundState {
  override def playCard(round: Round, card: Card, attack: Attack) = {
    round.statusLine = "The round is finished"
    round.notifyObservers
  }
  override def endTurn(round: Round) = {
    round.statusLine = "The round is finished"
    round.notifyObservers
  }

}