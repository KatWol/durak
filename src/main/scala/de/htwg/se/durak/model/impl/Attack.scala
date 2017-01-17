package de.htwg.se.durak.model.impl

import de.htwg.se.durak.model.{ Card => CardTrait }
import de.htwg.se.durak.model.{ AttackFactory => AttackFactoryTrait }

case class Attack(override val attackingCard: Card, override val defendingCard: Card = null) extends de.htwg.se.durak.model.Attack {

  //Fügt dem Attack eine Karte hinzu, falls dieser noch nicht beendet und die Karte nach den Regeln gültig ist
  override def defend(card: CardTrait): Attack = {
    val internalCard = card.asInstanceOf[Card]
    if (!isValid(internalCard)) throw new IllegalArgumentException("This card is not valid for this attack")
    if (isCompleted) throw new IllegalArgumentException("This attack is already completed")
    this.copy(defendingCard = internalCard)
  }

  override def isCompleted: Boolean = defendingCard != null

  //Überprüft, ob die Karte eine gültige Karte ist, um den Attack zu verteidigen
  def isValid(card: Card): Boolean = card > attackingCard

  //Gibt eine Liste aller Karten in dem Attack zurück
  override def getCards: List[de.htwg.se.durak.model.Card] = List[de.htwg.se.durak.model.Card](attackingCard, defendingCard).filter(card => card != null)
  override def toString: String = "[attackingCard: " + attackingCard + ", defendingCard: " + defendingCard + ", isCompleted: " + isCompleted + "]"

  override def resetDefendingCard = this.copy(defendingCard = null)
  override def toXml: scala.xml.Node = {
    if (isCompleted) <attack><attackingCard>{ attackingCard.toXml }</attackingCard><defendingCard>{ defendingCard.toXml }</defendingCard></attack>
    else <attack><attackingCard>{ attackingCard.toXml }</attackingCard><defendingCard>null</defendingCard></attack>
  }

}

object Attack {
  def fromXml(node: scala.xml.Node) = {
    val attackingCard = Card.fromXml((node \ "attackingCard" \ "card").head)
    if ((node \ "defendingCard").text != "null") {
      val defendingCard = Card.fromXml((node \ "defendingCard" \ "card").head)
      Attack(attackingCard, defendingCard)
    } else Attack(attackingCard, null)
  }
}

class AttackFactory extends AttackFactoryTrait {
  override def create(attackingCard: CardTrait, defendingCard: CardTrait): Attack = Attack(attackingCard.asInstanceOf[Card], defendingCard.asInstanceOf[Card])
}