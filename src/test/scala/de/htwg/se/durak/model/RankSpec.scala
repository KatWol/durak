package de.htwg.se.durak.model

import org.scalatest._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RankSpec extends WordSpec with Matchers {
  "A Rank" should {

    "only have allowed values" in {
      val allowedRanks = Array("two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "jack", "queen", "king", "ace")
      Rank.values.foreach { e => allowedRanks.contains(e.toString) should be(true) }

      val notAllowedRanks = Array("one", "seventeen")
      Rank.values.foreach { e => notAllowedRanks.contains(e.toString) should be(false) }
    }

    "have a pointValue" in {
      Rank.Ace.pointValue should be > (Rank.King.pointValue)
      Rank.King.pointValue should be > (Rank.Queen.pointValue)
      Rank.Queen.pointValue should be > (Rank.Jack.pointValue)
      Rank.Jack.pointValue should be > (Rank.Ten.pointValue)
      Rank.Ten.pointValue should be > (Rank.Nine.pointValue)
      Rank.Nine.pointValue should be > (Rank.Eight.pointValue)
      Rank.Eight.pointValue should be > (Rank.Seven.pointValue)
      Rank.Seven.pointValue should be > (Rank.Six.pointValue)
      Rank.Six.pointValue should be > (Rank.Five.pointValue)
      Rank.Five.pointValue should be > (Rank.Four.pointValue)
      Rank.Four.pointValue should be > (Rank.Three.pointValue)
      Rank.Three.pointValue should be > (Rank.Two.pointValue)
    }

    "have an order" in {
      Rank.Ace > Rank.King should be(true)
      Rank.King > Rank.Queen should be(true)
      Rank.Queen > Rank.Jack should be(true)
      Rank.Jack > Rank.Ten should be(true)
      Rank.Ten > Rank.Nine should be(true)
      Rank.Nine > Rank.Eight should be(true)
      Rank.Eight > Rank.Seven should be(true)
      Rank.Seven > Rank.Six should be(true)
      Rank.Six > Rank.Five should be(true)
      Rank.Five > Rank.Four should be(true)
      Rank.Four > Rank.Three should be(true)
      Rank.Three > Rank.Two should be(true)

      Rank.Two > Rank.Three should be(false)
      Rank.Nine > Rank.Nine should be(false)
      Rank.Four > Rank.Ace should be(false)

      Rank.Five < Rank.Four should be(false)
      Rank.Four < Rank.Three should be(false)
      Rank.Three < Rank.Two should be(false)

      Rank.Two < Rank.Three should be(true)
      Rank.Nine < Rank.Nine should be(false)
      Rank.Four < Rank.Ace should be(true)
    }

    "have a string representation" in {
      Rank.Ace.toString should be("ace")
      Rank.Two.toString should be("two")
    }
  }
}