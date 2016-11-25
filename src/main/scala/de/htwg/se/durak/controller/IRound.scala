package de.htwg.se.durak.controller
import de.htwg.se.durak.model._


trait IRound  {
  def IRound(deck: Deck, players: List[Player], activePlayerNumber: Int): Tuple3[Deck, List[Player], Int]  
}