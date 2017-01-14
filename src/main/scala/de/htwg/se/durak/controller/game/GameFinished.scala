package de.htwg.se.durak.controller.game

import de.htwg.se.durak.controller.GameRound

class GameFinished extends GameState {
  override def updateState(game: GameRound) = false 
}