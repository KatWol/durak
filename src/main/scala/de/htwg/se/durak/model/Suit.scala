package de.htwg.se.durak.model

sealed trait Suit {
  def value: String;
  override def toString = value
}

object Suit {
  case object Clubs extends Suit {val value="clubs"}
  case object Spades extends Suit {val value="spades"}
  case object Hearts extends Suit {val value="hearts"}
  case object Diamonds extends Suit {val value="diamonds"}
  
  val suits = List(Clubs, Spades, Hearts, Diamonds);
}