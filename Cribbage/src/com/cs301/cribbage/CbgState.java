package com.cs301.cribbage;
import java.util.ArrayList;

import android.util.Log;
import edu.up.cs301.card.*;
import edu.up.cs301.game.infoMsg.GameState;

class CbgState extends GameState{
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
	public int gameStage;
	private int winner;
	protected int player1Score;
	protected int player2Score;
	protected int tally;
	protected ArrayList<Card> tableArray;
	public static final int THROW_STAGE = 0;
	public static final int PEG_STAGE = 1;
	public static final int COUNT_STAGE = 2;
	public int cribOwner;
	public String[] tutorialTexts = {
			"It is now the Throw Stage. Please select two cards to throw to the crib",
			"It is now the Peg Stage. Please select one card at a time to send to the table",
			"It is now the Count Stage. The cards are being counted."
	};


	/*
	 * Set after score bar is updated, if score exceeds max points this is set to true.
	 */

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

	}
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
	public void setTurn(int turn){
		this.turn = turn;
	}
	public final void setThrowCount(int throwCount){
		this.throwCount = throwCount;
	}
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
		isGameOver();//checks if game is over when score is changed
	}

	public final void setTally(int tally) {
		this.tally = tally;

	}
	public final void setHand(Card[] hand) {
		playerHand = hand;
	}
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

	public final void setCrib(Card[] crib) {
		this.crib = crib;
	}

	public final void setDeck(Deck deck) {
		this.deck = deck;
	}

	public final void setTable(ArrayList<Card> table) {
		this.tableArray = table;
	}

	public final void setBonusCard(Card card) {
		bonusCard = card;
	}

	public final void setGameStage(int stage) {
		gameStage = stage;
	}

	public final void setGameOver(boolean isGameOver){
		this.isGameOver = isGameOver;
	}
	public final void setWinner(int winner){
		this.winner = winner;
	}

	public final int getScore(int player) {
		if(player == PLAYER_1) {
			return player1Score;
		}
		else if (player == PLAYER_2){
			return player2Score;
		}
		else return -1; //for error

	}

	public final int getTally() {
		return tally;

	}

	public final Card[] getCrib() {
		return crib;

	}

	public final ArrayList<Card> getTable() {
		return tableArray;

	}
	public final Card[] getHand() {
		return playerHand;
	}
	public final Card[] getHand(int player) {
		if (player == PLAYER_1){
			return player1Hand;
		}
		else if (player == PLAYER_2){
			return player2Hand;
		}
		else return null;// if error
	}

	public final Deck getDeck() {
		return deck;
	}

	public final Card getBonusCard() {
		return bonusCard;
	}

	public final boolean getGameOver() {
		return isGameOver;

	}



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

	public final int getGameStage() {
		//    	int p1Counter = 0;
		//    	int p2Counter = 0;
		//    	for(int i = 0; i<player1Hand.length; i++){
		//    		if(player1Hand[i] != null){
		//    			p1Counter++;
		//    		}
		//    		if(player2Hand[i] != null){
		//    			p2Counter++;
		//    		}
		//    	}
		//    	if(p1Counter == 6 && p2Counter == 6){
		//    		return THROW_STAGE;
		//    	}else if(p1Counter <= 4 && p2Counter <= 4) {
		//    		return PEG_STAGE;
		//    	}else if (p1Counter == 0 && p2Counter ==0){
		//    		return COUNT_STAGE;
		//    	}
		return gameStage;
	}//getGameStage

	public int getWinner(){
		return winner;
	}
	public int getCribOwner(){
		return cribOwner;
	}
	public int getThrowCount(){
		return throwCount;
	}
	public int getTurn(){
		return turn;
	}
	


}
