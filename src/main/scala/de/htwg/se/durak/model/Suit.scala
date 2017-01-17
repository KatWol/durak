package de.htwg.se.durak.model

import scala.util.Random

sealed trait Suit {
  def value: String;
  override def toString = value
}

object Suit {
  case object Clubs extends Suit { val value = "clubs" }
  case object Spades extends Suit { val value = "spades" }
  case object Hearts extends Suit { val value = "hearts" }
  case object Diamonds extends Suit { val value = "diamonds" }
  val values = List(Clubs, Spades, Hearts, Diamonds);

  def parseFromString(string: String) = {
    string match {
      case "Hearts" | "hearts" | "h" => Suit.Hearts
      case "Diamonds" | "diamonds" | "d" => Suit.Diamonds
      case "Spades" | "spades" | "s" => Suit.Spades
      case "Clubs" | "clubs" | "c" => Suit.Clubs
      case _ => null
    }
  }

}