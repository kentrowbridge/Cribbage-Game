package com.cs301.cribbage;
import java.util.ArrayList;

import android.util.Log;

import edu.up.cs301.card.*;
import edu.up.cs301.game.*;
import edu.up.cs301.game.actionMsg.GameAction;

/* 

 */


class CbgLocalGame extends LocalGame{

	private Deck deck;
	private CbgState state;
	private int currentGameStage;
	private int throwCount;
	private int tallyCount;
	private CbgCounter counter;
	private int turn;
	private int originalTally;

	public CbgLocalGame(){
		deck = new Deck();//creates a deck
		state = new CbgState();
		counter = new CbgCounter();
		turn = CbgState.PLAYER_1;
		gameCycle();

	}

	private void gameCycle()
	{
		while (!state.getGameOver())
		{
			deck.add52().shuffle();// creates a shuffled deck for each round
			state.setDeck(deck); 
			deal();
			throwCount = 0;
			tallyCount = 0;//reset tracker variables
			while (state.getGameStage() == state.THROW_STAGE)
			{
				if(throwCount >= 2){//throwcount updated each time a person throws their set of 
					//2 cards as it is only a single action
					state.setGameStage(state.PEG_STAGE);//each time a player throws a card, 
					//the throwcount is updated, when it hits 2 it moves on to the next stage
				}
			}
			while (state.getGameStage() == state.PEG_STAGE)
			{
				//once the game is in the pegging stage, flips the top card of deck
				state.getBonusCard();
				if(state.getGameOver()) break;
				
				
				//TODO make method to determine when all cards placed on table that can be
				
				
				
				
//				if(counter.countTable((Card[])state.getTable().toArray()) >= 31){ 
//				//get count, if greater than 31, move on
//				state.setGameStage(state.COUNT_STAGE);
//				}
			}
			while (state.getGameStage() == state.COUNT_STAGE)
			{
				//after the score has been tallied, checks if the game is over
				originalTally = state.getTally();
				state.setTally(counter.getTally((Card[])state.getTable().toArray()));
				if(state.getGameOver()) break;
				tallyCount++;
				if (tallyCount >= 2)
				{
					state.setGameStage(state.THROW_STAGE);
				}
			}
			
		}
	}//gameCycle

	/*
	 * Checks the boolean isGameOver in gameState, and if its true it sets the return string 
	 * to reflect the final score and the winner.
	 */

	private final boolean isValidCard() {
		boolean isValid = false;
		// TODO
		return isValid;
	}

	private final int handCount() {
		int count = 0;
		// TODO maybe put in counter class?
		return count;
	}

	@Override
	protected final boolean makeMove(GameAction action) {
		if (action instanceof CardsToTable && canMove(getPlayerIdx(action.getPlayer()))){
			//if card to table action and player can move
			ArrayList<Card> cardArr = new ArrayList<Card>(); 
			CardsToTable cards = (CardsToTable) action;
			cardArr = state.getTable();  // get table
			cardArr.add(cards.cards()); //add card
			state.setTable(cardArr); //send back to gamestate
			switchTurn();
			return true;
		}
		else if (action instanceof CardsToThrow  && canMove(getPlayerIdx(action.getPlayer()))){
			CardsToThrow cards = (CardsToThrow) action;
			Card[] cardArr = state.getCrib();
			Card[] cardsThrown = cards.cards();
			for(int i = 0; i < 2; i++){
				cardArr[cardArr.length] = cardsThrown[i];
			}
			switchTurn();
			throwCount++;
			return true;
		}
		
		else return false;
	}

//	private final int countTable(Card table[]) {
//		int count = 0;
//		// TODO in counter class
//		return count;
//	}

	private final void flipBonusCard() {//pulls a bonus card 
		state.setBonusCard(deck.removeTopCard());
	}

	private final void deal() {
		Card[] p1Hand = new Card[6];//placeholder variables for hand
		Card[] p2Hand = new Card[6];
		for (int i = 0; i < 6; i++){//adds 6 cards to each player's hand
			p1Hand[i] = deck.removeTopCard();
			p2Hand[i] = deck.removeTopCard();
		}
		state.setHand(p1Hand, CbgState.PLAYER_1);//puts hands in CbgState
		state.setHand(p2Hand, CbgState.PLAYER_2);
	}

	@Override
	protected void sendUpdatedStateTo(GamePlayer p) {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean canMove(int playerIdx) {
		if (playerIdx == turn){
			return true;
		}
		else return false;
	}

	@Override
	protected String checkIfGameOver() {
		if(state.getGameOver()){
			int winner = state.getWinner();
			if(winner == CbgState.PLAYER_1){
				return ("Player 1 won with " + state.getScore(CbgState.PLAYER_1) + " points.");
			}
			else if(winner == CbgState.PLAYER_2){
				return ("Player 2 won with " + state.getScore(CbgState.PLAYER_1) + " points.");
			}
			else return "ERROR";//if something horrible happens
		}

		return "FALSE";//if game is not over
	}
	
	private void switchTurn(){//switches the turn
		if(turn == CbgState.PLAYER_1){
			turn = CbgState.PLAYER_2;
		}
		else if(turn == CbgState.PLAYER_2){
			turn = CbgState.PLAYER_1;
		}
		else Log.i("ERROR", "Player number is broken");
	}

}