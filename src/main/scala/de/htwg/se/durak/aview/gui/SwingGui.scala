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
  def attacks = controller.getAttacksOnTable

  def attackImages: Array[Array[String]] =  {
    var result = Array.ofDim[String](2, 6)
    for (row <- 0 to 5) {
      if (attacks.size - 1 >= row) {
        result(0)(row) = path + attacks(row).attackingCard.suit + attacks(row).attackingCard.rank + ".jpg"
        if (attacks(row).defendingCard != null) {
          result(1)(row) = path + attacks(row).defendingCard.suit + attacks(row).defendingCard.rank + ".jpg"
        } else {
           result(1)(row) = path + "empty.jpg"
        }
      } else {
        result(0)(row) = path + "empty.jpg"
        result(1)(row) = path + "empty.jpg"        
      }
    }
    return result
  }
  
  def tablePanel = new GridPanel(1, 2) {

    contents += new Label {
      font = myFont
      text = "Number of cards in deck: " + controller.getNumberOfCardsInDeck
      horizontalAlignment = Alignment.Left
    }
    
    contents += new GridPanel(2,6) {
    
      for (row <- 0 to 1; col <- 0 to 5) {
        contents += new Label {  
          icon = new ImageIcon(attackImages(row)(col))
            listenTo(mouse.clicks)
            reactions += {
              case _: event.MouseClicked => numberOfAttack = col
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