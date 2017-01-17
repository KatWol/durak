package de.htwg.se.durak.controller.impl

import de.htwg.se.durak.controller.impl.round.AttackersTurn
import de.htwg.se.durak.controller.impl.round.DefendersTurn
import de.htwg.se.durak.model.Attack
import de.htwg.se.durak.model.Card
import de.htwg.se.util.Command
import com.google.inject.Guice
import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.model.AttackFactory

class PutDownCardCommand(round: Round, card: Card, attack: Attack = null) extends Command {
  val injector = Guice.createInjector(new DurakModule())
  val attackFactory = injector.getInstance(classOf[AttackFactory])

  override def execute = {
    round.state match {
      case a: AttackersTurn => {
        round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card))
        round.statusLine = round.getCurrentPlayer.name + " played the card " + card
        round.attacks = (attackFactory.create(card)) :: round.attacks
      }
      case d: DefendersTurn => {
        if (round.getCurrentPlayer.cards.contains(card)) {
          round.attacks = round.attacks.updated(round.attacks.indexOf(attack), attack.defend(card))
          round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.putDownCard(card))
          round.statusLine = round.getCurrentPlayer.name + " played the card " + card
        } else throw new IllegalArgumentException("Player does not have this card in his/her hand")
      }
    }
  }

  override def undo = {
    //Spieler Karte zurück geben
    round.updatePlayer(round.getCurrentPlayer, round.getCurrentPlayer.takeCards(card))

    //Karte aus Attack entfernen oder Attack entfernen
    round.state match {
      case a: AttackersTurn => round.attacks = round.attacks.filter(a => a != attackFactory.create(card))
      case d: DefendersTurn => {
        val oldAttack = round.attacks.filter(a => a.defendingCard == card).head
        val attack = oldAttack.resetDefendingCard
        round.attacks = round.attacks.updated(round.attacks.indexOf(oldAttack), attack)
      }
    }

    //Status Line setzen
    round.statusLine = "The player has undone the last action"
  }
}