package de.htwg.se.durak.controller

import de.htwg.se.util.Observable
import de.htwg.se.durak.model.Card
import de.htwg.se.durak.model.Attack
import de.htwg.se.util.Observer
import de.htwg.se.durak.model.Rank

trait GameRoundController extends Observable {
  /**
   * Adds the Subscriber to the round
   */
  def addSubscriberToRound(s: Observer)

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
   * If possible, undoes the last action
   */
  def undo

  /**
   * If a undo was called before, redoes that last undo
   */
  def redo

  //**** Methoden um den aktuellen Status anzuzeigen ***

  /**
   * Returns the game status as a string
   */
  def getGameStatus: String

  /**
   * Returns the round status as a string
   */
  def getRoundStatus: String

  /**
   * Returns the name of the current player
   */
  def getCurrentPlayerName: String

  /**
   * Returns the status of the current player
   */
  def getCurrentPlayerStatus: String

  /**
   * Returns the number of cards left in the deck
   */
  def getNumberOfCardsInDeck: String

  /**
   * Returns a formated string of the cards of the current player
   */
  def getCurrentPlayersCardsString: String //Für Tui

  /**
   * Returns the cards of the current player
   */
  def getCurrentPlayersCards: List[Card] //Für Gui
  /**
   * Returns a formated string of the attacks on the table
   */
  def getAttacksOnTableString: String //Für Tui

  /**
   * Returns the attacks on the table
   */
  def getAttacksOnTable: List[Attack] //Für Gui

  /**
   * Returns the last card from the deck that defines the trump colour
   */
  def getLastCardFromDeck: Card

  /**
   * Returns the name of the durak when a game is finished
   */
  def getDurakName: String

  /**
   * Returns whether the defender won in the last round
   */
  def getDefenderLost: Boolean
}

trait GameRoundControllerFactory {
  def create(playerNames: List[String], startWithRank: String = "seven", startWithSmallestTrump: Boolean = true): GameRoundController
}