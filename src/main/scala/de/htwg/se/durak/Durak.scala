package de.htwg.se.durak

import de.htwg.se.durak.aview.tui.Tui
import de.htwg.se.durak.controller.GameRound

import scala.io.StdIn._

object Durak {
  def main(args: Array[String]): Unit = {
    val controller = new GameRound
    val tui = new Tui(controller)
    tui.printTui
    while (tui.processInputLine(readLine())) {}
  }
}