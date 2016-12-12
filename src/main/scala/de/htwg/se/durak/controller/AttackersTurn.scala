package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

abstract class AttackersTurn extends RoundState {
  var playedCard = false

  override def playCard(round: RoundContext, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!round.attacks.isEmpty && !isRankOnTable(round, card.rank)) throw new IllegalArgumentException("The rank of this card is not on the table yet")
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

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: RoundContext, rank: Rank): Boolean = {
    var isRankOnTable = false
    for (attack <- round.attacks) {
      if (attack.attackingCard.rank == rank) isRankOnTable = true
      if (attack.defendingCard != null && attack.defendingCard.rank == rank) isRankOnTable = true
    }
    isRankOnTable
  }

  def putDownCard(round: RoundContext, card: Card) = {
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card)._2)
    round.attacks = Attack(card) :: round.attacks
  }

  //Beendet die Runde des Angreifers und ändert den Status
  def missTurn(round: RoundContext)

  def updateTurn(round: RoundContext) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
  }

}