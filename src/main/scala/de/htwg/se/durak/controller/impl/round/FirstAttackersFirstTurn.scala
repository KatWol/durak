package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.controller.impl.PutDownCardCommand
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Rank

class FirstAttackersFirstTurn extends RoundNotFinished {
  var playedCard = false

  override def playCard(round: Round, card: Card, attack: Attack) = {
    playedCard = true

    //Es wird überprüft, ob es sich um eine valide Karte handelt
    if (!round.attacks.isEmpty && !isRankOnTable(round, card.rank)) round.statusLine = "The rank of this card is not on the table yet"
    //Status wird geändert, wenn die maximale Anzahl an Attacks erreicht wurde
    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)   
    else {
      try {
        round.commandManager.executeCommand(new PutDownCardCommand(round, card))
        if (round.maxNumberOfAttacksReached) {
          endTurn(round, round.getCurrentPlayer.name + " played the card " + card + ". The maximum number of attacks is reached and it is the defenders turn")
        }
      } catch {
        case e: IllegalArgumentException => round.statusLine = e.getMessage
      }
    }
    round.notifyObservers
  }

  override def endTurn(round: Round) = endTurn(round, "")

  def endTurn(round: Round, statusLine: String) = {
    if (!playedCard) round.statusLine = "You must play a card at the beginning of a round"
    else updateTurn(round, statusLine)
    round.notifyObservers
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

  def isRankOnTable(round: Round, rank: Rank): Boolean = round.getCardsOnTable.exists(card => card.rank == rank)
}