package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.controller.impl.PutDownCardCommand
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Card
import scala.swing.event.Event
import de.htwg.se.durak.controller.impl.RoundFinishedEvent

class FirstAttackersTurn extends RoundNotFinished {
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
          endTurn(round, round.getCurrentPlayer.name + " played the card " + card + ". The maximum number of attacks is reached and it is the defenders turn")
        }
      } catch {
        case e: IllegalArgumentException => round.statusLine = e.getMessage
      }
      round.notifyObservers
    }

  }

  override def endTurn(round: Round) = endTurn(round, "")
  def endTurn(round: Round, statusLine: String) = {
    var event: Event = null
    if (!playedCard) event = missTurn(round)
    else {
      round.turnMissed = false
      updateTurn(round, statusLine)
    }
    round.commandManager = round.commandManager.reset
    if (event == null) round.notifyObservers
    else round.notifyObservers(event)
  }

  def missTurn(round: Round): Event = {
    if (round.players.size > 2) {
      if (round.turnMissed) {
        round.defenderWon = true
        changeState(round, new RoundFinished)
        round.commandManager = round.commandManager.reset
        new RoundFinishedEvent
      } else {
        updateTurn(round, "")
        null
      }
    } else {
      round.defenderWon = true
      changeState(round, new RoundFinished)
      round.commandManager = round.commandManager.reset
      new RoundFinishedEvent
    }
  }

  def updateTurn(round: Round, statusLine: String) = {
    val nextCurrentPlayer = round.getNextCurrentPlayer
    if (statusLine == "") round.statusLine = round.getCurrentPlayer.name + "'s turn is finished. It is " + nextCurrentPlayer.name + "'s turn"
    else round.statusLine = statusLine
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
    round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
    round.commandManager = round.commandManager.reset
    changeState(round, new DefendersTurn)
  }

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(round: Round, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)

}