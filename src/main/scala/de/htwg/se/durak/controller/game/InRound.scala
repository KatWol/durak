package de.htwg.se.durak.controller.game

import de.htwg.se.durak.controller.GameRound
import de.htwg.se.durak.controller.round.RoundFinished
import de.htwg.se.durak.controller.round.RoundNotFinished

class InRound extends GameState {
  override def updateState(game: GameRound): Boolean = {
    game.round.state match {
      case notFinished: RoundNotFinished => false
      case finished: RoundFinished => {
        game.changeState(new StartOfRound)
        true
      }
    }
  }

}