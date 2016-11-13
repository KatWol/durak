package de.htwg.se.durak.model

case class Player(name: String, number: Int, cards: List[Card], defender: Boolean = false, attacker: Boolean = false) {
  if (defender == true && attacker == true) throw new IllegalArgumentException("A player can only be attacker or defender")
  def takeCard(card: Card): Player = this.copy(cards = card :: this.cards)
  def putDownCard(card: Card): (Card, Player) = {
    if (this.cards.contains(card)) (card, this.copy(cards = cards.filterNot(c => c == card)))
    else throw new IllegalArgumentException("Player does not have this card in his/her hand")    
  }
  def isDefender: Boolean = defender
  def isAttacker: Boolean = attacker
  def makeDefender: Player = this.copy(defender = true, attacker = false)
  def makeAttacker: Player = this.copy(defender = false, attacker = true)
  override def toString: String = "Player [name: " + name + ", number: " + number + ", cards: " + cards + ", defender: " + defender + ", attacker: " + attacker + "]"
}