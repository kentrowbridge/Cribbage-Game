package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * 
 * @author Devon Griggs
 * @version 11/24
 *
 */

class CbgComputerSmartPlayer extends CbgComputerPlayer {

	public static final int NUM_CARDS_TOTAL = 6;
	public static final int NUM_CARDS_TO_KEEP = 4;
	public static final int NUM_CARDS_TO_THROW = 2;
	public static final int ERROR = -1;
	public static final int SORT_ARRAY_LENGTH = 14; // the 0th element will always have value 0.
	public static final int FIFTEEN = 15;
	public static final int NUM_SUITS = 4;
	public static final int MAX_CARDS_ON_TABLE = 8;
	private CbgState state;
	private GameAction action;
	public CbgComputerSmartPlayer(String name)
	{
		super(name);

	}
	/*private final int count4(Card[] table) {
		int count = 0;

		return count;
	}*/


	/**
	 * This function throws two selected cards from an initial hand of 6
	 */


	Card[] selectedThrow(Card[] hand, int player, CbgState state){
		Card[] handToKeep = findTopHand(hand, player, state);
		Card[] toThrow = new Card[2];
		boolean[] foundMatch = new boolean[6];
		int i = 0;
		int j = 0;
		for (i = 0; i < NUM_CARDS_TOTAL; ++i) {
			foundMatch[i] = false;
		}
		// if a card is found to be in both the hand and the handToKeep,
		// it is noted in the parallel array.
		for (i = 0; i < NUM_CARDS_TOTAL; ++i) {
			for (j = 0; j < NUM_CARDS_TO_KEEP; ++j) {
				if (hand[i].equals(handToKeep[j])) {
					foundMatch[i] = true;
					break;
				}
			}
		}
		// all two cards without pairs are thrown.
		for (i = 0, j = 0; i < NUM_CARDS_TOTAL; ++i) {
			if (!foundMatch[i]) {
				toThrow[j] = hand[i];
				++j;
			}
			if (j == NUM_CARDS_TO_THROW) break;
		}        
		return toThrow;
	}

	/**
	 * This function plays a selected card to the table
	 */
	Card selectedCardToTable(Card[] hand, Card[] table){
		int i = 0;
		int highScore = 0;
		Card bestCard = hand[0];
		for (i = 0; i < NUM_CARDS_TOTAL; ++i){
			if (hand[i] != null) {
				for (i = 0; i < MAX_CARDS_ON_TABLE; ++i){
					if (table[i] == null) {
						table[i] = hand[i];
						if(highScore < CbgCounter.countTable(table)) {
							highScore = CbgCounter.countTable(table);
							bestCard = table[i];
						}
						table[i] = null;
						break;
					}
				}
			}
		}
		return bestCard;
	}


	public Card[] findTopHand(Card[] hand, int player, CbgState state) 
	{
		// check that there are the proper number of cards in the hand
		if (hand.length != NUM_CARDS_TOTAL) return null;
		// excellent. we were handed the proper parameter

		// initialize variables
		Card[] topHand = new Card[NUM_CARDS_TO_KEEP];
		Card[] currentHand = new Card[NUM_CARDS_TO_KEEP];
		Card[] toCrib = new Card[NUM_CARDS_TO_THROW];
		int bestScore = 0;

		// iterate through all unique groups of 4 cards
		// and call count4(Card hand[], Card throw[]) on each
		// within the loop:
		int i = 0;
		int j = 0;
		for (i = 0; i < NUM_CARDS_TOTAL - 1; ++i) {
			for (j = i; j < NUM_CARDS_TOTAL; ++j) {

				// create this test case hand
				int k = 0; // for all six elements of hand
				int handIdx = 0; // for 0 thru 4th element of currentHand 
				int toCribIdx = 0; // for 0 thru 1st element of toCrib
				for (k = 0; k < NUM_CARDS_TOTAL; ++k) {
					if (k != i && k != j) {
						currentHand[handIdx] = hand[k];
						++handIdx;
					} else {
						toCrib[toCribIdx] = hand[k];
						++toCribIdx;
					}
				}

				// count the test case hand
				if (count4(currentHand, toCrib, player, state) > bestScore) {
					bestScore = count4(currentHand, toCrib, player, state);
					topHand = currentHand;
				}
			}
		}

		// finally, return topHand
		return topHand;
	} // end countToThrow(Card card[]) 

