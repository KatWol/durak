package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class Round(var deck: Deck, var players: List[Player]) {
  var attacks = List[Attack]()
  var roundFinished = false
  var turnMissed = false

  def getCurrentPlayer(): Player = players.filter(_.hisTurn == true)(0)

  //Der Spieler, der als nächstes an der Reihe ist, wird zurückgegeben --> inaktive Spieler werden nicht berücksichtigt
  def getNextCurrentPlayer(): Player = {
    val index = players.indexOf(this.getCurrentPlayer());
    var newCurrentPlayer: Player = null
    var indexNewPlayer = (index + 1)
    do {
      newCurrentPlayer = players(indexNewPlayer % players.size)
      indexNewPlayer += 1
    } while (newCurrentPlayer.status == PlayerStatus.Inactive)
    newCurrentPlayer
  }

  //Gibt zurück, ob die Runde beendet ist, wenn ein Spieler eine Runde aussetzt
  def missTurn(): Boolean = {
    val player = getCurrentPlayer
    if (player.isDefender) roundFinished = true
    if (player.isAttacker && turnMissed) roundFinished = true
    if (player.isAttacker && !turnMissed) turnMissed = true
    roundFinished
  }

  //Wird aufgerufen, wenn ein Spieler seine Runde beendet
  def endTurn(): Unit = {
    if (getCurrentPlayer.isAttacker) updateTurn
    else if (getCurrentPlayer.isDefender && defendersTurnOver) updateTurn
  }

  //Es wird upgedated wer dran ist, wenn die Runde noch nicht beendet ist
  def updateTurn(): Unit = {
    if (!roundFinished) {
      val indexCurrentPlayer = getIndexOfPlayer(getCurrentPlayer)
      val indexNextCurrentPlayer = getIndexOfPlayer(getNextCurrentPlayer)
      players = players.updated(indexCurrentPlayer, getCurrentPlayer.changeTurn)
      players = players.updated(indexNextCurrentPlayer, players(indexNextCurrentPlayer).changeTurn)
    }
  }

  def defendersTurnOver(): Boolean = {
    //1. Fall: Der Defender ruft missTurn() auf -> Ganze Runde wird beendet
    if (roundFinished) true
    //2. Fall: Der Defender hat alle Attacks verteidigt
    else if (allAttacksDefended) true
    else false
  }

  def hasDefenderWon(): Boolean = {
    //1. Fall: Runde ist nicht beendet
    if (!roundFinished) false
    //2. Fall: Runde ist beendet, Spieler hat gewonnen, wenn er alle Attacks verteidigt hat
    else allAttacksDefended
  }

  //Prüft ob Runde beendet ist: entweder, weil die maximale Anzahl an Attacks erreicht wurde, oder weil die Spieler ausgesetzt haben
  def isRoundFinished(): Boolean = {
    if (maxNumberOfAttacksReached && allAttacksDefended) roundFinished = true
    roundFinished
  }

  def maxNumberOfAttacksReached(): Boolean = attacks.size >= 6

  def allAttacksDefended(): Boolean = {
    var allAttacksDefended = true
    for (attack <- attacks) if (!attack.isCompleted) allAttacksDefended = false
    allAttacksDefended
  }

  def updatePlayerStatusForNextRound(): List[Player] = {
    if (!roundFinished) players
    else if (hasDefenderWon) updatePlayerStatuses(players(getIndexOfPlayer(getDefender)).number)
    else updatePlayerStatuses(players(getIndexOfPlayer(getDefender)).number + 1)
  }

  def getDefender(): Player = players.filter(_.isDefender)(0)
  def getIndexOfPlayer(player: Player): Int = players.indexOf(player)

  //Die Methode kann mit der Methode aus dem GameRound-Controller zusammengefasst werden
  //Gibt eine Liste mit der Startaufstellung für die nächste Runde zurück
  def updatePlayerStatuses(nextStartPlayer: Int): List[Player] = {
    players = for (player <- players) yield {
      if (player.number == nextStartPlayer) player.setTurn(true).setStatus(PlayerStatus.Attacker)
      else if (player.number == ((nextStartPlayer + 1) % players.size)) player.setTurn(false).setStatus(PlayerStatus.Defender)
      else if (player.number == ((nextStartPlayer + 2) % players.size)) player.setTurn(false).setStatus(PlayerStatus.Attacker)
      else player.setTurn(false).setStatus(PlayerStatus.Inactive)
    }
    players
  }

  //Es wird überprüft, ob der gegebene Kartenwert bereits auf dem Tisch liegt
  def isRankOnTable(rank: Rank): Boolean = {
    var isRankOnTable = false
    for (attack <- attacks) {
      if (attack.attackingCard.rank == rank) isRankOnTable = true
      if (attack.defendingCard != null && attack.defendingCard.rank == rank) isRankOnTable = true
    }
    isRankOnTable
  }

  def getAttackByIndex(index: Int): Attack = {
    if (index - 1 > attacks.size) throw new IndexOutOfBoundsException
    attacks(index - 1)
  }

  //Methode wird immer dann aufgerufen, wenn ein Angreifer eine Karte spielt
  def startAttack(card: Card): Unit = {
    //Überprüft, ob alle Bedingungen für einen validen Zug gegeben sind
    checkGeneralConditionsForNextMove(PlayerStatus.Attacker)
    if (!attacks.isEmpty && !isRankOnTable(card.rank)) throw new IllegalArgumentException("The rank of this card is not on the table yet")
    if (maxNumberOfAttacksReached) throw new IndexOutOfBoundsException("The maximum number of attacks is reached")

    //Update turnMissed-Flag
    turnMissed = false

    //Spieler legt Karte ab, ein neuer Attack der Runde hinzugefügt und der Spieler aktualisiert (da sich seine Karten auf der Hand verändert haben)
    val newPlayer = getCurrentPlayer.putDownCard(card)._2
    attacks = Attack(card) :: attacks
    players = players.updated(getIndexOfPlayer(getCurrentPlayer), newPlayer)
  }

  //Methode wird immer dann aufgerufen, wenn ein Verteidiger eine Karte spielt
  def defendAttack(card: Card, attack: Attack): Unit = {
    //Überprüft, ob alle Bedingungen für einen validen Zug gegeben sind
    checkGeneralConditionsForNextMove(PlayerStatus.Defender)
    if (!attacks.contains(attack)) throw new IllegalArgumentException("This attack does not exist")

    //Update turnMissed-Flag
    turnMissed = false

    //Spieler legt Karte ab, die Karte wird dem Attack hinzugefügt und Spieler und Attack werden aktualisiert  
    val newPlayer = getCurrentPlayer.putDownCard(card)._2
    val finishedAttack = attack.defend(card)
    players = players.updated(getIndexOfPlayer(getCurrentPlayer), newPlayer)
    attacks = attacks.updated(attacks.indexOf(attack), finishedAttack)
  }

  def checkGeneralConditionsForNextMove(requiredStatus: PlayerStatus): Unit = {
    if (roundFinished) throw new IllegalArgumentException("This round is finished")
    if (getCurrentPlayer.status != requiredStatus) throw new IllegalArgumentException("This player is not allowed to perform this action")
  }

}

object Round extends IRound {
  override def startNextRound(deck: Deck, players: List[Player]): Tuple2[Deck, List[Player]] = {
    val round = new Round(deck, players)
    while (!round.roundFinished) {

    }
    Tuple2(deck, players)
  }
}