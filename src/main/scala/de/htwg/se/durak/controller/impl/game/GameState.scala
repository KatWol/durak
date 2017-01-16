package de.htwg.se.durak.controller.impl.game

import de.htwg.se.durak.controller.impl.GameRound

trait GameState {
  def updateState(game: GameRound)

}