package de.htwg.se.durak.model

case class Attack(attackingCard: Card, defendingCard: Card = null) {
  def defendAttack(card: Card): Attack = {
    if (isValid(card)) this.copy(defendingCard = card)
    else if (isCompleted) throw new IllegalArgumentException("This attack is already completed")
    else throw new IllegalArgumentException("Invalid card for this attack")
  }
  
  def isCompleted: Boolean = defendingCard != null
  
  def isValid(card: Card): Boolean = {
    if (isCompleted) false
    else if (card <= attackingCard) false
    else if (!(attackingCard.isSameSuit(card)) && !card.isTrump) false
    else true
  }
}