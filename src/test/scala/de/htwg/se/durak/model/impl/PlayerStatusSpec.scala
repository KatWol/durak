package de.htwg.se.durak.model.impl

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import de.htwg.se.durak.model.PlayerStatus;

@RunWith(classOf[JUnitRunner])
class PlayerStatusSpec extends WordSpec with Matchers {
  "A PlayerStatus" should {
    "only have allowed values" in {
      val allowedPlayerStatuses = Array("Attacker", "Defender", "Inactive")
      PlayerStatus.values.foreach { e => allowedPlayerStatuses.contains(e.toString) should be(true) }

      val notAllowedPlayerStatuses = Array("player")
      PlayerStatus.values.foreach { e => notAllowedPlayerStatuses.contains(e.toString) should be(false) }
    }

    "have a string representation" in {
      PlayerStatus.Attacker.toString should be("Attacker")
      PlayerStatus.Defender.toString should be("Defender")
    }

    "be parsed from a String" in {
      PlayerStatus.parseFromString("attacker") should be(PlayerStatus.Attacker)
      PlayerStatus.parseFromString("Attacker") should be(PlayerStatus.Attacker)
      PlayerStatus.parseFromString("Defender") should be(PlayerStatus.Defender)
      PlayerStatus.parseFromString("defender") should be(PlayerStatus.Defender)
      PlayerStatus.parseFromString("Inactive") should be(PlayerStatus.Inactive)
      PlayerStatus.parseFromString("inactive") should be(PlayerStatus.Inactive)
      PlayerStatus.parseFromString("hello") should be(null)
    }
  }
}