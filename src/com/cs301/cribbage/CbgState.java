package com.cs301.cribbage;
import java.util.ArrayList;

import android.util.Log;
import edu.up.cs301.card.*;
import edu.up.cs301.game.infoMsg.GameState;

/**
 * @author Will Christiansen
 * @author Devon Griggs
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * @version 12/2/2013
 */

class CbgState extends GameState {
	
	// Instance Variables & Final Variables
	
	public static final int PLAYER_1 = 0;//player constants
	public static final int PLAYER_2 = 1;
	private Card[] player1Hand;
	private Card[] player2Hand;
	private Card[] playerHand;
	private int turn;
	private Card[] crib;
	private Deck deck;
	private Card bonusCard;
	private int throwCount;
	private int gameStage;
	private int winner;
	private int player1Score;
	private int player2Score;
	private int tally;
	private ArrayList<Card> tableArray;
	public static final int THROW_STAGE = 0;
	public static final int PEG_STAGE = 1;
	public static final int COUNT_STAGE = 2;
	public int cribOwner;
	public String[] tutorialTexts = {
			"It is now the Throw Stage. Please select two cards to throw to the crib",
			"It is now the Peg Stage. Please select one card at a time to send to the table",
			"It is now the Count Stage. The cards are being counted."
	};

	// Set after score bar is updated, if score exceeds max points this is set to true.

	public boolean isGameOver;
	public static int MAX_TALLY = 31;
	public static int MAX_SCORE = 121;

	public CbgState(){
		super();
		tableArray = new ArrayList<Card>();
		player1Hand = new Card[6];

		player2Hand= new Card[6];
		playerHand = new Card[6] ;

		crib = new Card[4];
		deck = new Deck();
		deck = deck.add52().shuffle();
	} // Constructor for the blank gameState

	/**
	 * Create the gameState using the blank CbgState
	 * @param CbgState orig  Previously created blank state
	 */
	
	public CbgState(CbgState orig){
		super();
		tableArray = new ArrayList<Card>();
		player1Hand = orig.player1Hand;
		player2Hand = orig.player2Hand;
		crib = orig.crib;
		deck = orig.deck;
		bonusCard = orig.bonusCard;
		gameStage = orig.gameStage;
		winner = orig.winner ;
		player1Score = orig.player1Score;
		player2Score = orig.player2Score;
		tally = orig.tally;
		tableArray = orig.tableArray;

		cribOwner = orig.cribOwner;
		tutorialTexts = orig.tutorialTexts ;
		isGameOver = orig.isGameOver ;
		playerHand = new Card[4];
	}
	
	/**
	 * Setter method for the turn
	 * @param int turn  The turn the game is in
	 */
	
	public void setTurn(int turn){
		this.turn = turn;
	}
	
	/**
	 * Setter method for the ThrowCount
	 * @param int throwCount  The number of cards thrown
	 */
	
	public final void setThrowCount(int throwCount){
		this.throwCount = throwCount;
	}
	
	/**
	 * Setter method for the score
	 * @param score  The score
	 * @param int player  Reference number for the player
	 */
	
	public final void setScore(int score, int player) {
		if (player == PLAYER_1){
			player1Score = score;
		}
		else if (player == PLAYER_2){
			player2Score = score;
		}
		else {
			Log.i("Player error", "Error, there is no player " + player);
		}
		isGameOver(); // Checks if game is over when score is changed
	}
	
	/**
	 * Adds to the already set score
	 * @param int score  The score
	 * @param int player  Reference number for the player
	 */
	
	public final void addScore(int score, int player) {
		if (player == PLAYER_1){
			player1Score += score;
		}
		else if (player == PLAYER_2){
			player2Score += score;
		}
		else {
			Log.i("Player error", "Error, there is no player " + player);
		}
		isGameOver(); // Checks if game is over when score is changed
	}
	
	/**
	 * Setter method for the tally
	 * @param int tally  The tally to be set
	 */

	public final void setTally(int tally) {
		this.tally = tally;
	}
	
	/**
	 * Adds to the already set tally
	 * @param int tally  The tally to be set
	 */
	
	public final void addTally(int tally){
		this.tally += tally;
	}
	
	/**
	 * Setter method for the hand
	 * @param Card[] hand  The given hand
	 */
	
	public final void setHand(Card[] hand) {
		playerHand = hand;
	}
	
	/**
	 * Setter method for a specific player's hand
	 * @param Card[] hand  The given hand
	 * @param int player  Reference for the player whose hand is to be set
	 */
	
	public final void setHand(Card[] hand, int player) {
		if (player == PLAYER_1){
			player1Hand = hand;
		}
		else if (player == PLAYER_2){
			player2Hand = hand;
		}
		else {
			Log.i("Player error", "Error, there is no player " + player);
		}
	}

