package de.htwg.se.durak.controller.impl

import scala.swing.event.Event

import com.google.inject.Guice
import de.htwg.se.durak.controller.impl.round.RoundNotFinished
import de.htwg.se.durak.controller.GameRoundControllerFactory
import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.controller.GameRoundController
import de.htwg.se.durak.model.Player
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Deck
import de.htwg.se.util.Observer
import de.htwg.se.durak.model.PlayerFactory
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.DeckFactory
import de.htwg.se.durak.controller.impl.round.RoundFinished

case class RoundFinishedEvent() extends Event
case class GameRoundFinishedEvent() extends Event

class GameRound(val playerNames: List[String] = List[String]("Kathrin", "Jakob"), val startWithRank: Rank = Rank.Seven, startWithSmallestTrump: Boolean = true) extends GameRoundController with Observer {
  val injector = Guice.createInjector(new DurakModule())
  //*********** BEGINN KONSTRUKTOR ***********

  //1. Deck wird beginnend ab Rank startWithRank instanziert mit gemischten Karten

  val deckFactory = injector.getInstance(classOf[DeckFactory])
  var deck: Deck = getDeck(startWithRank)
  //2. Spieler werden über Namens-Liste und bereits instanziertem Deck instanziert
  val allPlayers = getPlayers(playerNames) //Enthält eine Kopie aller Spieler, um diese bei einer neuen Runde wieder richtig zu setzen
  val temp = dealCards(allPlayers) //Enthält nur die aktiven Spieler, die die Runde noch nicht beendet haben
  deck = temp._2
  var activePlayers = temp._1
  //3. Trump-Card wird definiert (--> d.h. gezogen, nach hintengeschoben und "aufgedeckt") und isTrump wird für alle Karten gesetzt
  deck = deck.defineTrumpCard
  var trumpSuit = deck.getTrumpSuit
  var trumpCard = deck.getLastCardOfDeck
  //4. nachdem Trump-Suit feststeht, wird Trumpffarbe der Karten der Speiler aktualisiert
  activePlayers = for (player <- this.activePlayers) yield { player.setTrumpSuit(deck.getTrumpSuit) }
  //5. Pro Spieler wird kleinste TrumpCard ermittelt, Spielerstatus gesetzt und Startspieler bestimmt
  activePlayers = setPlayerStatusForNextRound(getPlayerWithSmallestTrumpCard)
  //6. Runde wird gestartet 
  var round = new Round(this.deck, this.activePlayers, this.trumpSuit)
  round.add(this)

  //*********** ENDE KONSTRUKTOR ***********

  var statusLine = "Start of a new game"
  var durakLastGameRound: String = ""
  var defenderLostLastRound: Boolean = true

  def updateRound = {
    round.state match {
      case finished: RoundFinished =>
        {
          setupForNextRound
          statusLine = "A new round has started"
          if (isGameRoundFinished) {
            statusLine = "The game round is finished. \n******Durak: " + activePlayers(0).name + "\nStarting a new game round."
            durakLastGameRound = activePlayers(0).name
            startNewGameRound
            notifyObservers(new GameRoundFinishedEvent)
          } else {
            notifyObservers(new RoundFinishedEvent)
          }
        }
    }
  }

  def setupForNextRound = {
    defenderLostLastRound = round.defenderWon
    dealCards
    deck = round.deck
    activePlayers = round.players
    removeFinishedPlayers
    val indexOfDefender = round.getIndexOfPlayer(round.getDefender)
    if (round.defenderWon) activePlayers = setPlayerStatusForNextRound(indexOfDefender)
    else activePlayers = setPlayerStatusForNextRound(indexOfDefender + 1)

    round = new Round(deck, activePlayers, trumpSuit)
    round.add(this)
  }

  def dealCards: Unit = {
    round.drawNCards(round.getFirstAttacker, Math.max(6 - round.getFirstAttacker.numberOfCards, 0))
    if (round.getSecondAttacker != null) round.drawNCards(round.getSecondAttacker, Math.max(6 - round.getSecondAttacker.numberOfCards, 0))
    if (round.defenderWon) round.drawNCards(round.getDefender, Math.max(6 - round.getDefender.numberOfCards, 0))
    else pickUpAllCardsOnTable
  }

  //Defender gets all cards on the table
  def pickUpAllCardsOnTable = round.updatePlayer(round.getDefender, round.getDefender.takeCards(round.getCardsOnTable))

  //Removes all players without cards
  def removeFinishedPlayers = activePlayers = for (player <- round.players; if player.numberOfCards > 0) yield player

  def isGameRoundFinished: Boolean = {
    if (deck.isEmpty && activePlayers.size < 2) true
    else false
  }

