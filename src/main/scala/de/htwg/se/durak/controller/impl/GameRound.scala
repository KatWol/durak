package de.htwg.se.durak.controller.impl

import de.htwg.se.durak.controller.GameRoundController
import de.htwg.se.durak.controller.impl.game.GameNotFinished
import de.htwg.se.durak.controller.impl.game.GameState
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Deck
import de.htwg.se.durak.model.Player
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Rank
import de.htwg.se.util.Observer

class GameRound(playerNames: List[String] = List[String]("Kathrin", "Jakob"), startWithRank: Rank = Rank.Seven, startWithSmallestTrump: Boolean = true) extends GameRoundController {
  val startRank = startWithRank //Um auf den Wert in GameFinished zugreifen zu können
  //*********** BEGINN KONSTRUKTOR ***********

  //1. Deck wird beginnend ab Rank startWithRank instanziert mit gemischten Karten
  var deck: Deck = getDeck(startWithRank)
  //2. Spieler werden über Namens-Liste und bereits instanziertem Deck instanziert
  val allPlayers = getPlayers(playerNames) //Enthält eine Kopie aller Spieler, um diese bei einer neuen Runde wieder richtig zu setzen
  val temp = dealCards(allPlayers) //Enthält nur die aktiven Spieler, die die Runde noch nicht beendet haben
  deck = temp._2
  var activePlayers = temp._1
  //3. Trump-Card wird definiert (--> d.h. gezogen, nach hintengeschoben und "aufgedeckt") und isTrump wird für alle Karten gesetzt
  deck = deck.defineTrumpCard
  val trumpSuit = deck.getTrumpSuit
  //4. nachdem Trump-Suit feststeht, wird Trumpffarbe der Karten der Speiler aktualisiert
  activePlayers = for (player <- this.activePlayers) yield { player.setTrumpSuit(deck.getTrumpSuit) }
  //5. Pro Spieler wird kleinste TrumpCard ermittelt, Spielerstatus gesetzt und Startspieler bestimmt
  activePlayers = setPlayerStatusForNextRound(getPlayerWithSmallestTrumpCard)

  //6. Status des Spiels wird gesetzt
  var state: GameState = new GameNotFinished
  //7. Runde wird gestartet 
  var round = new Round(this.deck, this.activePlayers, this.trumpSuit, subscribers)

  //*********** ENDE KONSTRUKTOR ***********

  var statusLine = "Start of a new game"

  //*********** Methoden die auf den StateObjekten aufgerufen werden *********

  //Führt alle notwendigen Aktionen des aktuellen Status aus und gibt dann einen boolschen Wert zurück, ob etwas geändert wurde
  override def updateGameRound = state.updateState(this)

  //*********** Implementierung des Traits
  override def playCard(suit: String, rank: String, attack: String) = round.playCard(suit, rank, attack)
  override def endTurn = round.endTurn
  override def addSubscriberToRound(s: Observer) = round.add(s)

  //**** Methoden um den aktuellen Stand in der View anzuzeigen *****

  //Setzt den Status zu einem leeren String nachdem er zurück gegeben wurde
  override def getGameStatus = {
    val tempStatus = statusLine
    statusLine = ""
    tempStatus
  }
  override def getRoundStatus = round.statusLine
  override def getCurrentPlayerName = round.getCurrentPlayer.name
  override def getCurrentPlayerStatus = round.getCurrentPlayer.status.toString
  override def getNumberOfCardsInDeck = round.deck.numberOfCards.toString
  override def getCurrentPlayersCardsString = round.getCurrentPlayer.printCards
  override def getCurrentPlayersCards = round.getCurrentPlayer.cards
  override def getAttacksOnTableString = round.getAttacksOnTableString
  override def getAttacksOnTable = round.attacks

  //*********** Sonstige Methoden ***********************
  def changeState(state: GameState) = this.state = state;

  def getDeck(startWithRank: Rank): Deck = new Deck(startWithRank)

  def getPlayers(playerNames: List[String]): List[Player] = {
    var i = -1
    for (name <- playerNames) yield {
      i = i + 1
      Player(name, i, List[Card](), PlayerStatus.Inactive)
    }
  }

  def dealCards(allPlayers: List[Player]) = {
    var newDeck = deck
    val players = for (player <- allPlayers) yield {
      val temp = newDeck.drawNCards(6)
      newDeck = temp._2
      player.copy(cards = temp._1)
    }
    (players, newDeck)
  }

  def getPlayerWithSmallestTrumpCard: Int = {
    //1. Spieler werden durchlaufen und ein Tuple2 mit Index des Spielers und kleinster Trumpfkarte erstellt
    //2. Über .minBy(_._2) wird der Tuple2 mit kleinster Trumpfkarte ermittelt
    //3. Von dem ermittelten Tuple2 wird die erste Komponente, welche den Index des Spielers enthält zurückgegeben
    ((for (i <- 0 to (activePlayers.size - 1); if activePlayers(i).getSmallestTrumpCard != null) yield Tuple2(i, activePlayers(i).getSmallestTrumpCard)).minBy(_._2))._1
  }

  //Rückgabe einer Liste mit Player mit der Startaufstellung für Beginn-Strategie "Kleinster Trumpf"
  def setPlayerStatusForNextRound(index: Int): List[Player] = {
    (for (i <- 0 to (activePlayers.size - 1)) yield {
      if (i == index % activePlayers.size) activePlayers(i).copy(status = PlayerStatus.Attacker, hisTurn = true) //Erster Angreifer
      else if (i == (index + 1) % activePlayers.size) activePlayers(i).copy(status = PlayerStatus.Defender, hisTurn = false) //Verteidiger
      else if (i == (index + 2) % activePlayers.size) activePlayers(i).copy(status = PlayerStatus.Attacker, hisTurn = false) //Zweiter Angreifer
      else activePlayers(i).copy(status = PlayerStatus.Inactive, hisTurn = false)
    }).toList
  }

}