package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class Game(playerNames: List[String] = List[String]("Kathrin", "Jakob"), startWithCard: Rank = Rank.Seven, startWithSmallestTrump: Boolean) {

  //Deck wird instanziert
  val trumpSuit: Suit = Suit.getRandomTrumpSuit
  var deck: Deck = new Deck(startWithCard, trumpSuit)

  //Spieler werden instanziert und bekommen Karten
  var players: List[Player] = List[Player]()
  for (name <- playerNames) {
    val temp = deck.drawNCards(6)
    deck = temp._2
    players :+ Player(name, 1, temp._1, PlayerStatus.Inactive)
  }

  //Spielerreihenfolge wird gesetzt und Startspieler bestimmmt
  var temp: List[Player] = List[Player]()
  for (rank <- Rank.values if rank >= startWithCard; player <- players) {
    if (player.cards.contains(Card(trumpSuit, rank, true))) {
      temp :+ player.setStatus(PlayerStatus.Attacker)
    } else {
      temp :+ player
    }
  }
  players = temp

}