  def startNewGameRound = {
    deck = getDeck(startWithRank)
    val indexNextStartPlayer = getNextStartPlayer
    val temp = dealCards(allPlayers)
    activePlayers = temp._1
    deck = temp._2

    deck = deck.defineTrumpCard
    trumpSuit = deck.getTrumpSuit
    trumpCard = deck.getLastCardOfDeck
    //4. nachdem Trump-Suit feststeht, wird Trumpffarbe der Karten der Speiler aktualisiert
    activePlayers = for (player <- this.activePlayers) yield { player.setTrumpSuit(deck.getTrumpSuit) }
    activePlayers = setPlayerStatusForNextRound(indexNextStartPlayer)
    round = new Round(deck, activePlayers, trumpSuit)
    round.add(this)
  }

  def getNextStartPlayer: Int = {
    val nextStartPlayer = getNextDefender - 1
    if (nextStartPlayer >= 0) nextStartPlayer
    else nextStartPlayer + allPlayers.size
  }

  def getNextDefender: Int = activePlayers(0).number

  //*********** Implementierung des Traits ************************************************************
  override def playCard(suit: String, rank: String, attack: String) = round.playCard(suit, rank, attack)
  override def endTurn = round.endTurn
  override def addSubscriberToRound(s: Observer) = round.add(s)
  override def undo = {
    try {
      round.commandManager.undo
    } catch {
      case e: IllegalStateException => statusLine = e.getMessage
    }

    update
  }
  override def redo = {
    try {
      round.commandManager.redo
    } catch {
      case e: IllegalStateException => statusLine = e.getMessage
    }
    update
  }

  //**** Methoden um den aktuellen Stand in der View anzuzeigen *****

  override def getGameStatus = statusLine
  override def getRoundStatus = round.statusLine
  override def getCurrentPlayerName = round.getCurrentPlayer.name
  override def getCurrentPlayerStatus = round.getCurrentPlayer.status.toString
  override def getNumberOfCardsInDeck = round.deck.numberOfCards.toString
  override def getCurrentPlayersCardsString = round.getCurrentPlayer.printCards
  override def getCurrentPlayersCards = round.getCurrentPlayer.cards
  override def getAttacksOnTableString = round.getAttacksOnTableString
  override def getAttacksOnTable = round.attacks
  override def getLastCardFromDeck = trumpCard
  //Macht nur Sinn, wenn die GameRound beendet wurde
  override def getDurakName: String = durakLastGameRound
  override def getDefenderLost: Boolean = defenderLostLastRound

  //******************************* Sonstige Methoden ***********************

  def getDeck(startWithRank: Rank) = deckFactory.create(startWithRank)

  def getPlayers(playerNames: List[String]): List[Player] = {
    val playerFactory = injector.getInstance(classOf[PlayerFactory])
    var i = -1
    for (name <- playerNames) yield {
      i = i + 1
      playerFactory.create(name, i, List[Card]())
    }
  }

  def dealCards(allPlayers: List[Player]): Tuple2[List[Player], Deck] = {
    var newDeck = deck
    val players: List[Player] = for (player <- allPlayers) yield {
      val temp = newDeck.drawNCards(6)
      newDeck = temp._2
      player.setCards(temp._1)
    }
    (players, newDeck)
  }

  def getPlayerWithSmallestTrumpCard: Int = {
    //1. Spieler werden durchlaufen und ein Tuple2 mit Index des Spielers und kleinster Trumpfkarte erstellt
    //2. Über .minBy(_._2) wird der Tuple2 mit kleinster Trumpfkarte ermittelt
    //3. Von dem ermittelten Tuple2 wird die erste Komponente, welche den Index des Spielers enthält zurückgegeben
    val temp: IndexedSeq[Tuple2[Int, Card]] = for (i <- 0 to (activePlayers.size - 1); if activePlayers(i).getSmallestTrumpCard != null) yield { Tuple2(i, activePlayers(i).getSmallestTrumpCard) }
    if (!temp.isEmpty) (temp.minBy(_._2))._1
    else 0
  }

  //Rückgabe einer Liste mit Player mit der Startaufstellung für Beginn-Strategie "Kleinster Trumpf"
  def setPlayerStatusForNextRound(index: Int): List[Player] = {
    (for (i <- 0 to (activePlayers.size - 1)) yield {
      if (i == index % activePlayers.size) activePlayers(i).setStatus(PlayerStatus.Attacker).setTurn(true) //Erster Angreifer
      else if (i == (index + 1) % activePlayers.size) activePlayers(i).setStatus(PlayerStatus.Defender).setTurn(false) //Verteidiger
      else if (i == (index + 2) % activePlayers.size) activePlayers(i).setStatus(PlayerStatus.Attacker).setTurn(false) //Zweiter Angreifer
      else activePlayers(i).setStatus(PlayerStatus.Inactive).setTurn(false)
    }).toList
  }

  override def update = notifyObservers
  override def update(e: Event) = updateRound

}

class GameRoundFactory extends GameRoundControllerFactory {
  override def create(playerNames: List[String], startWithRank: String = "jack", startWithSmallestTrump: Boolean = true): GameRound = new GameRound(playerNames, Rank.parseFromString(startWithRank), startWithSmallestTrump)
}