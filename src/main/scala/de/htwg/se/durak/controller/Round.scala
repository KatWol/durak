package de.htwg.se.durak.controller

import de.htwg.se.durak.model._

class Round(var deck: Deck, var players: List[Player], val activePlayerNumber: Int) {
   var attacks = List[Attack]()
   
   def startFirstAttack(card: Card): Unit = {
     val activePlayer = getActivePlayer()
     val cardAndPlayer = activePlayer.putDownCard(card)
     attacks = Attack(cardAndPlayer._1) :: attacks
     players = players.updated(players.indexOf(activePlayer), cardAndPlayer._2)     
   }
   
   def getActivePlayer(): Player = players.filter(_.number == activePlayerNumber)(0)   
   
   def isRankOnTable(rank: Rank): Boolean = {
     var isRankOnTable = false
     for (attack <- attacks) {
       if (attack.attackingCard.rank == rank) isRankOnTable = true
       if (attack.defendingCard != null && attack.defendingCard.rank == rank) isRankOnTable = true
     }
     isRankOnTable
   }
   
   
   def startAttack(card: Card): Unit = Unit
   def defendAttack(card: Card): Unit = Unit
   def isValid(card: Card): Boolean = true
}