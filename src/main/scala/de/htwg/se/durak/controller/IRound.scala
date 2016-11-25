package de.htwg.se.durak.controller
import de.htwg.se.durak.model._


trait IRound  {
  def IRound(deck: Deck, players: List[Player], activePlayer: Player): Tuple3[Deck, List[Player], Player]  
}