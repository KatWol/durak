package de.htwg.se.durak.controller

import de.htwg.se.util.Observable
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.util.Observer
import de.htwg.se.durak.model.Rank

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
  def playCard(suit: String, rank: String, attack: String = "")

  /**
   * Ends the turn of the current player
   */
  def endTurn

  /**
   * Adds the Subscriber to the round
   */
  def addSubscriberToRound(s: Observer)

  /**
   * If possible, undoes the last action
   */
  def undo

  /**
   * If a undo was called before, redoes that last undo
   */
  def redo

  //**** Methoden um den aktuellen Status anzuzeigen ***
  def getGameStatus: String
  def getRoundStatus: String
  def getCurrentPlayerName: String
  def getCurrentPlayerStatus: String
  def getNumberOfCardsInDeck: String
  /**
   * Returns a formated string of the cards of the current player
   */
  def getCurrentPlayersCardsString: String //Für Tui
  def getCurrentPlayersCards: List[Card] //Für Gui
  /**
   * Returns a formated string of the attacks on the table
   */
  def getAttacksOnTableString: String //Für Tui
  def getAttacksOnTable: List[Attack] //Für Gui
}

trait GameRoundControllerFactory {
  def create(playerNames: List[String], startWithRank: String = "seven", startWithSmallestTrump: Boolean = true): GameRoundController
}