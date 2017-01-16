package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.PutDownCardCommand

abstract class AttackersTurn extends RoundNotFinished {
  var playedCard = false

  override def playCard(round: Round, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!isRankOnTable(round, card.rank)) {
      round.statusLine = "The rank of this card is not on the table yet"
      round.notifyObservers
    } //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    else if (round.maxNumberOfAttacksReached) {
      round.statusLine = "The maximum number of attacks is reached and the turn is finished. It is " + round.getNextCurrentPlayer() + "'s turn"
      endTurn(round)
    } //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    else {
      try {
        round.commandManager.executeCommand(new PutDownCardCommand(round, card, attack))
      } catch {
        case e: IllegalArgumentException => round.statusLine = e.getMessage
      }
      round.notifyObservers
    }

  }

  override def endTurn(round: Round) = {
    if (!playedCard) missTurn(round)
    else {
      round.turnMissed = false
      updateTurn(round)
    }
    round.commandManager = round.commandManager.reset
    round.notifyObservers
  }

  def missTurn(round: Round)

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: Round, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)

  /*override def putDownCard(round: Round, card: Card, attack: Attack) = {
    try {
      super.putDownCard(round, card)
      round.attacks = Attack(card) :: round.attacks
    } catch {
      case e: IllegalArgumentException => round.statusLine = e.getMessage
    }

  }*/
}