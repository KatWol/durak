package de.htwg.se.durak.model

import scala.util.Random

case class Deck(cards: List[Card]) {
  def this(startWith: Rank, trumpSuit: Suit) = {
    this(Deck.getInitialCards(startWith, trumpSuit))
    this.shuffle
  }
  def isEmpty: Boolean = numberOfCards == 0
  def numberOfCards: Int = this.cards.size
  def drawNCards(n: Int): Tuple2[List[Card], Deck] = {
    if (n <= numberOfCards) new Tuple2(cards.slice(0, n), new Deck(cards.slice(n, this.numberOfCards)))
    else throw new IndexOutOfBoundsException("Not enough cards in the deck")
  }
  def shuffle: Deck = Deck(Random.shuffle(cards))
  override def toString: String = "Deck [size: " + numberOfCards + "]"
  def print: Unit = cards.foreach { println }
}

object Deck {
  def getInitialCards(startWith: Rank, trumpSuit: Suit): List[Card] = {
    for (suit <- Suit.values; rank <- Rank.values if rank >= startWith) yield Card(suit, rank, trumpSuit == suit)
  }
}