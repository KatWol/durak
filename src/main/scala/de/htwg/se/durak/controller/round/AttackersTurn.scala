package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.RoundContext

abstract class AttackersTurn extends RoundNotFinished {
  var playedCard = false

  override def playCard(round: RoundContext, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!isRankOnTable(round, card.rank)) throw new IllegalArgumentException("The rank of this card is not on the table yet")
    //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    if (round.maxNumberOfAttacksReached) endTurn(round)
    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    else putDownCard(round, card)

  }

  override def endTurn(round: RoundContext) = {
    if (!playedCard) missTurn(round)
    else {
      round.turnMissed = false
      updateTurn(round)
    }
  }

  def missTurn(round: RoundContext)

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: RoundContext, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)

  override def putDownCard(round: RoundContext, card: Card, attack: Attack) = {
    super.putDownCard(round, card)
    round.attacks = Attack(card) :: round.attacks
  }
}