package de.htwg.se.durak.controller.impl

import de.htwg.se.durak.controller.impl.round.FirstAttackersFirstTurn
import de.htwg.se.durak.controller.impl.round.RoundState
import de.htwg.se.durak.model._
import de.htwg.se.util.Observable
import de.htwg.se.util.Observer
import de.htwg.se.util.CommandManager
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Player
import com.google.inject.Guice
import de.htwg.se.durak.DurakModule

//State ändern sich in der folgenden Reihenfolge:
//FirstAttackersFirstTurn -> DefendersTurn -> SecondAttackersTurn -> FirstAttackersTurn -> DefendersTurn ... -> RoundFinished (und hier je nachdem RoundFinishedDefenderWon oder RoundFinishedDefenderLost)
class Round(var deck: Deck, var players: List[Player], val trumpSuit: Suit) extends Observable {
  val injector = Guice.createInjector(new DurakModule())
  private[controller] var state: RoundState = new FirstAttackersFirstTurn
  var attacks = List[Attack]()
  var turnMissed = false
  var defenderWon = false

  var statusLine = "Start of a new Round. It is " + getCurrentPlayer.name + "'s turn"
  var commandManager = new CommandManager

  //**************Methoden, die von den State-Objekten ausgeführt werden********************

  def playCard(card: Card, attack: Attack = null): Unit = this.state.playCard(this, card, attack)
  def endTurn = this.state.endTurn(this)

  def changeState(state: RoundState) = this.state = state

  //**************Generelle Methoden für die Runde, unabhängig von dem aktuellen State******

  def playCard(suit: String, rank: String, attack: String): Unit = {
    val card = parseToCard(suit, rank)
    try {
      val anAttack = getAttackByIndex(attack.toInt)
      if (card != null) playCard(card, anAttack)
      else {
        statusLine = "False input!"
        notifyObservers
      }
    } catch {
      case e: Exception => {
        if (card != null) playCard(card)
        else {
          statusLine = "False input!"
          notifyObservers
        }
      }
    }
  }

  def getIndexOfPlayer(player: Player): Int = players.indexOf(player)

  //Gibt den Spieler zurück, der an der Reihe ist
  def getCurrentPlayer(): Player = players.filter(_.hisTurn == true).head

  //Gibt den Spieler zurück, der als nächste an der Reihe ist  
  def getNextCurrentPlayer(): Player = {
    val activePlayers = players.filter(player => player.status != PlayerStatus.Inactive)
    activePlayers((activePlayers.indexOf(getCurrentPlayer) + 1) % activePlayers.size)
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
  def allAttacksDefended(): Boolean = !attacks.exists(attack => !(attack.isCompleted))

  //Max erreicht wenn 6 Attacks gestartet wurden, oder wenn der Defender keine Karten mehr hat
  def maxNumberOfAttacksReached(): Boolean = {
    attacks.size >= 6 || getDefender.cards.size <= this.getNumberOfUndefendedAttacks
  }

  def getNumberOfUndefendedAttacks: Int = attacks.filter(a => !a.isCompleted).size

  //Zieht Karten vom Deck und fügt diese dem Spieler hinzu
  def drawNCards(player: Player, numberOfCards: Int) = {
    val (cards, newDeck) = deck.drawNCards(numberOfCards)
    updatePlayer(player, player.takeCards(cards.asInstanceOf[List[de.htwg.se.durak.model.impl.Card]]))
    deck = newDeck
  }

  //Der oldPlayer wird durch den newPlayer ersetzt
  def updatePlayer(oldPlayer: Player, newPlayer: Player) = players = players.updated(getIndexOfPlayer(oldPlayer), newPlayer)

  //Returns a list containing all cards on the table
  def getCardsOnTable: List[Card] = (for (attack <- attacks; cards <- attack.getCards) yield cards).asInstanceOf[List[Card]]

  //Returns a formated string of the attacks on the table
  def getAttacksOnTableString = {
    var string = ""
    var i = 0
    for (attack <- attacks) {
      string = string + "\n" + i + ": " + attack.toString
      i = i + 1
    }
    string
  }

  def getAttackByIndex(index: Int) = attacks(index)

  def parseToCard(suit: String, rank: String): Card = {
    val cardFactory = injector.getInstance(classOf[CardFactory])
    var card = cardFactory.create(suit, rank)
    if (card != null) {
      if (trumpSuit == card.suit) card = card.setTrump
    }
    card
  }
}