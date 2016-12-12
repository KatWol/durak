package de.htwg.se.durak.model

case class Attack(attackingCard: Card, defendingCard: Card = null) {

  //Fügt dem Attack eine Karte hinzu, falls dieser noch nicht beendet und die Karte nach den Regeln gültig ist
  def defend(card: Card): Attack = {
    if (isValid(card)) this.copy(defendingCard = card)
    else if (isCompleted) throw new IllegalArgumentException("This attack is already completed")
    else throw new IllegalArgumentException("Invalid card for this attack")
  }

  def isCompleted: Boolean = defendingCard != null

  //Überprüft, ob die Karte eine gültige Karte ist, um den Attack zu verteidigen
  def isValid(card: Card): Boolean = {
    if (isCompleted) false
    else if (card <= attackingCard) false
    else if (!(attackingCard.isSameSuit(card)) && !card.isTrump) false
    else true
  }

  //Gibt eine Liste aller Karten in dem Attack zurück
  def getCards: List[Card] = {
    if (defendingCard != null) List[Card](attackingCard, defendingCard)
    else List[Card](attackingCard)
  }
}