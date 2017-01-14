package de.htwg.se.durak.controller.round

import de.htwg.se.durak.controller.Round
import de.htwg.se.durak.model._

abstract class AttackersTurn extends RoundNotFinished {
  var playedCard = false

  override def playCard(round: Round, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!isRankOnTable(round, card.rank)) round.statusLine = "The rank of this card is not on the table yet"
    //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    else if (round.maxNumberOfAttacksReached) endTurn(round)
    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    else putDownCard(round, card)
    round.notifyObservers

  }

  override def endTurn(round: Round) = {
    if (!playedCard) missTurn(round)
    else {
      round.turnMissed = false
      updateTurn(round)
    }
    round.notifyObservers
  }

  def missTurn(round: Round)

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: Round, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)

  override def putDownCard(round: Round, card: Card, attack: Attack) = {
    try {
      super.putDownCard(round, card)
      round.attacks = Attack(card) :: round.attacks
    } catch {
      case e: IllegalArgumentException => round.statusLine = e.getMessage
    }

  }
}