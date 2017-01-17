package de.htwg.se.durak.model.impl

import org.junit.runner.RunWith
import org.scalatest._
import org.scalatest.junit.JUnitRunner

import de.htwg.se.durak.model.Suit

@RunWith(classOf[JUnitRunner])
class SuitSpec extends WordSpec with Matchers {
  "A Suit" should {
    "only have allowed values" in {
      val allowedSuits = Array("clubs", "spades", "hearts", "diamonds")
      Suit.values.foreach { e => allowedSuits.contains(e.toString) should be(true) }

      val notAllowedSuits = Array("red", "black")
      Suit.values.foreach { e => notAllowedSuits.contains(e.toString) should be(false) }
    }
  }
}