	// counts the score when handed a hand of 4 cards
	// player represents the player who is counting his cards. This is used to determine if the player owns the crib.
	public int count4(Card[] hand, Card[] toCrib, int player, CbgState state)
	{
		// check that there are the proper number of cards in the hand
		if (hand.length != NUM_CARDS_TO_KEEP) return ERROR;
		if (toCrib.length != NUM_CARDS_TO_THROW)return ERROR;
		// excellent. we were handed the proper parameter

		// initialize score instance variable
		int score = 0;

		// create a value ordered array and initialize to zero.
		int[] sort = new int[SORT_ARRAY_LENGTH];
		int i = 0; // iterator
		for (i = 0; i < SORT_ARRAY_LENGTH; ++i) sort[i] = 0;    

		// this array contains the number of cards of each value type
		for (i = 0; i < hand.length; ++i) sort[hand[i].rank.intRank()] += 1;

		// let's start by looking for pairs.
		for (i = 1; i < SORT_ARRAY_LENGTH; ++i) {
			if (sort[i] == 2) score += 2;
			else if (sort[i] == 3) {
				score += 6;
				break; // no more pairs to be found
			} else if (sort[i] == 4) { 
				score += 12;
				break; // no more pairs to be found
			}
		}

		// look for straights
		for (i = 0; i < SORT_ARRAY_LENGTH - 2; ++i) {
			if (sort[i] > 0 && sort[i+1] > 0 && sort[i+2] > 0) {

				// we have a run. is it a run of four?
				if (i+3 < SORT_ARRAY_LENGTH && sort[i+3] > 0) { 
					score += 4;
					break; // no more runs to be found in this hand
				}

				// is it a double run?
				else if (sort[i] > 1 || sort[i+1] > 1 || sort[i+2] > 1) {
					score += 6;
					break; // no more runs to be found in this hand
				}

				else {
					score += 3;
					break; // no more runs to be found in this hand
				}
			}
		}

		// look for 15s
		// first look for 15s in groups of two cards
		int j = 0; // new iterator
		for (i = 0; i < hand.length - 1; ++i)
			for (j = i + 1; j < hand.length; ++j)
				if (hand[i].rank.intCountValue() + hand[j].rank.intCountValue() == FIFTEEN) score += 2;

		// now look for 15s in groups of three cards
		int tempSum = 0;
		for (i = 0; i < hand.length; ++i) {
			tempSum = 0;
			for (j = 0; j < hand.length; ++j) if (j != i) tempSum += hand[j].rank.intCountValue();
			if (tempSum == FIFTEEN) score += 2;
		}

		// now look for a 15 made of all four cards
		tempSum = 0;
		for (i = 0; i < hand.length; ++i) tempSum += hand[i].rank.intCountValue();
		if (tempSum == FIFTEEN) score += 2;

		// look for a flush
		int flushSuit = hand[0].suit.intSuit();
		boolean isFlush = true;
		for (i = 1; i < hand.length; ++i) {
			if (flushSuit != hand[i].suit.intSuit()) {
				isFlush = false;
				break;
			}
		}
		if (isFlush) score += 4;        

		// subtract crib throw adjustment
		// check for a pair
		if (toCrib[0].rank.intRank() == toCrib[1].rank.intRank()) {
			if (player == state.cribOwner) score += 2;
			else score -= 2;
		}

		// check for a 15
		else if (toCrib[0].rank.intCountValue() + toCrib[1].rank.intCountValue() == 15) {// if pair, then 15 impossible.
			if (player == state.cribOwner) score += 2; 
			else score -= 2;
		}

		return score;
	} // end count4(Card hand[])


	@Override
	protected void receiveInfo(GameInfo info) {
		if(info instanceof CbgState){
			state = (CbgState)info;	
			if(state.getTurn() == state.PLAYER_2) {
				takeTurn();
			}// else do nothing
		}
	}

	private void takeTurn(){
		action = null;
		if(state.getGameStage() == CbgState.THROW_STAGE){
			action = new CardsToThrow(this,selectedThrow(state.getHand(), CbgState.PLAYER_2, state));
		}
		else if (state.getGameStage() == CbgState.PEG_STAGE){
			action = new CardsToTable(this, selectedCardToTable(state.getHand(), state.getTable().toArray(new Card[8])));

		}

	}
}
