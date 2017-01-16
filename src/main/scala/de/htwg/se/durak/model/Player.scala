package de.htwg.se.durak.model

case class Player(name: String, number: Int, cards: List[Card], status: PlayerStatus = PlayerStatus.Inactive, hisTurn: Boolean = false) {
  def takeCards(card: Card): Player = this.copy(cards = card :: this.cards)
  def takeCards(cards: List[Card]): Player = this.copy(cards = cards ::: this.cards)
  def putDownCard(card: Card): Player = {
    if (!this.cards.contains(card)) throw new IllegalArgumentException("Player does not have this card in his/her hand")
    this.copy(cards = this.cards.diff(List[Card](card)))
  }
  def isDefender: Boolean = status == PlayerStatus.Defender
  def isAttacker: Boolean = status == PlayerStatus.Attacker
  def setStatus(status: PlayerStatus): Player = this.copy(status = status)
  def changeTurn(): Player = this.copy(hisTurn = !hisTurn)
  def setTurn(hisTurn: Boolean): Player = this.copy(hisTurn = hisTurn)
  def numberOfCards: Int = cards.size
  def hasCard(card: Card): Boolean = cards.contains(card)
  override def toString: String = "Player [name: " + name + ", number: " + number + ", cards: " + cards + ", status: " + status + ", hisTurn: " + hisTurn + "]"
  def setTrumpSuit(trumpSuit: Suit): Player = this.copy(cards = (for (card <- cards) yield { card.copy(isTrump = card.suit == trumpSuit) }))
  def getSmallestTrumpCard = {
    val trumpCards = for (card <- cards if card.isTrump) yield card
    if (trumpCards.size != 0) trumpCards.min
    else null
  }

  def printCards = {
    var string = ""
    for (card <- cards) {
      string = string + "\n" + card
    }
    string
  }
}