package de.htwg.se.durak.controller.game

import de.htwg.se.durak.controller.GameRound
import de.htwg.se.durak.controller.Round

trait GameState {
  def updateState(game: GameRound)

}