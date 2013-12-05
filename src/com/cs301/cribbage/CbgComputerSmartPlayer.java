package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

/**
 * @author Will Christiansen
 * @author Devon Griggs
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * @version 12/2/2013
 */

class CbgComputerSmartPlayer extends CbgComputerPlayer {

	// Instance Variables
	
	public static final int NUM_CARDS_TOTAL = 6;
	public static final int NUM_CARDS_TO_KEEP = 4;
	public static final int NUM_CARDS_TO_THROW = 2;
	public static final int ERROR = -1;
	public static final int SORT_ARRAY_LENGTH = 14; // The 0th element will always have value 0.
	public static final int FIFTEEN = 15;
	public static final int NUM_SUITS = 4;
	public static final int MAX_CARDS_ON_TABLE = 8;
	private CbgState state;
	private GameAction action;
	
	public CbgComputerSmartPlayer(String name) {
		super(name);
	} // Constructor for a CbgComputerSmartPlayer

	/**
	 * This function throws two selected cards from an initial hand of 6
	 * @param Card[] hand  Hand to throw cards from
	 * @param int player  Reference to which player is using this method
	 * @param CbgState state  Current state of the game
	 * @return Card[] toThrow (array of cards selected to throw)
	 */
	
	private Card[] selectedThrow(Card[] hand, int player, CbgState state){
		Card[] handToKeep = findTopHand(hand, player, state);
		Card[] toThrow = new Card[2];
		boolean[] foundMatch = new boolean[6];
		int i = 0;
		int j = 0;
		for (i = 0; i < NUM_CARDS_TOTAL; ++i) {
			foundMatch[i] = false;
		}
		// If a card is found to be in both the hand and the handToKeep,
		// It is noted in the parallel array.
		
		for (i = 0; i < NUM_CARDS_TOTAL; ++i) {
			for (j = 0; j < NUM_CARDS_TO_KEEP; ++j) {
				if (hand[i].equals(handToKeep[j])) {
					foundMatch[i] = true;
					break;
				}
			}
		} // All two cards without pairs are thrown.
		
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
	 * @param Card[] hand  Hand to choose card from
	 * @param Card[] table  Current cards on the table
	 * @return Card bestCard  The selected card
	 */
	
	private Card selectedCardToTable(Card[] hand, Card[] table) {
		int highScore = 0;
		Card bestCard = hand[0];
		for (int i = 0; i < NUM_CARDS_TOTAL; ++i){ // Total number of cards
			if (hand[i] != null) {
				for (int j = 0; j < MAX_CARDS_ON_TABLE; ++j) {
					if (table[j] == null) {
						table[j] = hand[i];
						
						if (highScore < CbgCounter.countTable(table)) {
							highScore = CbgCounter.countTable(table);
							bestCard = table[j];
						}
						table[j] = null;
						break;
					}
				}
			}
		}
		return bestCard;
	}

	/**
	 * This method finds the hand that will score the most points
	 * @param Card[] hand  Hand that has been dealt
	 * @param int player  Reference to which player is using this method
	 * @param CbgState state  The current state
	 * @return Card[] topHand  The top scoring hand
	 */
	
	private Card[] findTopHand(Card[] hand, int player, CbgState state){
		// Check that there are the proper number of cards in the hand
		if (hand.length != NUM_CARDS_TOTAL) return null;
		// Excellent. we were handed the proper parameter

		// Initialize variables
		Card[] topHand = new Card[NUM_CARDS_TO_KEEP];
		Card[] currentHand = new Card[NUM_CARDS_TO_KEEP];
		Card[] toCrib = new Card[NUM_CARDS_TO_THROW];
		int bestScore = 0;

		// Iterate through all unique groups of 4 cards,
		// and call count4(Card hand[], Card throw[]) on each within the loop:	
		for (int i = 0; i < NUM_CARDS_TOTAL - 1; ++i) {
			for (int j = i; j < NUM_CARDS_TOTAL; ++j) {

				// Create this test case hand			
				int handIdx = 0; // For 0 through the 4th element of currentHand 
				int toCribIdx = 0; // For 0 through 1st element of toCrib
				for (int k = 0; k < NUM_CARDS_TOTAL; ++k) {
					if (k != i && k != j) {
						currentHand[handIdx] = hand[k];
						++handIdx;
					} 
					else {
						toCrib[toCribIdx] = hand[k];
						++toCribIdx;
					}
				}

				// Count the test case hand
				if (count4(currentHand, toCrib, player, state) > bestScore) {
					bestScore = count4(currentHand, toCrib, player, state);
					topHand = currentHand;
				}
			}
		}

		// Finally, return the topHand
		return topHand;
	} // End countToThrow(Card card[]) 

	/**
	 * Counts the score when handed a hand of 4 cards
	 * Player represents the player who is counting his cards. This is used to determine if the player owns the crib.
	 * @param Card[] hand  Hand to select card out of
	 * @param Card[] toCrib  Cards being sent to the crib
	 * @param int player  Player reference
	 * @param CbgState state  State of the game
	 * @return int score  Score the player has earned
	 */
	
	public int count4(Card[] hand, Card[] toCrib, int player, CbgState state)
	{
		// Check that there are the proper number of cards in the hand
		if (hand.length != NUM_CARDS_TO_KEEP) return ERROR;
		if (toCrib.length != NUM_CARDS_TO_THROW) return ERROR;
		// Excellent. we were handed the proper parameter

		// Initialize score instance variable
		int score = 0;

		// Create a value ordered array and initialize to zero.
		int[] sort = new int[SORT_ARRAY_LENGTH];
		for (int i = 0; i < SORT_ARRAY_LENGTH; ++i) sort[i] = 0;    

		// This array contains the number of cards of each value type
		for (int i = 0; i < hand.length; ++i){
			sort[hand[i].rank.intRank()] += 1;
		}

		// Let's start by looking for pairs.
		for (int i = 1; i < SORT_ARRAY_LENGTH; ++i) {
			if (sort[i] == 2) score += 2;
			else if (sort[i] == 3) {
				score += 6;
				break; // No more pairs to be found
			} else if (sort[i] == 4) { 
				score += 12;
				break; // No more pairs to be found
			}
		}

		// Look for straights:
		for (int i = 0; i < SORT_ARRAY_LENGTH - 2; ++i) {
			if (sort[i] > 0 && sort[i+1] > 0 && sort[i+2] > 0) {
				// We have a run. is it a run of four?
				if (i+3 < SORT_ARRAY_LENGTH && sort[i+3] > 0) { 
					score += 4;
					break; // No more runs to be found in this hand
				}
				// Is it a double run?
				else if (sort[i] > 1 || sort[i+1] > 1 || sort[i+2] > 1) {
					score += 6;
					break; // No more runs to be found in this hand
				}
				else {
					score += 3;
					break; // No more runs to be found in this hand
				}
			}
		}

		// Look for 15's:
		// First look for 15's in groups of two cards:
		for (int i = 0; i < hand.length - 1; ++i)
			for (int j = i + 1; j < hand.length; ++j)
				if (hand[i].rank.intCountValue() + hand[j].rank.intCountValue() == FIFTEEN) score += 2;

		// Now look for 15's in groups of three cards:
		int tempSum = 0;
		for (int i = 0; i < hand.length; ++i) {
			tempSum = 0;
			for (int j = 0; j < hand.length; ++j) if (j != i) tempSum += hand[j].rank.intCountValue();
			if (tempSum == FIFTEEN) score += 2;
		}

		// Now look for a 15 made of all four cards
		tempSum = 0;
		for (int i = 0; i < hand.length; ++i) tempSum += hand[i].rank.intCountValue();
		if (tempSum == FIFTEEN) score += 2;

		// Look for a flush
		int flushSuit = hand[0].suit.intSuit();
		boolean isFlush = true;
		for (int i = 1; i < hand.length; ++i) {
			if (flushSuit != hand[i].suit.intSuit()) {
				isFlush = false;
				break;
			}
		}
		if (isFlush) score += 4;        

		// Subtract crib throw adjustment
		// Check for a pair:
		if (toCrib[0].rank.intRank() == toCrib[1].rank.intRank()) {
			if (player == state.cribOwner) score += 2;
			else score -= 2;
		}

		// Check for a 15
		else if (toCrib[0].rank.intCountValue() + toCrib[1].rank.intCountValue() == 15) { // If pair, then a 15 is impossible.
			if (player == state.cribOwner) score += 2; 
			else score -= 2;
		}

		return score;
	} // End count4

	/**
	 * Receives info from the game and if it is the computers turn, calls the method to select 
	 * Cards to throw or to place on the table
	 */
	
	@Override
	protected void receiveInfo(GameInfo info) {
		if(info instanceof CbgState){
			state = (CbgState)info;	
			if(state.getTurn() == CbgState.PLAYER_2) {
				takeTurn();
			} // Else do nothing
		}
	}

	/**
	 * Method that determines what cards or card to play
	 */
	
	private void takeTurn(){
		action = null;		
		// Fill the action depending on the stage.
		if(state.getGameStage() == CbgState.THROW_STAGE){
			action = new CardsToThrow(this,selectedThrow(state.getHand(), CbgState.PLAYER_2, state));
		}
		else if (state.getGameStage() == CbgState.PEG_STAGE){
			action = new CardsToTable(this, selectedCardToTable(state.getHand(), state.getTable().toArray(new Card[8])));
			sleep((int) (Math.random()*1000)); // Sleep up to one second to avoid stressing out the player
		}
		game.sendAction(action); // Sends action
	}
}