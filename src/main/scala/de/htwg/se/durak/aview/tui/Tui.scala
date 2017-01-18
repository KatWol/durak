package de.htwg.se.durak.aview.tui

import com.google.inject.Inject

import de.htwg.se.durak.controller.GameRoundController
import de.htwg.se.util.Observer
import org.apache.log4j.Logger

class Tui(var controller: GameRoundController) extends Observer {
  controller.add(this)
  controller.addSubscriberToRound(this)
  val logger: Logger = Logger.getLogger("de.htwg.se.durak.aview.tui")

  override def update = printTui

  def printTui = {
    logger.info(controller.getGameStatus)
    logger.info(controller.getRoundStatus)
    logger.info("Current players name: " + controller.getCurrentPlayerName)
    logger.info("Current players status: " + controller.getCurrentPlayerStatus)
    logger.info("Number of cards in deck: " + controller.getNumberOfCardsInDeck + "\n")

    logger.info("Current players cards: " + controller.getCurrentPlayersCardsString + "\n")

    logger.info("Current attacks on the table: " + controller.getAttacksOnTableString + "\n")

    logger.info("Please enter a command: ")
    logger.info("suit,rank - Play a card to start an attack")
    logger.info("suit,rank,numberOfAttack - Play a card to defend an attack")
    logger.info("e - End turn")
    logger.info("s - Start next round")
    logger.info("u - Undo")
    logger.info("r - Redo")
    logger.info("q - Quit game")
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