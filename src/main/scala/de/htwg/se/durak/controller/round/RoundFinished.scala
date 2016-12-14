package de.htwg.se.durak.controller.round

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.RoundContext

abstract class RoundFinished extends RoundState {
  //Die Methode kann mit der Methode aus dem GameRound-Controller zusammengefasst werden
  //Gibt eine Liste mit der Startaufstellung für die nächste Runde zurück
  def updatePlayerStatuses(round: RoundContext, nextStartPlayer: Int) = {
    round.players = for (player <- round.players) yield {
      if (player.number == nextStartPlayer % round.players.size) player.setTurn(true).setStatus(PlayerStatus.Attacker)
      else if (player.number == (nextStartPlayer + 1) % round.players.size) player.setTurn(false).setStatus(PlayerStatus.Defender)
      else if (player.number == (nextStartPlayer + 2) % round.players.size) player.setTurn(false).setStatus(PlayerStatus.Attacker)
      else player.setTurn(false).setStatus(PlayerStatus.Inactive)
    }
  }

  def dealCards(round: RoundContext) = {
    round.drawNCards(round.getFirstAttacker, Math.max(6 - round.getFirstAttacker.numberOfCards, 0))
    if (round.getSecondAttacker != null) round.drawNCards(round.getSecondAttacker, Math.max(6 - round.getSecondAttacker.numberOfCards, 0))
  }
}