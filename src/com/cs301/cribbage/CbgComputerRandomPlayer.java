package com.cs301.cribbage;

import android.util.Log;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * 
 * @author Devon Griggs
 * @version 11/24
 *
 */

class CbgComputerRandomPlayer extends CbgComputerPlayer {
	CbgState state;
	private GameAction action;//action that will be sent to the game


	public CbgComputerRandomPlayer(String name)
	{
		super(name);
	}

	/**
	 * This function throws two random cards from an initial hand of 6
	 */
	Card[] throwCards(Card[] hand){
		int rand1 = (int)Math.random()*hand.length; // oracle to remind myself how to make a random number.
		int rand2 = (int)Math.random()*hand.length;
		while(rand1 == rand2) {
			rand2 = (int)Math.random()*hand.length;
		}
		Card[] toThrow = new Card[2];
		toThrow[0] = hand[rand1];
		toThrow[1] = hand[rand2];
		return toThrow;        
	}

	/**
	 * This function plays a random card to the table
	 */
	Card cardsToTable(Card[] hand){
		int rand1 = (int)Math.random()*hand.length; // oracle to remind myself how to make a random number.
		// return the randomly chosen element if not null. 
		// if null, loop through the array until a non-null element is found and then retrun.
		while (hand[rand1] == null) {
			rand1 = (rand1 + 1)%6;
		}
		return hand[rand1];
	}

	@Override
	protected void receiveInfo(GameInfo info) {
		Log.i("CPU","getting state...");
		if(info instanceof CbgState){
			state = (CbgState)info;	
			Log.i("CPU","got state...");
			if(state.getTurn() == state.PLAYER_2) {
				Log.i("CPU","taking turn...");
				takeTurn();
			}// else do nothing
		}
	}
	
	private void takeTurn(){
		action = null;
		if(state.getGameStage() == CbgState.THROW_STAGE){
			action = new CardsToThrow(this, throwCards(state.getHand()));
		}
		else if (state.getGameStage() == CbgState.PEG_STAGE){
			action = new CardsToTable(this, cardsToTable(state.getHand()));

		}
		
		
		game.sendAction(action);
	}
}
