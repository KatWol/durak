package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.controller.impl.PutDownCardCommand

class DefendersTurn extends RoundNotFinished {
  override def playCard(round: Round, card: Card, attack: Attack) {
    if (!round.attacks.contains(attack)) round.statusLine = "This attack does not exist"

    //Spieler legt Karte ab, die Karte wird dem Attack hinzugefügt und Spieler und Attack werden aktualisiert
    else {
      try {
        round.commandManager.executeCommand(new PutDownCardCommand(round, card, attack))
      } catch {
        case e: IllegalArgumentException => round.statusLine = e.getMessage
      }

    }
    round.notifyObservers
  }

  override def endTurn(round: Round) = {
    if (round.allAttacksDefended && !round.maxNumberOfAttacksReached) {
      updateTurn(round)
      if (round.players.size > 2) changeState(round, new SecondAttackersTurn)
      else changeState(round, new FirstAttackersTurn)
    } else if (!round.allAttacksDefended) {
      changeState(round, new RoundFinished)
      round.statusLine = "The round is finished and the defender has lost the round. Start a new round by entering r"
    } else {
      round.defenderWon = true
      changeState(round, new RoundFinished)
      round.statusLine = "The round is finished and the defender has won the round. Start a new round by entering r"
    }
    round.commandManager = round.commandManager.reset
    round.notifyObservers
  }

  /*override def putDownCard(round: Round, card: Card, attack: Attack) = {
    try {
      if (round.getCurrentPlayer.cards.contains(card)) {
        round.attacks = round.attacks.updated(round.attacks.indexOf(attack), attack.defend(card))
        super.putDownCard(round, card, attack)
      } else round.statusLine = "Player does not have this card in his/her hand"
    } catch {
      case e: IllegalArgumentException => round.statusLine = e.getMessage
    }

  }*/
}