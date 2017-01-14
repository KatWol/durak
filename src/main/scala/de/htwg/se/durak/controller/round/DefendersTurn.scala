package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.Round

class DefendersTurn extends RoundNotFinished {
  override def playCard(round: Round, card: Card, attack: Attack) {
    if (!round.attacks.contains(attack)) throw new IllegalArgumentException("This attack does not exist")

    //Spieler legt Karte ab, die Karte wird dem Attack hinzugefügt und Spieler und Attack werden aktualisiert
    putDownCard(round, card, attack)
  }

  override def endTurn(round: Round) = {
    if (round.allAttacksDefended && !round.maxNumberOfAttacksReached) {
      updateTurn(round)
      if (round.players.size > 2) changeState(round, new SecondAttackersTurn)
      else changeState(round, new FirstAttackersTurn)
    } else if (!round.allAttacksDefended) changeState(round, new RoundFinished)
    else {
      round.defenderWon = true
      changeState(round, new RoundFinished)
    }
  }

  override def putDownCard(round: Round, card: Card, attack: Attack) = {
    super.putDownCard(round, card, attack)
    round.attacks = round.attacks.updated(round.attacks.indexOf(attack), attack.defend(card))
  }
}