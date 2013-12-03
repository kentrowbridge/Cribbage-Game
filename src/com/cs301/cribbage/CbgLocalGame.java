package com.cs301.cribbage;
import java.util.ArrayList;

import android.util.Log;

import edu.up.cs301.card.*;
import edu.up.cs301.game.*;
import edu.up.cs301.game.actionMsg.GameAction;

class CbgLocalGame extends LocalGame{

	private Deck deck;
	private CbgState state;
	private int throwCount;

	public CbgLocalGame(){
		state = new CbgState();
		deck = new Deck();
		deck.add52().shuffle();// creates a shuffled deck for each round
		deal();
		state.setTurn(CbgState.PLAYER_1);
		state.setGameOver(false);
	}


	/*
	 * Checks the boolean isGameOver in gameState, and if its true it sets the return string 
	 * to reflect the final score and the winner.
	 */

	private final boolean isValidTableCard(Card card) {
		if(card != null && card.getRank().intCountValue() + state.getTally() > 31){
			return false;
		}
		else return true;
	}


	private final boolean canPlay(int player, Card card){//checks if a player can play
		Card[] hand = state.getHand(player);
		for(Card c : hand){
			if(c != null && c != card){//if not null or the card just played
				if(c.getRank().intCountValue() + state.getTally() <= 31){//and they have at least one card that will keep the tally at or under 31
					return true;
				}
			}
		}
		return false;

	}
	private final boolean outOfCards(int player, Card card){
		Card[] hand = state.getHand(player);
		for(Card c : hand){
			if(c != null && c != card){//if not 
				return false;//if it has a non-null card and not the on just played (which will be nulled when makeMove returns
			}
		}
		return true;//if all (will be) empty
	}

