package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PlayerStatusSpec extends WordSpec with Matchers {
  "A PlayerStatus" should {
    "only have allowed values" in {
      val allowedPlayerStatuses = Array("attacker", "defender", "inactive")
      PlayerStatus.values.foreach { e => allowedPlayerStatuses.contains(e.toString) should be(true) }

      val notAllowedPlayerStatuses = Array("player")
      PlayerStatus.values.foreach { e => notAllowedPlayerStatuses.contains(e.toString) should be(false) }
    }

    "have a string representation" in {
      PlayerStatus.Attacker.toString should be("attacker")
      PlayerStatus.Defender.toString should be("defender")
    }
  }
}