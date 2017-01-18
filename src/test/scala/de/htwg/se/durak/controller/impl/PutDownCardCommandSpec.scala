package de.htwg.se.durak.controller.impl

import org.scalatest.Matchers
import org.scalatest.WordSpec

import com.google.inject.Guice

import de.htwg.se.durak.DurakModule
import de.htwg.se.durak.model.AttackFactory
import de.htwg.se.durak.model.CardFactory
import de.htwg.se.durak.model.DeckFactory
import de.htwg.se.durak.model.Player
import de.htwg.se.durak.model.PlayerFactory
import de.htwg.se.durak.model.PlayerStatus
import de.htwg.se.durak.model.Rank
import de.htwg.se.durak.model.Suit
import de.htwg.se.durak.controller.impl.round.DefendersTurn

class PutDownCardCommandSpec extends WordSpec with Matchers {
  "A PutDownCardCommand" should {
    val injector = Guice.createInjector(new DurakModule())
    val deckFactory = injector.getInstance(classOf[DeckFactory])
    val playerFactory = injector.getInstance(classOf[PlayerFactory])
    val cardFactory = injector.getInstance(classOf[CardFactory])
    val attackFactory = injector.getInstance(classOf[AttackFactory])

    "throw an IllegalArgumentException if the defender plays a card he does not have" in {

      var players = List[Player](playerFactory.create("Kathrin", 0, List()).setStatus(PlayerStatus.Defender).setTurn(true), playerFactory.create("Jakob", 1, List()).setStatus(PlayerStatus.Attacker))
      var round = new Round(deckFactory.create(Rank.Seven), players, Suit.Hearts)
      round.state = new DefendersTurn
      val card = cardFactory.create("clubs", "nine")
      val attack = attackFactory.create(cardFactory.create("clubs", "seven"))

      val putDownCardCommand = new PutDownCardCommand(round, card, attack)

      the[IllegalArgumentException] thrownBy {
        putDownCardCommand.execute
      } should have message ("Player does not have this card in his/her hand")
    }

    "undo the last action" in {
      val card = cardFactory.create("clubs", "nine")
      var player1 = playerFactory.create("Kathrin", 0, List(card)).setStatus(PlayerStatus.Defender).setTurn(true)
      var players = List[Player](player1, playerFactory.create("Jakob", 1, List()).setStatus(PlayerStatus.Attacker))
      var round = new Round(deckFactory.create(Rank.Seven), players, Suit.Hearts)
      round.state = new DefendersTurn
      val attack = attackFactory.create(cardFactory.create("clubs", "seven"))
      round.attacks = List(attack)
      val putDownCardCommand = new PutDownCardCommand(round, card, attack)

      putDownCardCommand.execute
      putDownCardCommand.undo

      round.attacks.contains(attack) should be(true)
      player1.cards.contains(card) should be(true)

    }
  }
}