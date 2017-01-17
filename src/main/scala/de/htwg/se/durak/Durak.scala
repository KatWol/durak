package de.htwg.se.durak

import de.htwg.se.durak.aview.tui.Tui
import de.htwg.se.durak.controller.impl.GameRound

import scala.io.StdIn._
import de.htwg.se.durak.aview.gui.SwingGui
import de.htwg.se.durak.controller.GameRoundController
import com.google.inject.Guice
import de.htwg.se.durak.controller.GameRoundControllerFactory

object Durak {
  def main(args: Array[String]): Unit = {

    enterPlayerNames

    //val playerNames = processPlayerNamesInput(readLine())
    val playerNames = processPlayerNamesInput("Jakob,Kathrin")
    val injector = Guice.createInjector(new DurakModule)
    val controllerFactory = injector.getInstance(classOf[GameRoundControllerFactory])
    val controller = controllerFactory.create(playerNames)
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

  def processPlayerNamesInput(input: String): List[String] = input.split(",").map(_.trim).toList

}