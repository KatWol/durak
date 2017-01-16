package de.htwg.se.durak.aview.tui

import de.htwg.se.durak.controller.GameRound
import de.htwg.se.util.Observer

class Tui(var controller: GameRound) extends Observer {
  controller.add(this)
  controller.round.add(this)

  override def update = printTui

  def printTui = {
    println(controller.getStatusLine)
    println(controller.round.statusLine)
    println("Current players name: " + controller.round.getCurrentPlayer.name)
    println("Current players status: " + controller.round.getCurrentPlayer.status)
    println("Number of cards in deck: " + controller.round.deck.numberOfCards)
    println
    println("Current players cards: " + controller.round.getCurrentPlayer.printCards)
    println
    println("Current attacks on the table: " + controller.round.getAttacksOnTableString)
    println
    println("Please enter a command: ")
    println("suit,rank - Play a card to start an attack")
    println("suit,rank,numberOfAttack - Play a card to defend an attack")
    println("e - EndTurn")
    println("r - Start next round")
    println("q - Quit game")
  }

  def processInputLine(input: String): Boolean = {
    var continue = true
    input match {
      case "q" => continue = false
      case "e" => controller.round.endTurn
      case "r" => controller.updateState
      case _ => {
        val list: List[String] = input.split(",").map(_.trim).toList
        list match {
          case suit :: rank :: Nil => controller.round.playCard(suit, rank, "")
          case suit :: rank :: attack :: Nil => controller.round.playCard(suit, rank, attack)
          case _ => {
            println("False Input!")
            printTui
          }
        }
      }
    }
    continue
  }
}