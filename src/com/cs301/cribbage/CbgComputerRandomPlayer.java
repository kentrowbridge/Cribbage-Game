package com.cs301.cribbage;

import edu.up.cs301.card.Card;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * 
 * @author Devon Griggs
 * @author Kenny Trowbridge
 * @version 12/1/2013
 *
 */

class CbgComputerRandomPlayer extends CbgComputerPlayer {
	private CbgState state;
	private GameAction action;//action that will be sent to the game


	public CbgComputerRandomPlayer(String name)
	{
		super(name);
	}

	/**
	 * This function throws two random cards from an initial hand of 6
	 */
	Card[] throwCards(Card[] hand){
		int rand1 = (int)(Math.random()*hand.length); // oracle to remind myself how to make a random number.
		int rand2 = (int)(Math.random()*hand.length);
		while(rand1 == rand2) {
			rand2 = (int)(Math.random()*hand.length);
		}
		Card[] toThrow = new Card[2];
		toThrow[0] = hand[rand1];
		toThrow[1] = hand[rand2];
		hand[rand1] = null;
		hand[rand2] = null;
		return toThrow;        
	}



	private int indexOfCard(Card[] a, Card c){
		for(int i =0; i<a.length;i++){//iterate through array searching for card
			if(a[i]==c){
				return i;//index of card touched
			}
		}
		return -1;//error value
	}

	/**
	 * Determines randomly which card in its hand to play to the table
	 */


	private Card cardsToTable(Card[] hand){

		////Likely unneeded
		//		// check that we don't have all of the hand be null.
		//		boolean isAllNull = true;
		//		for (int i = 0; i < hand.length; ++i){
		//			if (hand[i] != null) {
		//				isAllNull = false;
		//				break;
		//			}
		//		}
		//		
		//		//if the computer has no more cards left in hand
		//		if (isAllNull){
		//			return null;
		//		}

		//check if the computers hand has a card that can be played
		//		boolean hasValidCard = false;
		//		for (int i = 0; i < hand.length; ++i){
		//			Card card = hand[i];
		//			if(card != null && card.getRank().intCountValue() + state.getTally() <= 31){
		//				hasValidCard = true;
		//				break;
		//			}
		//		}
		//		if (!hasValidCard) return null;

		int rand1 = (int)Math.random()*hand.length;
		boolean canPlay = false;

		// oracle to remind myself how to make a random number.
		// return the randomly chosen element if not null. 
		// if null, loop through the array until a non-null element is found and then retrun.
		while (hand[rand1] == null && !canPlay) {

			rand1 = (rand1 + 1)%6;
			canPlay = CbgCounter.canMove(hand[rand1], state);
		}

		return hand[rand1];
	}

	@Override
	protected void receiveInfo(GameInfo info) {
		if(info instanceof CbgState){
			state = (CbgState)info;	
			int turn = state.getTurn();
			if(state.getTurn() == CbgState.PLAYER_2) {
				takeTurn();
			}// else do nothing
		}
	}

	/**
	 * Method to determine what to play during the computer's turn
	 */
	private void takeTurn(){
		action = null;
		Card card = null;
		if(state.getGameStage() == CbgState.THROW_STAGE){//if throw stage
			action = new CardsToThrow(this, throwCards(state.getHand()));//pick two cards to throw and save them into 
			//a CardsToThrow action
		}
		else if (state.getGameStage() == CbgState.PEG_STAGE){
			card = cardsToTable(state.getHand());
			action = new CardsToTable(this, card);//pick one card and save it to 
			//a CardsToTable action


			sleep((int) (Math.random()*1000));//sleep up to one second
		}		
		game.sendAction(action);//sends action
		if(card != null){
			int cardPos = indexOfCard(state.getHand(), card);
			if (cardPos >=0 && cardPos < state.getHand().length){
				Card[] hand = state.getHand();
				hand[cardPos] = null;
				state.setHand(hand);//gets index of card played and removes the card
			}
		}
	}
}