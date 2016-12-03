package de.htwg.se.durak.model

case class Player(name: String, number: Int, cards: List[Card], status: PlayerStatus = PlayerStatus.Inactive) {
  def takeCards(card: Card): Player = this.copy(cards = card :: this.cards)
  def takeCards(cards: List[Card]): Player = this.copy(cards = cards ::: this.cards)
  def putDownCard(card: Card): (Card, Player) = {
    if (!this.cards.contains(card)) throw new IllegalArgumentException("Player does not have this card in his/her hand")
    else (card, this.copy(cards = this.cards.diff(List[Card](card))))
  }
  def isDefender: Boolean = status == PlayerStatus.Defender
  def isAttacker: Boolean = status == PlayerStatus.Attacker
  def setStatus(status: PlayerStatus): Player = this.copy(status = status)
  def numberOfCards: Int = cards.size
  def hasCard(card: Card): Boolean = cards.contains(card)
  override def toString: String = "Player [name: " + name + ", cards: " + cards + ", status: " + status + "]"
}