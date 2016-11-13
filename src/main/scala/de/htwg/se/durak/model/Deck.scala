package de.htwg.se.durak.model

import scala.util.Random

case class Deck(cards: List[Card]) {
  def isEmpty: Boolean = size == 0
  def size: Int = this.cards.size
  def drawNCards(n: Int): Tuple2[List[Card], Deck] = {
    if (n <= size) new Tuple2(cards.slice(0, n), new Deck(cards.slice(n, this.size)))
    else throw new IndexOutOfBoundsException("Not enough cards in the deck")
  }
  def shuffle: Deck = Deck(Random.shuffle(cards))
  override def toString: String = "Deck [size: " + size + "]"
}

object Deck {
  val fullDurakDeck = Deck(for (s <- Suit.suits; r <- Rank.ranks if r.pointValue > 5) yield Card(s, r))
}