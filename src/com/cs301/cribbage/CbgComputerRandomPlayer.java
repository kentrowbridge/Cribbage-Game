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
		return toThrow;        
	}

	/**
	 * Determines randomly which card in its hand to play to the table
	 */
	Card cardsToTable(Card[] hand){
		int rand1 = (int)(Math.random()*hand.length); // oracle to remind myself how to make a random number.
		// return the randomly chosen element if not null. 
		// if null, loop through the array until a non-null element is found and then retrun.
		while (hand[rand1] == null) {
			rand1 = (rand1 + 1)%6;
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
		if(state.getGameStage() == CbgState.THROW_STAGE){//if throw stage
			action = new CardsToThrow(this, throwCards(state.getHand()));//pick two cards to throw and save them into 
																		//a CardsToThrow action
		}
		else if (state.getGameStage() == CbgState.PEG_STAGE){
			action = new CardsToTable(this, cardsToTable(state.getHand()));//pick one card and save it to 
																		//a CardsToTable action
			sleep((int) (Math.random()*1000));//sleep up to one second
		}		
		game.sendAction(action);//senda action
	}
}