package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class DefendersTurn extends RoundState {
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
    } else changeState(round, new RoundFinished)
  }

  def putDownCard(round: RoundContext, card: Card, attack: Attack) = {
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card)._2)
    round.attacks = round.attacks.updated(round.attacks.indexOf(attack), attack.defend(card))
  }

  def updateTurn(round: RoundContext) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
  }
}