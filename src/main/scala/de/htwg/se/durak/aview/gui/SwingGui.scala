package de.htwg.se.durak.aview.gui

import scala.swing.Alignment
import scala.swing.Frame
import scala.swing.GridPanel
import scala.swing.Label
import scala.swing.TextField
import scala.swing._
import javax.swing.ImageIcon

import de.htwg.se.durak.controller.GameRoundController
import de.htwg.se.util.Observer

class SwingGui(var controller: GameRoundController) extends Frame with Observer {
  final val myFont = new Font("Arial", 0, 25)

  controller.add(this)
  controller.addSubscriberToRound(this)

  title = "Durak"

  contents = getContent
  //contents = statusPanel

  visible = true

  //Größe und Position auf dem Bildschirm
  val framewidth = 1024
  val frameheight = 768
  val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
  location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
  minimumSize = new java.awt.Dimension(framewidth, frameheight)

  def getContent = new BoxPanel(Orientation.Vertical) {
    contents += statusPanel
    contents += tablePanel
    contents += playerPanel

  }

  def statusPanel = new FlowPanel {

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
      font = myFont
    }
    contents += new TextField {
      text = controller.getRoundStatus
      columns = 45
      font = myFont
    }

    contents += new Label {
      text = "Player name:"
      horizontalAlignment = Alignment.Left
      font = myFont
    }
    contents += new TextField {
      text = controller.getCurrentPlayerName
      columns = 15
      font = myFont
    }

    contents += new Label {
      text = "Status:"
      horizontalAlignment = Alignment.Left
      font = myFont
    }
    contents += new TextField {
      text = controller.getCurrentPlayerStatus
      columns = 15
      font = myFont
    }

    contents += new Label {
      text = "Number of cards in deck:"
      horizontalAlignment = Alignment.Left
      font = myFont
    }
    contents += new TextField {
      text = controller.getNumberOfCardsInDeck
      columns = 15
      font = myFont
    }
  }

  def tablePanel = new GridPanel(1, 2) {

  }

  def playerPanel = new FlowPanel() {

    for (card <- controller.getCurrentPlayersCards) {
      /*contents += new Label {
        font = myFont
        text = card.suit + " " + card.rank
        horizontalAlignment = Alignment.Left
        icon = new ImageIcon("D:/Diverses/durak/karten/a1_big.jpg")
      }*/

      contents += new BoxPanel(Orientation.Vertical) {
        contents += new Label {
          icon = new ImageIcon("D:/Diverses/durak/karten/a1_big.jpg")
        }
        contents += new Label {
          font = myFont
          text = card.suit + " " + card.rank
          horizontalAlignment = Alignment.Left
        }
      }

    }
  }

  def redraw = {
    contents = statusPanel
    repaint
  }

  override def update = {
    redraw
  }

}