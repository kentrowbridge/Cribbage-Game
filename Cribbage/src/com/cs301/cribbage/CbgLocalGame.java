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
	private GamePlayer p1;
	private GamePlayer p2;


	public CbgLocalGame(){
		deck = new Deck();//creates a deck
		state = new CbgState();
		counter = new CbgCounter();
		turn = CbgState.PLAYER_1;
		state.setGameOver(false);
		
		//gameCycle();

	}

	private void gameCycle()
	{
//		while (!state.getGameOver())
//		{
			state.tableArray = new ArrayList<Card>();
			deck.add52().shuffle();// creates a shuffled deck for each round
			state.setDeck(deck); 
			deal();
			throwCount = 0;
			tallyCount = 0;//reset tracker variables
			state.setGameStage(state.THROW_STAGE);
			sendUpdatedStateTo(p1);
			sendUpdatedStateTo(p2);

			
//			while (state.getGameStage() == state.THROW_STAGE)
//			{
//				if(throwCount >= 2){//throwcount updated each time a person throws their set of 
//					//2 cards as it is only a single action
//					state.setGameStage(state.PEG_STAGE);//each time a player throws a card, 
//					//the throwcount is updated, when it hits 2 it moves on to the next stage
//				}
//			}
//			
//			//once the game is in the pegging stage, flips the top card of deck
//			if (state.getGameStage() == state.PEG_STAGE) 
//				state.getBonusCard();
//			
//			while (state.getGameStage() == state.PEG_STAGE)
//			{
//				if(state.getGameOver()) break;//TODO initiate game over sequence
//				
//				if (state.tableArray.size() == 8)
//					state.setGameStage(state.COUNT_STAGE);
//				
//				originalTally = state.getTally();
//				state.setTally(counter.getTally((Card[])state.getTable().toArray()));
//			}
//			while (state.getGameStage() == state.COUNT_STAGE)
//			{
//				//after the score has been tallied, checks if the game is over
//				if(state.getGameOver()) break;
//				CbgCounter counter = new CbgCounter();
//				
//				if (state.cribOwner == state.PLAYER_1) {
//					state.setScore(state.player2Score + counter.count5(state.getHand(state.PLAYER_2), state.getBonusCard()), state.PLAYER_2);// counting player 2 hand
//					checkIfGameOver();
//					state.setScore(state.player1Score + counter.count5(state.getHand(state.PLAYER_1), state.getBonusCard()), state.PLAYER_1);// counting player 1 hand
//					state.setScore(state.player1Score + counter.count5(state.getCrib(), state.getBonusCard()), state.PLAYER_1);// counting crib hand for player 1 
//					checkIfGameOver();
//				} else {
//					state.setScore(state.player1Score + counter.count5(state.getHand(state.PLAYER_1), state.getBonusCard()), state.PLAYER_1);
//					checkIfGameOver();
//					state.setScore(state.player2Score + counter.count5(state.getHand(state.PLAYER_2), state.getBonusCard()), state.PLAYER_2);
//					state.setScore(state.player2Score + counter.count5(state.getCrib(), state.getBonusCard()), state.PLAYER_2);
//					checkIfGameOver();
//				}
//				state.gameStage = state.THROW_STAGE;
//			}
			
		}//while (!state.getGameOver())
	//}//gameCycle

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
	protected final boolean makeMove(GameAction action) {//TODO remember to null the card in the person's hand
		if (action instanceof CardsToTable && canMove(getPlayerIdx(action.getPlayer()))){
			//if card to table action and player can move
			ArrayList<Card> cardArr = new ArrayList<Card>(); 
			CardsToTable cards = (CardsToTable) action;
			cardArr = state.getTable();  // get table
			
			// is the card a valid card to play? If so, proceed. If not, gracefully take care of the situation.
			if (cards.cards().getRank().intCountValue() + state.tally > 31) {
				// The card is invalid
				//TODO insert text sent to the text field here to inform the player of the error.

			} else {
				// The card is valid
				cardArr.add(cards.cards()); //add card
				state.setTable(cardArr); //send back to gamestate
				state.tally += cards.cards().getRank().intCountValue();
				sendUpdatedStateTo(action.getPlayer());
				
				// update the score
				if (getPlayerIdx(action.getPlayer()) == state.PLAYER_1) 
					state.player1Score += counter.countTable((Card[])cardArr.toArray()); 
				else state.player2Score += counter.countTable((Card[])cardArr.toArray());
				
				// check for a 31
				if(state.tally == 31) {
					if (getPlayerIdx(action.getPlayer()) == state.PLAYER_1)
						state.player1Score += 2;
					else state.player2Score += 2;
					
				} else {// check for the go
					boolean getGoPoint = true;
					for (int i = 0; i < 8; ++i) {
						
						// if player 2 was the last to play, and the particular card in the hand is not null, and that card would be a valid card, no go point is awarded
						if (getPlayerIdx(action.getPlayer()) == state.PLAYER_2 && state.getHand(state.PLAYER_2)[i] != null && state.getHand(state.PLAYER_2)[i].getRank().intCountValue() + state.tally <= 31) {
							getGoPoint = false;
							break;
						}
						if (getPlayerIdx(action.getPlayer()) == state.PLAYER_1 && state.getHand(state.PLAYER_2)[i] != null && state.getHand(state.PLAYER_2)[i].getRank().intCountValue() + state.tally <= 31) {
							getGoPoint = false;
							break;
						}
					}
					if (getGoPoint) {
						if (getPlayerIdx(action.getPlayer()) == state.PLAYER_1)
							state.player1Score += 1;
						if (getPlayerIdx(action.getPlayer()) == state.PLAYER_2)
							state.player2Score += 1;
					}
				}
				sendUpdatedStateTo(action.getPlayer());	
				checkIfGameOver();
				
				switchTurn();
			}
			// send the state back to the player
			sendUpdatedStateTo(action.getPlayer());// to change the GUI accordingly
			// note that the state has not changed since the card was invalid
			
			// the action has been handled correctly
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
		else if (action instanceof CheckInAction){
			CheckInAction check = (CheckInAction) action;
			if(getPlayerIdx(check.getPlayer()) == 0){
				p1 = check.getPlayer();
			}
			if(getPlayerIdx(check.getPlayer()) == 1){
				p2 = check.getPlayer();
				
			}
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
		p.sendInfo(new CbgState(state));

	}

	@Override
	protected boolean canMove(int playerIdx) {
		if (playerIdx == turn){
			return true;
		}
		else return false;
	}

	@Override
	protected String checkIfGameOver() {// TODO Will: change this accordingly.
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