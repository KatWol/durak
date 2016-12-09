package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

//hisTurn updaten
//

class Round(var deck: Deck, var players: List[Player], activePlayerNumber: Int) {
  var attacks = List[Attack]()
  var roundFinished = false

  def getActivePlayer(): Player = players.filter(_.number == activePlayerNumber)(0)

  def isRankOnTable(rank: Rank): Boolean = {
    var isRankOnTable = false
    for (attack <- attacks) {
      if (attack.attackingCard.rank == rank) isRankOnTable = true
      if (attack.defendingCard != null && attack.defendingCard.rank == rank) isRankOnTable = true
    }
    isRankOnTable
  }

  def startAttack(card: Card, player: Player): Unit = {
    if ((attacks.isEmpty || (isRankOnTable(card.rank)) && player.isAttacker && player.hisTurn)) {
      val cardAndPlayer = player.putDownCard(card)
      attacks = Attack(cardAndPlayer._1) :: attacks
      players = players.updated(players.indexOf(player), cardAndPlayer._2)
    } else if (!player.isAttacker || !player.hisTurn) throw new IllegalArgumentException("The player is currently not allowed to attack")
    else throw new IllegalArgumentException("The rank of this card is not on the table yet")
  }

  def defendAttack(card: Card, player: Player, attack: Attack): Unit = {
    if (attacks.contains(attack) && player.isDefender && player.hisTurn) {
      val cardAndPlayer = player.putDownCard(card)
      val finishedAttack = attack.defend(card)
      players.updated(players.indexOf(player), cardAndPlayer._2)
      attacks = attacks.updated(attacks.indexOf(attack), finishedAttack)
    } else if (!attacks.contains(attack)) throw new IllegalArgumentException("This attack does not exist")
    else if (!player.isDefender || !player.hisTurn) throw new IllegalArgumentException("This player is currently not allowed to defend")
  }
}

object Round extends IRound {
  override def startNextRound(deck: Deck, players: List[Player], activePlayerNumber: Int): Tuple3[Deck, List[Player], Int] = {
    val round = new Round(deck, players, activePlayerNumber)
    while (!round.roundFinished) {

    }
    Tuple3(deck, players, activePlayerNumber)
  }
}