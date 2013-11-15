package com.cs301.cribbage;
import android.util.Log;
import edu.up.cs301.card.*;

/**
 * Cribbage State
 * Version 11/14/2013
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * 
 *   This class holds the information for the current state of the game. 
 */


class CbgState {
	public final int THROW_STAGE = 1;
	public final int PEG_STAGE = 2;
	public final int COUNT_STAGE = 3;
	public static final int PLAYER_1 = 1;//player constants
	public static final int PLAYER_2 = 2;
	
    private Card[] player1Hand;
    private Card[] player2Hand;
    private Card[] crib;
    private Deck deck;
    private Card bonusCard;
    private int gameStage;
    private int winner;
    protected int player1Score;
    protected int player2Score;
    protected int tally;
    protected Card[] tableArray;
    

    /*
     * Set after score bar is updated, if score exceeds max points this is set to true.
     */

    public boolean isGameOver;
    public int MAX_TALLY = 31;
    public int MAX_SCORE = 121;

    public CbgState()
    {
        /*crib = null;
        deck.setDeck();
        bonusCard = null;
        gameStage = cbgStage;
        winner = ;
        player1Score = ;
        player2Score = ;
        tally = 
        tableArray = ;*/

    }//constructor
    
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
    }//setScore

    public final void setTally(int tally) {
    	this.tally = tally;
    
    }//setTally

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
    }//setHand

    public final void setCrib(Card[] crib) {
    	this.crib = crib;
    }//setCrib

    public void setDeck(Deck deck) {
    	this.deck = deck;
    }//setDeck

    public final void setTable(Card[] table) {
    	this.tableArray = table;
    }//setTable

    public final void setBonusCard(Card card) {
    	bonusCard = card;
    }//setBonusCard

    public final void setGameStage(int stage) {
    	//TODO
    	gameStage = stage;
    }//setGameStage
    
    public final void setGameOver(boolean isGameOver){
    	this.isGameOver = isGameOver;
    }//setGameOver
    public final void setWinner(int winner){
    	this.winner = winner;
    }//setWinnder

    public final int getScore(int player) {
    	if(player == PLAYER_1) {
    		return player1Score;
    	}
    	else if (player == PLAYER_2){
    		return player2Score;
    	}
    	else return -1; //for error
    
    }//getScore

    public final int getTally() {
    	//TODO
    	return tally;
    
    }//getTally

    public final Card[] getCrib() {
    	//TODO
    	return crib;
    
    }//getCrib

    public final Card[] getTable() {
    	//TODO
    	return tableArray;
    
    }//getTable

    public final Card[] getHand(int player) {
    	if (player == PLAYER_1){
    		return player1Hand;
    	}
    	else if (player == PLAYER_2){
    		return player2Hand;
    	}
    	else return null;// if error
    }//getHand

    public final Deck getDeck() {
    	//TODO
    	return deck;
    }//getDeck

    public final Card getBonusCard() {
    	//TODO
    	return bonusCard;
    }//getBonusCard

    public final boolean getGameOver() {
    	//TODO    	
    	return isGameOver;
    
    }//getGameOver

    public final int getGameStage() {
    	int p1Counter = 0;
    	int p2Counter = 0;
    	for(int i = 0; i<player1Hand.length; i++){
    		if(player1Hand[i] != null){
    			p1Counter++;
    		}
    		if(player2Hand[i] != null){
    			p2Counter++;
    		}
    	}
    	if(p1Counter == 6 && p2Counter == 6){
    		return THROW_STAGE;
    	}else if(p1Counter <= 4 && p2Counter <= 4) {
    		return PEG_STAGE;
    	}else if (p1Counter == 0 && p2Counter ==0){
    		return COUNT_STAGE;
    	}
    	return gameStage;
    }//getGameStage
   
    public void isGameOver(){
    	if(player1Score >= MAX_SCORE){
    		setGameOver(true);
    		setWinner(PLAYER_1);
    	}
    	else if (player2Score >= MAX_SCORE){
    		setGameOver(true);
    		setWinner(PLAYER_2);
    	}
    }//isGameOver
    public int getWinner(){
    	return winner;
    }//getWinner
}