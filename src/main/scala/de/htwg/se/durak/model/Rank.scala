package de.htwg.se.durak.model

sealed trait Rank {
  def value: String;
  def pointValue: Int
  override def toString = value
  def >(that: Rank): Boolean = this.pointValue > that.pointValue
  def <(that: Rank): Boolean = this.pointValue < that.pointValue
}

object Rank {
  case object Ace extends Rank { val value = "ace"; val pointValue = 14 }
  case object King extends Rank { val value = "king"; val pointValue = 13 }
  case object Queen extends Rank { val value = "queen"; val pointValue = 12 }
  case object Jack extends Rank { val value = "jack"; val pointValue = 11 }
  case object Ten extends Rank { val value = "ten"; val pointValue = 10 }
  case object Nine extends Rank { val value = "nine"; val pointValue = 9 }
  case object Eight extends Rank { val value = "eight"; val pointValue = 8 }
  case object Seven extends Rank { val value = "seven"; val pointValue = 7 }
  case object Six extends Rank { val value = "six"; val pointValue = 6 }
  case object Five extends Rank { val value = "five"; val pointValue = 5 }
  case object Four extends Rank { val value = "four"; val pointValue = 4 }
  case object Three extends Rank { val value = "three"; val pointValue = 3 }
  case object Two extends Rank { val value = "two"; val pointValue = 2 }

  val ranks = List(Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Jack, Queen, King, Ace)
}
