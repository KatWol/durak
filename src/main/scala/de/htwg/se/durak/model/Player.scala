package de.htwg.se.durak.model

trait Player {
  val name: String
  val number: Int
  val cards: List[Card]
  val status: PlayerStatus = PlayerStatus.Inactive
  val hisTurn: Boolean = false

  def takeCards(card: Card): Player
  def takeCards(card: List[Card]): Player
  def putDownCard(card: Card): Player
  def isDefender: Boolean
  def isAttacker: Boolean
  def setStatus(status: PlayerStatus): Player
  def changeTurn: Player
  def setTurn(hisTurn: Boolean): Player
  def numberOfCards: Int
  def hasCard(card: Card): Boolean
  def toString: String
  def setTrumpSuit(trumpSuit: Suit): Player
  def setCards(cards: List[Card]): Player
  def getSmallestTrumpCard: Card
  def printCards: String
  def toXml: scala.xml.Node
}

trait PlayerFactory {
  def create(name: String, number: Int, cards: List[Card]): Player
}