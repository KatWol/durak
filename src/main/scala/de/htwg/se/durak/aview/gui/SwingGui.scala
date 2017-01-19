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
import scala.swing.event.Event
import de.htwg.se.durak.controller.impl.RoundFinishedEvent
import de.htwg.se.durak.controller.impl.GameRoundFinishedEvent

class SwingGui(var controller: GameRoundController) extends Frame with Observer {
  final val myFont = new Font("Arial", 0, 25)
  final val path = "img/"
  var numberOfAttack: Int = -1

  controller.add(this)
  title = "Durak"
 
  menuBar = new MenuBar {
    font = myFont
    contents += new Menu("Spielzug") {
      font = myFont
      contents += new MenuItem("Undo") { 
        action = Action("Undo") { controller.undo } 
        font = myFont 
      } 
      contents += new MenuItem("Redo") {
        action = Action("Redo") { controller.redo }
        font = myFont
      }
    }
  }
 
  contents = getContent
  maximize
  visible = true

  //Größe und Position auf dem Bildschirm
  /*val framewidth = 1024
  val frameheight = 1024
  val screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize()
  location = new java.awt.Point((screenSize.width - framewidth) / 2, (screenSize.height - frameheight) / 2)
  minimumSize = new java.awt.Dimension(framewidth, frameheight)*/

  def getContent = new BoxPanel(Orientation.Vertical) {
    contents += statePanel
    contents += tablePanel
    contents += playerPanel

  }

  def statePanel = new FlowPanel {

    contents += new Label {
      text = "Game state:"
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
      text = "Player state:"
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
  }

  def tablePanel = new GridPanel(1, 2) {

    contents += new Label {
      font = myFont
      text = "Number of cards in deck: " + controller.getNumberOfCardsInDeck
      horizontalAlignment = Alignment.Left
    }

    contents += new FlowPanel() {
      for (attack <- controller.getAttacksOnTable) {
        contents += new BoxPanel(Orientation.Vertical) {
          contents += new Label {
            icon = new ImageIcon(path + attack.attackingCard.suit + attack.attackingCard.rank + ".jpg")
            listenTo(mouse.clicks)
            reactions += {
              case _: event.MouseClicked => {
                numberOfAttack = controller.getAttacksOnTable.indexOf(attack)
                println(numberOfAttack)
              }
            }

          }
          contents += new Label {
            if (attack.defendingCard != null) {
              icon = new ImageIcon(path + attack.defendingCard.suit + attack.defendingCard.rank + ".jpg")
            }
          }

        }
      }

    }

  }

  def playerPanel = new FlowPanel() {
    contents += new Label {
      font = myFont
      text = "Cards of " + controller.getCurrentPlayerName
      horizontalAlignment = Alignment.Left
    }
    for (card <- controller.getCurrentPlayersCards) {
      contents += new BoxPanel(Orientation.Vertical) {
        contents += new Label {
          icon = new ImageIcon(path + card.suit + card.rank + ".jpg")
          listenTo(mouse.clicks)
          reactions += {
            case _: event.MouseClicked => {
              if (controller.getCurrentPlayerStatus == "Attacker") {
                controller.playCard(card.suit.toString(), card.rank.toString())
              } else {
                controller.playCard(card.suit.toString(), card.rank.toString(), numberOfAttack.toString())
              }
            }
          }

        }
        contents += new Label {
          font = myFont
          text = card.suit + " " + card.rank
          horizontalAlignment = Alignment.Left
        }
      }
    }
    contents += new Button("") {
      action = Action("Finish turn") {
        controller.endTurn
        numberOfAttack = -1
      }
      font = myFont
    }
  }

  def redraw = {
    contents = getContent
    repaint
    maximize
  
  }

  override def update = {
    redraw
    
  }

  override def update(e: Event) = {
    e match {
      case roundFinished: RoundFinishedEvent => {
        var defenderLost: String = ""
        if (controller.getDefenderLost) defenderLost = "won"
        else defenderLost = "lost"
        showDialog("Round finished", "This round is finished. The defender " + defenderLost + " the last round.")
        redraw
      }

      case gameFinished: GameRoundFinishedEvent => {
        showDialog("Game finished", "The Durak is " + controller.getDurakName)
        redraw
      }
    }
  }

  def showDialog(title: String, message: String) = Dialog.showMessage(playerPanel, message, title)

}