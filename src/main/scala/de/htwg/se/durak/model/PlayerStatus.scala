package de.htwg.se.durak.model

sealed trait PlayerStatus {
  def value: String;
  override def toString = value
}

object PlayerStatus {
  case object Attacker extends PlayerStatus { val value = "attacker" }
  case object Defender extends PlayerStatus { val value = "defender" }
  case object Inactive extends PlayerStatus { val value = "inactive" }
  
  val playerStatuses = List(Attacker, Defender, Inactive)
}