	/**
	 * Setter method for the crib
	 * @param Card[] crib  The array of cards holding the crib
	 */
	
	public final void setCrib(Card[] crib) {
		this.crib = crib;
	}

	/**
	 * Setter method for the deck of cards
	 * @param Deck deck  The deck of cards
	 */
	
	public final void setDeck(Deck deck) {
		this.deck = deck;
	}

	/**
	 * Setter method for the table
	 * @param ArrayList<Card> table  The entire array for cards on the table
	 */
	
	public final void setTable(ArrayList<Card> table) {
		this.tableArray = table;
	}
	
	/**
	 * Setter method for the bonus card
	 * @param Card card  The bonus card
	 */

	public final void setBonusCard(Card card) {
		bonusCard = card;
	}

	/**
	 * Setter method for the GameStage
	 * @param int stage  The reference for the stage (using final ints)
	 */
	
	public final void setGameStage(int stage) {
		gameStage = stage;
	}
	
	/**
	 * Setter method for whether or not the game is over
	 * @param boolean isGameOver  Whether or not the game is over
	 */

	public final void setGameOver(boolean isGameOver){
		this.isGameOver = isGameOver;
	}
	
	/**
	 * Setter method for the winner
	 * @param int winner  Reference to who has won the game (final ints)
	 */

	public final void setWinner(int winner){
		this.winner = winner;
	}

	/**
	 * Getter method for the score
	 * @param int player  Reference for the player
	 * @return int player1Score (OR) int player2Score  The score to be set depending on the input player reference number
	 */
	
	public final int getScore(int player) {
		if(player == PLAYER_1) {
			return player1Score;
		}
		else if (player == PLAYER_2){
			return player2Score;
		}
		else return -1; //for error
	}
	
	/**
	 * Getter method for the tally
	 * @return int tally  The tally
	 */

	public final int getTally() {
		return tally;
	}

	/**
	 * Getter method for the crib
	 * @return Card[] crib  The crib
	 */
	
	public final Card[] getCrib() {
		return crib;
	}
	
	/**
	 * Getter method for the table
	 * @return ArrayList<Card> tableArray  The array of all the cards on the table
	 */

	public final ArrayList<Card> getTable() {
		return tableArray;
	}
	
	/**
	 * Getter method for the hand
	 * @return Card[] playerHand  The player's hand
	 */

	public final Card[] getHand() {
		return playerHand;
	}

	/**
	 * Getter method for the hand
	 * @param int player  Reference for which player's hand we are getting
	 * @return player1Hand (OR) player2Hand  The player's hand
	 */
	
	public final Card[] getHand(int player) {
		if (player == PLAYER_1){
			return player1Hand;
		}
		else if (player == PLAYER_2){
			return player2Hand;
		}
		else return null; // If there is an error
	}

	/**
	 * Getter method for the deck
	 * @return Deck deck  The deck
	 */
	
	public final Deck getDeck() {
		return deck;
	}
	
	/**
	 * Getter method for the bonus card
	 * @return Card bonusCard  The bonus card
	 */

	public final Card getBonusCard() {
		return bonusCard;
	}

	/**
	 * Getter method for whether or not the game is over
	 * @return boolean isGameOver  Whether or not the game is over
	 */
	
	public final boolean getGameOver() {
		return isGameOver;

	}

	/**
	 * 	Method to check if the game is over by checking if the score is over the MAX_SCORE (221)
	 */

	public void isGameOver(){
		if(player1Score >= MAX_SCORE){
			setGameOver(true);
			setWinner(PLAYER_1);
		}
		else if (player2Score >= MAX_SCORE){
			setGameOver(true);
			setWinner(PLAYER_2);
		}
	}

	/**
	 * Getter method for the gameStage
	 * @return int gameStage  The stage the game is in
	 */
	
	public final int getGameStage() {
		return gameStage;
	} // GetGameStage

	/**
	 * Getter method for the winner of the game
	 * @return int winner  Reference to the winner
	 */
	
	public int getWinner(){
		return winner;
	}
	
	/**
	 * Getter method for the owner of the crib
	 * @return int cribOwner  Reference to the crib owner
	 */
	
	public int getCribOwner(){
		return cribOwner;
	}
	
<<<<<<< HEAD
	public void switchCribOwner(){
		if(cribOwner == CbgState.PLAYER_1){
			cribOwner = CbgState.PLAYER_2;
		} else {
			cribOwner = CbgState.PLAYER_1;
		}
	}
=======
>>>>>>> 9a784e1c681e701813182b1fc71b380ae4d1f810
	/**
	 * Getter method for the throwCount
	 * @return int throwCount  The number of cards being thrown
	 */
	
	public int getThrowCount(){
		return throwCount;
	}
	
	/**
	 * Getter method for the turn the game is in
	 * @return int turn  Reference for which turn the game is in
	 */
	
	public int getTurn(){
		return turn;
	}
}