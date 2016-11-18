package de.htwg.se.durak.model

case class Player(name: String, number: Int, cards: List[Card], status: PlayerStatus = PlayerStatus.Inactive) {
  def takeCard(card: Card): Player = this.copy(cards = card :: this.cards)
  def putDownCard(card: Card): (Card, Player) = {
    if (this.cards.contains(card)) (card, this.copy(cards = cards.filterNot(c => c == card)))
    else throw new IllegalArgumentException("Player does not have this card in his/her hand")
  }
  def isDefender: Boolean = status == PlayerStatus.Defender
  def isAttacker: Boolean = status == PlayerStatus.Attacker
  def makeDefender: Player = this.copy(status = PlayerStatus.Defender)
  def makeAttacker: Player = this.copy(status = PlayerStatus.Attacker)
  override def toString: String = "Player [name: " + name + ", number: " + number + ", cards: " + cards + ", status: " + status + "]"
}