	private final int otherPlayer(int player){
		if(player == CbgState.PLAYER_1){
			return CbgState.PLAYER_2;
		}
		else{
			return CbgState.PLAYER_1;
		}
	}
	@Override
	protected final boolean makeMove(GameAction action) {

		if (action instanceof CardsToTable){

			//if card to table action and player can move
			ArrayList<Card> cardArr = state.getTable();//saves current table
			CardsToTable cards = (CardsToTable) action;//saves action
			Card card = cards.cards();
			int cardVal = card.getRank().intCountValue();
			int newTally = 0;
			int oldTally = state.getTally();
			int player = getPlayerIdx(action.getPlayer());
			cardArr.add(card); //add card to table
			state.setTable(cardArr);			
//			if((oldTally + cardVal) > 31){
//				newTally = cardVal;
//			}
			if((oldTally + cardVal) == 31){
				newTally = 0;
				state.addScore(2, player);
			}
			else {
				newTally = oldTally + cardVal;
			}
			if(newTally == 15){
				state.addScore(2, player);
			}
			
			state.setTally(newTally);

			state.addScore(CbgCounter.countTable(cardArr.toArray(new Card[8])), player); 
			
			if(outOfCards(player, card) && outOfCards(otherPlayer(player), card)){//if both players out of cards
				state.setGameStage(CbgState.COUNT_STAGE);
				count();
				return true;
			}
			
			if(!canPlay(otherPlayer(player), card)){//if other player can't play
				state.addScore(1, player);//add 1 to player score
				if(!canPlay(player, card)){//sets tally to 0 if player can't play
					state.setTally(0);
				}
				else{
				switchTurn();//switches turn so it will switch back at end
				}
			}
			checkIfGameOver();
			switchTurn();
			return true;
		}
		//
		//			// is the card a valid card to play? If so, proceed. If not, gracefully take care of the situation.
		//			if (!isValidTableCard(cards.cards())) {
		//				switchTurn();//switches whose turn it is`
		//				//sendAllUpdatedState();//sends updated state to all players
		//				return true;
		//				// If the card is invalid, switch turn
		//			} 
		//			else {//if action is a valid throw action
		//				cardArr.add(cards.cards()); //add card to table
		//				state.setTable(cardArr); //send back to gamestate
		//				int newTally = 0;
		//				if(state.getTally() + cards.cards().getRank().intCountValue() > 31){
		//					newTally = cards.cards().getRank().intCountValue();
		//				}
		//				else if(state.getTally() == 31){
		//					newTally = 0;
		//				}
		//				else {
		//					newTally = state.getTally() + cards.cards().getRank().intCountValue();
		//				}
		//				//calculates new tally
		//				state.setTally(newTally);//sets tally to new value				
		//
		//				// update the score
		//				if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_1){ //if player 1
		//					newP1Score += CbgCounter.countTable(cardArr.toArray(new Card[8]));//adds to new score
		//				} 
		//				else {
		//					newP2Score += CbgCounter.countTable(cardArr.toArray(new Card[8]));//adds to new score
		//				}
		//
		//				// check for a 31
		//				if(state.getTally() == 31) {// if the last card sets tally to exactly 31
		//
		//					if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_1){
		//						newP1Score += 2;//adds two to that player
		//					}else{
		//						newP2Score += 2;//adds two to other player
		//					}
		//
		//				}else if(state.getTally() < 31){// check for the go
		//					boolean getGoPoint = true;
		//					for (int i = 0; i < state.getHand().length; ++i) {//iterate through player hand
		//
		//						// if player 2 was the last to play, and the particular card in the hand is not null, and that card would be a valid card, no go point is awarded
		//						if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_1 &&//if player 1
		//								state.getHand(CbgState.PLAYER_1)[i] != null &&//if current card is not null 
		//								(state.getHand(CbgState.PLAYER_1)[i].getRank().intCountValue() + state.getTally()) <= 31) {
		//							getGoPoint = false;
		//							break;
		//						}					
		//						if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_2 &&//if player 2
		//								state.getHand(CbgState.PLAYER_2)[i] != null && //if current card is not null
		//								(state.getHand(CbgState.PLAYER_2)[i].getRank().intCountValue() + state.getTally()) <= 31) {//if the added value of the next card does not exceed 31
		//							getGoPoint = false;
		//							break;
		//						}
		//					}
		//
		//					if (getGoPoint) {//if player can't play valid card, add point to opposing player
		//
		//
		//						if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_2)
		//							newP2Score += 1; //TODO its right that when this player has to go, the other gets a point?
		//						if (getPlayerIdx(action.getPlayer()) == CbgState.PLAYER_1)
		//							newP1Score += 1;
		//
		//						boolean canPlay = false;
		//						int playerIdx = getPlayerIdx(action.getPlayer()); 
		//						for (int i = 0; i < state.getHand().length; ++i) {//iterate through player hand
		//
		//							// if player 2 was the last to play, and the particular card in the hand is not null, and that card would be a valid card, no go point is awarded
		//							if (state.getHand(playerIdx)[i] != null &&//if current card is not null 
		//									(state.getHand(playerIdx)[i].getRank().intCountValue() + state.getTally()) <= 31) {
		//								canPlay = true;
		//							}
		//						}
		//
		//						if(canPlay){//if they can play again at a go point
		//							switchTurn();//switch turn so it'll switch back when called at end
		//						}
		//					}
		//
		//					state.setScore(newP1Score, CbgState.PLAYER_1);
		//					state.setScore(newP2Score, CbgState.PLAYER_2);
		//
		//					checkIfGameOver();//checks if game is over
		//					if(checkCountStage()){
		//						state.setGameStage(CbgState.COUNT_STAGE);
		//					}
		//					if(state.getGameStage() == CbgState.COUNT_STAGE){
		//						count();
		//					}				
		//					//sets the new score for each player
		//
		//
		//					switchTurn();//switches whose turn it is
		//					//sendAllUpdatedState();//sends updated state to all players
		//					return true;
		//				}
		//			}
		//		}

		else if (action instanceof CardsToThrow ){//TODO make so checks for can move
			CardsToThrow cards = (CardsToThrow) action;
			Card[] cribArr = state.getCrib();//cards to set the crib
			Card[] cardsThrown = cards.cards();//cards from most recent action
			for(int i = 0; i < cardsThrown.length; i++){
				for(int j = 0; j<cribArr.length;j++){
					if(cribArr[j]==null){
						cribArr[j] = cardsThrown[i];
						break;
					}
				}
			}
			state.setCrib(cribArr);
			throwCount++;
			if(throwCount >= 2){
				flipBonusCard();
				state.setGameStage(CbgState.PEG_STAGE);
			}
			switchTurn();
			//sendAllUpdatedState();
			return true;
		}
		return false;
	}

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
		if (state == null) {
			return;
		}

