package de.htwg.se.durak.model

case class Game(trump: String, players: Set[Player], cards: List[Card], attacks: List[Attack]) 