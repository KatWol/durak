package de.htwg.se.durak.model.impl

import de.htwg.se.durak.model.{ Card => CardTrait }
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Suit
import de.htwg.se.durak.model.{ Player => PlayerTrait }
import de.htwg.se.durak.model.{ PlayerFactory => PlayerFactoryTrait }

case class Player(override val name: String, override val number: Int, override val cards: List[Card], override val status: PlayerStatus = PlayerStatus.Inactive, override val hisTurn: Boolean = false) extends PlayerTrait {
  override def takeCards(card: CardTrait): Player = {
    val internalCard = card.asInstanceOf[Card]
    this.copy(cards = internalCard :: this.cards)
  }
  override def takeCards(cards: List[CardTrait]): Player = {
    val internalCards = cards.asInstanceOf[List[Card]]
    this.copy(cards = internalCards ::: this.cards)
  }
  override def putDownCard(card: CardTrait): Player = {
    val internalCard = card.asInstanceOf[Card]
    if (!this.cards.contains(internalCard)) throw new IllegalArgumentException("Player does not have this card in his/her hand")
    this.copy(cards = this.cards.diff(List[Card](internalCard)))
  }
  override def isDefender: Boolean = status == PlayerStatus.Defender
  override def isAttacker: Boolean = status == PlayerStatus.Attacker
  override def setStatus(status: PlayerStatus): Player = this.copy(status = status)
  override def changeTurn(): Player = this.copy(hisTurn = !hisTurn)
  override def setTurn(hisTurn: Boolean): Player = this.copy(hisTurn = hisTurn)
  override def numberOfCards: Int = cards.size
  override def hasCard(card: CardTrait): Boolean = cards.contains(card)
  override def toString: String = "Player [name: " + name + ", number: " + number + ", cards: " + cards + ", status: " + status + ", hisTurn: " + hisTurn + "]"
  override def setTrumpSuit(trumpSuit: Suit): Player = this.copy(cards = (for (card <- cards) yield { card.copy(isTrump = card.suit == trumpSuit) }))
  override def getSmallestTrumpCard = {
    val trumpCards = for (card <- cards if card.isTrump) yield card
    if (trumpCards.size != 0) {
      Card.min(trumpCards)
    } else null
  }

  override def setCards(cards: List[CardTrait]) = this.copy(cards = cards.asInstanceOf[List[Card]])

  override def printCards = {
    var string = ""
    for (card <- cards) {
      string = string + "\n" + card
    }
    string
  }

  override def toXml = {
    <player><name>{ name }</name><number>{ number }</number><cards>{ cards.map(c => c.toXml) }</cards><status>{ status.toString }</status><hisTurn>{ hisTurn }</hisTurn></player>
  }
}

object Player {
  def fromXml(node: scala.xml.Node) = {
    val name = (node \ "name").text
    val number = (node \ "number").text.toInt
    val statusString = (node \ "status").text
    val status = PlayerStatus.parseFromString(statusString)
    val hisTurn = (node \ "hisTurn").text.toBoolean
    val cardNodes = (node \ "cards" \\ "card")
    if (cardNodes.text != "") {
      val cards = (for (card <- cardNodes) yield (Card.fromXml(card.head))).toList
      Player(name, number, cards, status, hisTurn)
    } else Player(name, number, List[Card](), status, hisTurn)

  }
}

class PlayerFactory extends PlayerFactoryTrait {
  override def create(name: String, number: Int, cards: List[CardTrait]) = new Player(name, number, cards.asInstanceOf[List[Card]])
}