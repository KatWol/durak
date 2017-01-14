package de.htwg.se.durak.controller

import de.htwg.se.durak.model._
import de.htwg.se.durak.controller.game._

class GameRound(playerNames: List[String] = List[String]("Kathrin", "Jakob"), startWithRank: Rank = Rank.Seven, startWithSmallestTrump: Boolean = true) {

  
  //*********** BEGINN KONSTRUKTOR ***********

  //1. Deck wird beginnend ab Rank startWithRank instanziert mit gemischten Karten
  var deck: Deck = getDeck(startWithRank)

  //2. Spieler werden über Namens-Liste und bereits instanziertem Deck instanziert
  val temp = getPlayers(playerNames, deck) //temporärer Wert für Tuple2 mit Spielern und aktualisiertem Deck
  var players: List[Player] = temp._1
  deck = temp._2

  //3. Trump-Card wird definiert (--> d.h. gezogen, nach hintengeschoben und "aufgedeckt") und isTrump wird für alle Karten gesetzt
  deck = deck.defineTrumpCard

  //4. nachdem Trump-Suit feststeht, wird Trumpffarbe der Karten der Speiler aktualisiert
  players = for (player <- players) yield { player.setTrumpSuit(deck.getTrumpSuit) }

  //5. Pro Spieler wird kleinste TrumpCard ermittelt, Spielerstatus gesetzt und Startspieler bestimmt
  players = setPlayerStatusForNextRound(getPlayerWithSmallestTrumpCard)

  //6. Status des Spiels wird gesetzt
  var state: GameState = new InRound
  
  //7. Runde wird gestartet
  var round = new Round(this.deck, this.players)

  //*********** ENDE KONSTRUKTOR ***********

  //*********** Methoden die auf den StateObjekten aufgerufen werden *********
  
  //Führt alle notwendigen Aktionen des aktuellen Status aus und gibt dann einen boolschen Wert zurück, ob der State geändert wurde
  def updateState(game:GameRound) = state.updateState(game)
  
  def changeState(state: GameState) = this.state = state;
  
  
  def getDeck(startWithRank: Rank): Deck = new Deck(startWithRank)

  def getPlayers(playerNames: List[String], deck: Deck): (List[Player], Deck) = {
    var temp = Tuple2(List[Card](), deck)
    var players: List[Player] =
      for (name <- playerNames) yield {
        temp = deck.drawNCards(6)
        Player(name, 1, temp._1, PlayerStatus.Inactive)
      }
    (players, temp._2) // Rückgabe von Tuple2 mit Player-Liste und dem Deck
  }

  def getPlayerWithSmallestTrumpCard: Int = {
    //1. Spieler werden durchlaufen und ein Tuple2 mit Index des Spielers und kleinster Trumpfkarte erstellt
    //2. Über .minBy(_._2) wird der Tuple2 mit kleinster Trumpfkarte ermittelt
    //3. Von dem ermittelten Tuple2 wird die erste Komponente, welche den Index des Spielers enthält zurückgegeben
    ((for (i <- 0 to (players.size - 1)) yield Tuple2(i, players(i).getSmallestTrumpCard)).minBy(_._2))._1
  }

  //Rückgabe einer Liste mit Player mit der Startaufstellung für Beginn-Strategie "Kleinster Trumpf"
  def setPlayerStatusForNextRound(index: Int): List[Player] = {
    (for (i <- 0 to (players.size - 1)) yield {
      if(i == index % players.size) players(i).copy(status = PlayerStatus.Attacker, hisTurn = true) //Erster Angreifer
      else if (i == (index + 1) % players.size) players(i).copy(status = PlayerStatus.Defender, hisTurn = false) //Verteidiger
      else if (i == (index + 2) % players.size) players(i).copy(status = PlayerStatus.Attacker, hisTurn = false) //Zweiter Angreifer
      else players(i).copy(status = PlayerStatus.Inactive, hisTurn = false) 
    }).toList
  }

}