package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.controller.Round

class FirstAttackersFirstTurn extends FirstAttackersTurn {
  override def playCard(round: Round, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!round.attacks.isEmpty && !isRankOnTable(round, card.rank)) throw new IllegalArgumentException("The rank of this card is not on the table yet")
    //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    if (round.maxNumberOfAttacksReached) endTurn(round)
    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    else putDownCard(round, card)
  }

  override def endTurn(round: Round) = {
    if (!playedCard) throw new IllegalArgumentException("Must play a card at the beginning of a round")
    else updateTurn(round)
  }
}