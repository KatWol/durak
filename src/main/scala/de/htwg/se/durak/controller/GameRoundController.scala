package de.htwg.se.durak.controller

import de.htwg.se.util.Observable
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.util.Observer

trait GameRoundController extends Observable {
  /**
   * All actions to start a new game round are performed
   */
  def updateGameRound

  /**
   * Takes card from the current player and adds it to the given attack if the player is an attacker
   * or a new attack if the player is a defender
   *
   * @param suit: Suit of the card
   * @param rank: Rank of the card
   * @param attack: Number of the attack
   */
  def playCard(suit: String, rank: String, attack: String)

  /**
   * Ends the turn of the current player
   */
  def endTurn

  def addSubscriberToRound(s: Observer)

  //**** Methoden um den aktuellen Status anzuzeigen ***
  def getGameStatus: String
  def getRoundStatus: String
  def getCurrentPlayerName: String
  def getCurrentPlayerStatus: String
  def getNumberOfCardsInDeck: String
  def getCurrentPlayersCardsString: String //Für Tui
  def getCurrentPlayersCards: List[Card] //Für Gui
  def getAttacksOnTableString: String //Für Tui
  def getAttacksOnTable: List[Attack] //Für Gui
}