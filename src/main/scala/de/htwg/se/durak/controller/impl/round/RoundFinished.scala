package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.Round

class RoundFinished extends RoundState {
  override def playCard(round: Round, card: Card, attack: Attack) = {
    round.statusLine = "The round is finished. Start a new round by entering r"
    round.notifyObservers
  }
  override def endTurn(round: Round) = {
    round.statusLine = "The round is finished. Start a new round by entering r"
    round.notifyObservers
  }

}