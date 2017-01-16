package de.htwg.se.durak.aview.tui

import de.htwg.se.durak.controller.impl.GameRound
import de.htwg.se.util.Observer
import de.htwg.se.durak.controller.GameRoundController

class Tui(var controller: GameRoundController) extends Observer {
  controller.add(this)
  controller.addSubscriberToRound(this)

  override def update = printTui

  def printTui = {
    println(controller.getGameStatus)
    println(controller.getRoundStatus)
    println("Current players name: " + controller.getCurrentPlayerName)
    println("Current players status: " + controller.getCurrentPlayerStatus)
    println("Number of cards in deck: " + controller.getNumberOfCardsInDeck)
    println
    println("Current players cards: " + controller.getCurrentPlayersCardsString)
    println
    println("Current attacks on the table: " + controller.getAttacksOnTableString)
    println
    println("Please enter a command: ")
    println("suit,rank - Play a card to start an attack")
    println("suit,rank,numberOfAttack - Play a card to defend an attack")
    println("e - End turn")
    println("s - Start next round")
    println("u - Undo")
    println("r - Redo")
    println("q - Quit game")
  }

  def processInputLine(input: String): Boolean = {
    var continue = true
    input match {
      case "q" => continue = false
      case "e" => controller.endTurn
      case "s" => controller.updateGameRound
      case "u" => controller.undo
      case "r" => controller.redo
      case _ => {
        val list: List[String] = input.split(",").map(_.trim).toList
        list match {
          case suit :: rank :: Nil => controller.playCard(suit, rank, "")
          case suit :: rank :: attack :: Nil => controller.playCard(suit, rank, attack)
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