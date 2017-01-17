package de.htwg.se.durak.model

sealed trait PlayerStatus {
  def value: String
  override def toString = value
}

object PlayerStatus {
  case object Attacker extends PlayerStatus { val value = "Attacker" }
  case object Defender extends PlayerStatus { val value = "Defender" }
  case object Inactive extends PlayerStatus { val value = "Inactive" }

  val values = List(Attacker, Defender, Inactive)
  def parseFromString(string: String) = {
    string match {
      case "attacker" | "Attacker" => PlayerStatus.Attacker
      case "defender" | "Defender" => PlayerStatus.Defender
      case "inactive" | "Inactive" => PlayerStatus.Inactive
      case _ => null
    }
  }
}