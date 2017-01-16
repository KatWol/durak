package de.htwg.se.durak.aview.gui

import scala.swing._

import de.htwg.se.durak.controller.GameRound
import de.htwg.se.util.Observer

class SwingGui(var controller: GameRound) extends Frame with Observer {
  controller.add(this)
  controller.round.add(this)

  title = "Durak"

  def statusPanel = new GridPanel(4, 2) {

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.round.statusLine, 45)

    contents += new Label {
      text = "Player name:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.round.getCurrentPlayer.name, 15)

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.round.getCurrentPlayer.status.toString, 15)

    contents += new Label {
      text = "Number of cards in deck:"
      horizontalAlignment = Alignment.Left
    }
    contents += new TextField(controller.round.deck.numberOfCards.toString, 15)
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