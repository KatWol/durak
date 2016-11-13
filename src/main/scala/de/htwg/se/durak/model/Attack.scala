package de.htwg.se.durak.model

case class Attack(attackingCard: Card, defendingCard: Card = null) {
  def isCompleted: Boolean = if (defendingCard == null) false else true
  def isSameSuit: Boolean = if(isCompleted) this.attackingCard.isSameSuit(defendingCard) else false
  def defend(defendingCard: Card): Attack = {
    if (!isCompleted) {
      if (!this.attackingCard.isSameSuit(defendingCard)) this.copy(defendingCard = defendingCard)
      else if (defendingCard > this.attackingCard) this.copy(defendingCard = defendingCard)
      else throw new IllegalArgumentException("This is not a legal card")
    }     
    else throw new IllegalArgumentException("This attack is already completed")
  }
  override def toString = "Attack [attackingCard: " + attackingCard + ", defendingCard: " + defendingCard + "]"
}