		CbgState orig = new CbgState(state);//creates a copy of the current state
		orig.setHand(state.getHand(getPlayerIdx(p)));//sets the copy states player hand to the current states hand
		orig.setHand(null, CbgState.PLAYER_1);//nulls out hands TODO should this really be nulled out?
		orig.setHand(null, CbgState.PLAYER_2);//nulls out hands 
		orig.setTurn(state.getTurn());
		orig.setGameStage(state.getGameStage());

		p.sendInfo(orig);	
	}



	@Override
	protected boolean canMove(int playerIdx) {
		//TODO
		if (playerIdx == state.getTurn()){
			if(state.getGameStage() == CbgState.THROW_STAGE){
				return true;
			}
			else if(state.getGameStage() == CbgState.PEG_STAGE){
				boolean canPlay = false;
				for (int i = 0; i < state.getHand().length; ++i) {//iterate through player hand

					// if player 2 was the last to play, and the particular card in the hand is not null, and that card would be a valid card, no go point is awarded
					if (state.getHand(playerIdx)[i] != null &&//if current card is not null 
							(state.getHand(playerIdx)[i].getRank().intCountValue() + state.getTally()) <= 31) {
						canPlay = true;
					}
				}
				return canPlay;

			}
			else return false;
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

		return null;//if game is not over
	}



	private void switchTurn(){//switches the turn
		if(state.getTurn() == CbgState.PLAYER_1){
			state.setTurn(CbgState.PLAYER_2);
		}
		else if(state.getTurn() == CbgState.PLAYER_2){//swaps turn
			state.setTurn(CbgState.PLAYER_1);
		}
		else Log.i("ERROR", "Player number is broken");
	}

	//TODO what happens when player can't play a card and that card is kept in his hand?
	private boolean checkCountStage(){
		boolean isEmpty = true;
		for(Card c : state.getHand(CbgState.PLAYER_1)){
			if(c != null){
				isEmpty = false;
			}
		}
		for(Card c : state.getHand(CbgState.PLAYER_2)){
			if(c != null){
				isEmpty = false;
			}
		}
		return isEmpty;
	}

	private void count(){	
		//after the score has been tallied, checks if the game is over
		if(state.getGameOver()) return;
		if (state.getCribOwner() == CbgState.PLAYER_1) {
			state.setScore(state.getScore(CbgState.PLAYER_2) + CbgCounter.count5(state.getHand(CbgState.PLAYER_2), 
					state.getBonusCard()), CbgState.PLAYER_2);// counting player 2 hand

			checkIfGameOver();

			state.setScore(state.getScore(CbgState.PLAYER_1) + CbgCounter.count5(state.getHand(CbgState.PLAYER_1), 
					state.getBonusCard()), CbgState.PLAYER_1);// counting player 1 hand
			state.setScore(state.getScore(CbgState.PLAYER_1) + CbgCounter.count5(state.getCrib(), 
					state.getBonusCard()), CbgState.PLAYER_1);// counting crib hand for player 1 
			checkIfGameOver();
		} else {
			state.setScore(state.getScore(CbgState.PLAYER_1) + CbgCounter.count5(state.getHand(CbgState.PLAYER_1), 
					state.getBonusCard()), CbgState.PLAYER_1);

			checkIfGameOver();

			state.setScore(state.getScore(CbgState.PLAYER_2) + CbgCounter.count5(state.getHand(CbgState.PLAYER_2), 
					state.getBonusCard()), CbgState.PLAYER_2);
			state.setScore(state.getScore(CbgState.PLAYER_2) + CbgCounter.count5(state.getCrib(), 
					state.getBonusCard()), CbgState.PLAYER_2);
			checkIfGameOver();
		}
		newRound();
		
	}


	/**
	 * 
	 */
	private void newRound() {
		state.setGameStage(CbgState.THROW_STAGE); 
		state.setDeck(new Deck());
		state.setTable(new ArrayList<Card>());
		state.setCrib(new Card[4]);
		deal();
		throwCount = 0;
		state.setTally(0);
		sleep(5000);
	}


	private void sleep(int i) {
		// TODO Auto-generated method stub
		
	}
}