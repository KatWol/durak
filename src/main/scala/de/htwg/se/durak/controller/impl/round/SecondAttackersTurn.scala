package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.controller.impl.PutDownCardCommand
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Rank

class SecondAttackersTurn extends RoundNotFinished {
  var playedCard = false

  override def playCard(round: Round, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!isRankOnTable(round, card.rank)) {
      round.statusLine = "The rank of this card is not on the table yet"
      round.notifyObservers
    } //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    else {
      try {
        round.commandManager.executeCommand(new PutDownCardCommand(round, card, attack))
        if (round.maxNumberOfAttacksReached) {
          endTurnAndStartDefendersTurn(round, round.getCurrentPlayer.name + " played the card " + card + ". The maximum number of attacks is reached and it is the defenders turn")
        }
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

  def missTurn(round: Round) = {
    round.turnMissed = true
    updateTurn(round)
    changeState(round, new FirstAttackersTurn)
  }

  def updateTurn(round: Round) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    round.statusLine = round.getCurrentPlayer.name + "'s turn is finished. It is " + nextCurrentPlayer.name + "'s turn"
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
    round.commandManager = round.commandManager.reset
    changeState(round, new FirstAttackersTurn)
  }

  def endTurnAndStartDefendersTurn(round: Round, statusLine: String) {
    round.turnMissed = false
    round.statusLine = statusLine
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(round.getDefender, round.getDefender.changeTurn)
    round.commandManager = round.commandManager.reset
    changeState(round, new DefendersTurn)

  }

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: Round, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)
}