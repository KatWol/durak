package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

//State ändern sich in der folgenden Reihenfolge:
//FirstAttackersFirstTurn -> DefendersTurn -> SecondAttackersTurn -> FirstAttackersTurn -> DefendersTurn ... -> RoundFinished
class RoundContext(var deck: Deck, var players: List[Player]) {
  private[controller] var state: RoundState = new FirstAttackersFirstTurn
  var attacks = List[Attack]()
  var turnMissed = false

  //Methoden, die von den State-Objekten ausgeführt werden

  def playCard(card: Card, attack: Attack = null) = this.state.playCard(this, card, attack)
  def endTurn = this.state.endTurn(this)
  def setupForNextRound = this.state.setupForNextRound(this)

  def changeState(state: RoundState) = { this.state = state }

  //Generelle Methoden für die Runde, unabhängig von dem aktuellen State

  def getIndexOfPlayer(player: Player): Int = players.indexOf(player)

  //Gibt den Spieler zurück, der an der Reihe ist
  def getCurrentPlayer(): Player = players.filter(_.hisTurn == true).head

  //Gibt den Spieler zurück, der als nächste an der Reihe ist
  def getNextCurrentPlayer(): Player = {
    var newCurrentPlayer: Player = null
    var indexNewPlayer = (getIndexOfPlayer(getCurrentPlayer) + 1)
    do {
      newCurrentPlayer = players(indexNewPlayer % players.size)
      indexNewPlayer += 1
    } while (newCurrentPlayer.status == PlayerStatus.Inactive)
    newCurrentPlayer
  }

  def getDefender(): Player = players.filter(_.isDefender).head

  //Gibt den Spieler eine Position vor dem Defender zurück
  def getFirstAttacker(): Player = players(((getIndexOfPlayer(getDefender) - 1) + players.size) % players.size)

  //Gibt den zweiten Angreifer zurück, fall es zwei Angreifer gibt, sonst null
  def getSecondAttacker(): Player = {
    if (players.size == 2) null
    else players((getIndexOfPlayer(getDefender) + 1) % players.size)
  }

  //Überprüft, ob alle Attacks auf dem Tisch verteidigt wurden
  def allAttacksDefended(): Boolean = {
    var allAttacksDefended = true
    for (attack <- attacks) if (!attack.isCompleted) allAttacksDefended = false
    allAttacksDefended
  }

  def maxNumberOfAttacksReached(): Boolean = attacks.size >= 6

  //Zieht Karten vom Deck und fügt diese dem Spieler hinzu
  def drawNCards(player: Player, numberOfCards: Int) = {
    val cardsAndDeck = deck.drawNCards(numberOfCards)
    updatePlayer(player, player.takeCards(cardsAndDeck._1))
    deck = cardsAndDeck._2
  }

  //Der oldPlayer wird durch den newPlayer ersetzt
  def updatePlayer(oldPlayer: Player, newPlayer: Player) = players = players.updated(getIndexOfPlayer(oldPlayer), newPlayer)
}