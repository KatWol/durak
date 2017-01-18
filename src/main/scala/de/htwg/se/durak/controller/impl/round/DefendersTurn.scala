package de.htwg.se.durak.controller.impl.round

import de.htwg.se.durak.controller.impl.PutDownCardCommand
import de.htwg.se.durak.controller.impl.Round
import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.impl.RoundFinishedEvent
import scala.swing.event.Event

class DefendersTurn extends RoundNotFinished {
  override def playCard(round: Round, card: Card, attack: Attack) {
    if (!round.attacks.contains(attack)) round.statusLine = "This attack does not exist"

    //Spieler legt Karte ab, die Karte wird dem Attack hinzugefügt und Spieler und Attack werden aktualisiert
    else {
      try {
        round.commandManager.executeCommand(new PutDownCardCommand(round, card, attack))
        if (round.allAttacksDefended && round.maxNumberOfAttacksReached) {
          endTurn(round, "", new RoundFinishedEvent)
        } else if (round.allAttacksDefended) {

          endTurn(round, round.getCurrentPlayer.name + "'s turn is finished. It is " + round.getNextCurrentPlayer.name + "'s turn", null)
        }
      } catch {
        case e: IllegalArgumentException => round.statusLine = e.getMessage
      }

    }
    round.notifyObservers
  }

  override def endTurn(round: Round) = endTurn(round, "", null)

  def endTurn(round: Round, statusLine: String, event: Event) = {
    //  Defender hat gewonnen, da er die maximale Anzahl an Attacks erreicht hat und alle verteidigt hat
    if (statusLine == "" && event != null) {
      round.defenderWon = true
      changeState(round, new RoundFinished)
      round.notifyObservers(event)
    } // Defender hat alle Attacks verteidig, aber es wurde noch nicht die maximale Anzahl an Attacks erreicht
    else if (statusLine != "" && event == null) {
      val nextCurrentPlayer = round.getNextCurrentPlayer
      round.statusLine = statusLine
      round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.changeTurn)
      round.updatePlayer(nextCurrentPlayer, nextCurrentPlayer.changeTurn)
      if (round.players.size > 2) changeState(round, new SecondAttackersTurn)
      else changeState(round, new FirstAttackersTurn)
    } // Defender hat die Runde beendet, bevor er alle Attacks verteidigt hat und verliert damit
    else if (statusLine == "" && event == null) {
      round.defenderWon = false
      changeState(round, new RoundFinished)
      changeState(round, new RoundFinished)
      round.notifyObservers(new RoundFinishedEvent)

    }
    round.commandManager = round.commandManager.reset

  }

}