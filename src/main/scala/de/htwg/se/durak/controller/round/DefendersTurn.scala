package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.RoundContext

class DefendersTurn extends RoundNotFinished {
  override def playCard(round: RoundContext, card: Card, attack: Attack) {
    if (!round.attacks.contains(attack)) throw new IllegalArgumentException("This attack does not exist")

    //Spieler legt Karte ab, die Karte wird dem Attack hinzugefügt und Spieler und Attack werden aktualisiert
    putDownCard(round, card, attack)
  }

  override def endTurn(round: RoundContext) = {
    if (round.allAttacksDefended && !round.maxNumberOfAttacksReached) {
      updateTurn(round)
      if (round.players.size > 2) changeState(round, new SecondAttackersTurn)
      else changeState(round, new FirstAttackersTurn)
    } else if (!round.allAttacksDefended) changeState(round, new RoundFinishedDefenderLost)
    else changeState(round, new RoundFinishedDefenderWon)
  }

  override def putDownCard(round: RoundContext, card: Card, attack: Attack) = {
    super.putDownCard(round, card, attack)
    round.attacks = round.attacks.updated(round.attacks.indexOf(attack), attack.defend(card))
  }
}