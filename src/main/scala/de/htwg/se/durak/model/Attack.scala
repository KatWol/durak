package de.htwg.se.durak.model

trait Attack {
  val attackingCard: Card
  val defendingCard: Card = null
  def defend(card: Card): Attack
  def isCompleted: Boolean
  def getCards: List[Card]
  def toString: String
  def resetDefendingCard: Attack
  def toXml: scala.xml.Node
}

trait AttackFactory {
  def create(attackingCard: Card, defendingCard: Card = null): Attack
}