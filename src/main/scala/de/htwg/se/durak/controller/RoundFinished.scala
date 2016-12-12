package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class RoundFinished extends RoundState {
  override def setupForNextRound(round: RoundContext) = {
    //Karten an die Spieler verteilen
    dealCards(round)

    //Setzt die Startaufstellung
    val defenderNumber = round.players(round.getIndexOfPlayer(round.getDefender)).number
    if (hasDefenderWon(round)) updatePlayerStatuses(round, defenderNumber)
    else updatePlayerStatuses(round, defenderNumber + 1)
  }

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

    if (hasDefenderWon(round)) round.drawNCards(round.getDefender, Math.max(6 - round.getDefender.numberOfCards, 0))
    else pickUpAllCardsOnTable(round)
  }

  def hasDefenderWon(round: RoundContext) = round.allAttacksDefended

  //Defender gets all cards on the table
  def pickUpAllCardsOnTable(round: RoundContext) {
    val cards = for (attack <- round.attacks; cards <- attack.getCards) yield cards
    val defender = round.getDefender.takeCards(cards)
    round.updatePlayer(round.getDefender, defender)
  }
}