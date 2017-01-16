package de.htwg.se.durak

import de.htwg.se.durak.aview.tui.Tui
import de.htwg.se.durak.controller.impl.GameRound

import scala.io.StdIn._
import de.htwg.se.durak.aview.gui.SwingGui

object Durak {
  def main(args: Array[String]): Unit = {

    enterPlayerNames
    val controller = processPlayerNamesInput(readLine())
    val tui = new Tui(controller)
    val gui = new SwingGui(controller)
    tui.printTui
    while (tui.processInputLine(readLine())) {}
  }

  def enterPlayerNames = {
    println("Durak")
    println
    println("Please enter the names of the players, separed by comma:")
  }

  def processPlayerNamesInput(input: String): GameRound = new GameRound(playerNames = input.split(",").map(_.trim).toList)

}