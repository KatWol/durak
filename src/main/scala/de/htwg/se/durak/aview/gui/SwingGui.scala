package de.htwg.se.durak.aview.gui

import scala.swing.Alignment
import scala.swing.Frame
import scala.swing.GridPanel
import scala.swing.Label
import scala.swing.TextField

import de.htwg.se.durak.controller.GameRoundController
import de.htwg.se.util.Observer

class SwingGui(var controller: GameRoundController) extends Frame with Observer {
  controller.add(this)
  controller.addSubscriberToRound(this)

  title = "Durak"

  def statusPanel = new GridPanel(4, 2) {

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.getRoundStatus, 45)

    contents += new Label {
      text = "Player name:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.getCurrentPlayerName, 15)

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.getCurrentPlayerStatus, 15)

    contents += new Label {
      text = "Number of cards in deck:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.getNumberOfCardsInDeck, 15)
  }

  contents = statusPanel

  visible = true

  def redraw = {
    contents = statusPanel
    repaint
  }
  override def update = {
    redraw
  }

}