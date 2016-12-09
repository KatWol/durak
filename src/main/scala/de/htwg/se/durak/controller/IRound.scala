package de.htwg.se.durak.controller
import de.htwg.se.durak.model._

trait IRound {
  def startNextRound(deck: Deck, players: List[Player]): Tuple2[Deck, List[Player]]
}