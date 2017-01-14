package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.Round

class RoundFinished extends RoundState {
  override def playCard(round: Round, card: Card, attack: Attack) = throw new IllegalStateException("The round is finished. Start a new round");
  override def endTurn(round: Round) = throw new IllegalStateException("The round is finished. Start a new round")

}