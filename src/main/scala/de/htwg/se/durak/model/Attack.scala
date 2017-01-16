package de.htwg.se.durak.model

case class Attack(attackingCard: Card, defendingCard: Card = null) {

  //Fügt dem Attack eine Karte hinzu, falls dieser noch nicht beendet und die Karte nach den Regeln gültig ist
  def defend(card: Card): Attack = {
    if (!isValid(card)) throw new IllegalArgumentException("This card is not valid for this attack")
    if (isCompleted) throw new IllegalArgumentException("This attack is already completed")
    this.copy(defendingCard = card)
  }

  def isCompleted: Boolean = defendingCard != null

  //Überprüft, ob die Karte eine gültige Karte ist, um den Attack zu verteidigen
  def isValid(card: Card): Boolean = card > attackingCard

  //Gibt eine Liste aller Karten in dem Attack zurück
  def getCards: List[Card] = List[Card](attackingCard, defendingCard).filter(card => card != null)
  override def toString: String = "[attackingCard: " + attackingCard + ", defendingCard: " + defendingCard + ", isCompleted: " + isCompleted + "]"

  def toXml = {
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