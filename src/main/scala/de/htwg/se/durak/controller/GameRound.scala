package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

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

  //5. Pro Spieler wird kleisnte TrumpCard ermittelt, Spielerstatus gesetzt und Startspieler bestimmt
  players = setInitalTurn(getPlayerWithSmallestTrumpCard)

  //6. Runde wird gestartet
  //TODO: Ab hier wird jetzt gespielt

  //*********** ENDE KONSTRUKTOR ***********

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
    //1. Spieler werden durchlaufen und ein Tuple2 mit SpielerNr und kleinster Trumpfkarte erstellt
    //2. Über .minBy(_._2) wird der Tuple2 mit kleinster Trumpfkarte ermittelt
    //3. Von dem ermittelten Tuple2 wird die erste Komponente, welche die Spielernummer enthält zurückgegeben
    ((for (player <- players) yield Tuple2(player.number, player.getSmallestTrumpCard)).minBy(_._2))._1
  }

  def getNumberOfPlayers: Int = players.size - 1

  //Rückgabe einer Liste mit Player mit initialen Startaufstellung für Beginn-Strategie "Kleinster Trumpf"
  def setInitalTurn(number: Int): List[Player] = {
    for (player <- players) yield {
      if (player.number == number) player.copy(status = PlayerStatus.Attacker, hisTurn = true) //Erster Angreifer
      else if (player.number == ((number + 1) % getNumberOfPlayers)) player.copy(status = PlayerStatus.Defender, hisTurn = false) //Verteidiger
      else if ((getNumberOfPlayers > 2) && (player.number == ((number + 2) % getNumberOfPlayers))) player.copy(status = PlayerStatus.Attacker, hisTurn = false) //zweiter Angreifer
      else player //restlichen Spieler
    }
  }

}