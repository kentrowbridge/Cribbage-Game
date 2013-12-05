package com.cs301.cribbage;

import edu.up.cs301.card.Card;

//Static methods, call from CbgCounter
public class CbgCounter { 

	public static final int NUM_CARDS_TOTAL = 6;
	public static final int NUM_CARDS_TO_KEEP = 4;
	public static final int NUM_CARDS_TO_THROW = 2;
	public static final int ERROR = -1;
	public static final int SORT_ARRAY_LENGTH = 14; // the 0th element will always have value 0.
	public static final int FIFTEEN = 15;
	public static final int NUM_SUITS = 4;
	public static final int MAX_CARDS_ON_TABLE = 8;


	/**
	 * This function throws two selected cards from an initial hand of 6
	 * 
	 * @param hand  the hand the is being counted
	 * @param player  the player that is calling the count
	 * @para state  the entire gameState. This is so that the cribOwner may be extracted.
	 */
	// counts the score when handed a hand of 5 cards (including the bonus card)

	static boolean canMove(Card card,CbgState state){//checks if a player can play
		if(card != null && card.getRank().intCountValue() + state.getTally() <= 31){
			return true;
		}
		return false;
	}
	
	static boolean isValidTableCard(Card card, CbgState state) {
		if(card != null && card.getRank().intCountValue() + state.getTally() > 31){
			return false;
		}
		else return true;
	}
	
	/**
	 * Counts the points that would results from the given hand with the bonus card
	 * @param hand  hand to count
	 * @param bonusCard  bonus card to count 
	 * @return  score from count
	 */
	static int count5(Card hand[], Card bonusCard)
	{
		// check that there are the proper number of cards in the hand
		if (hand.length != NUM_CARDS_TO_KEEP) return ERROR;
		// excellent. we were handed the proper parameter

		// initialize score instance variable
		int score = 0;

		// create a value ordered array and initialize to zero.
		int[] sort = new int[SORT_ARRAY_LENGTH];
		for (int i = 0; i < SORT_ARRAY_LENGTH; ++i) sort[i] = 0;    

		// this array contains the number of cards of each value type
		for (int i = 0; i < hand.length; ++i) sort[hand[i].rank.intRank()] += 1;
		sort[bonusCard.rank.intRank()] += 1; 

		// let's start by looking for pairs.
		for (int i = 1; i < SORT_ARRAY_LENGTH; ++i) {
			if (sort[i] == 2) score += 2;
			else if (sort[i] == 3) score += 6;
			else if (sort[i] == 4) { 
				score += 12;
				break; // no more pairs to be found
			}
		}


		// look for straights
		for (int i = 0; i < SORT_ARRAY_LENGTH - 2; ++i) {
			if (sort[i] > 0 && sort[i+1] > 0 && sort[i+2] > 0) {

				// we have a run. is it a run of four?
				if (i+3 < SORT_ARRAY_LENGTH && sort[i+3] > 0) { 

					// we have a run of four. is it a run of five?
					if (i+4 < SORT_ARRAY_LENGTH && sort[i+4] > 0) { 
						score += 5;
						break; // no more runs to be found in this hand
					}

					// it is not a run of five. is it a double run of four?
					if (sort[i] > 1 || sort[i+1] > 1 || sort[i+2] > 1 || sort[i+3] > 1) {
						score += 8;
						break; // no more runs to be found in this hand
					}

					score += 4;
					break; // no more runs to be found in this hand
				}
				// it is not a run of four

				// is it a run of three where one card is tripled?
				else if (sort[i] > 2 || sort[i+1] > 2 || sort[i+2] > 2) {
					score += 9;
					break; // no more runs to be found in this hand
				}

				// is it a run of three where two cards are doubled?
				else if (sort[i] + sort[1+1] + sort[1+2] == 5) {
					// this means that all five cards are used.
					// this is true for other ways to score points,
					// but all of those ways were already covered above.
					score += 12;
					break; // no more runs to be found in this hand
				}

				// we know that less than five cards are used.
				// the use of four cards in a run means that there was
				// a straight of four, which was already covered, or
				// a double run. test below.
				else if (sort[i] + sort[1+1] + sort[1+2] == 4) {
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
		// create a new hand of five cards which includes the bonus card to aid in searching for 15s
		Card[] bcHand = new Card[NUM_CARDS_TO_KEEP + 1];
		for (int i = 0; i < bcHand.length - 1; ++i) {
			bcHand[i] = hand[i];
		}
		bcHand[bcHand.length - 1] = bonusCard;

		// first look for 15s in pairs of cards and if triples of cards    
		int tempSum = 0;
		for (int i = 0; i < bcHand.length - 1; ++i) {
			int j = 0; // new iterator
			for (j = i + 1; j < bcHand.length; ++j) {
				// test if the two cards make 15
				if (bcHand[i].rank.intCountValue() + bcHand[j].rank.intCountValue() == FIFTEEN) score += 2;

				// test if the three cards make 15
				tempSum = 0;
				for (int k = 0; k < bcHand.length; ++k) if (k != i && k != j) tempSum += bcHand[k].rank.intCountValue();
				if (tempSum == FIFTEEN) score += 2;
			}
		}

		// now look for 15s in groups of four cards
		for (int i = 0; i < bcHand.length; ++i) {
			tempSum = 0;
			for (int k = 0; k < bcHand.length; ++k) if (k != i) tempSum += bcHand[k].rank.intCountValue();
			if (tempSum == FIFTEEN) score += 2;
		}

		// now see if all five cards make 15
		tempSum = 0;
		for (int i = 0; i < bcHand.length; ++i) tempSum += bcHand[i].rank.intCountValue();    
		if (tempSum == FIFTEEN) score += 2;

		// look for flushes
		// create an array much like sort but for suits
		int[] suitsArray = new int[NUM_SUITS];
		for (int i = 0; i < bcHand.length; ++i) suitsArray[bcHand[i].suit.intSuit()] += 1;
		for (int i = 0; i < NUM_SUITS; ++i) {
			if (suitsArray[i] == 4) {
				score += 4;
				break;
			}
			if (suitsArray[i] == 5) {
				score += 5;
				break;
			}
		}

		// look for nobs
		for (int i = 0; i < hand.length; ++i){
			if (hand[i].rank.intRank() == 11 && hand[i].suit.intSuit() == bonusCard.suit.intSuit()) score += 1;
		}

		return score;
	}

	static int arrLength(Card arr[]){
		int count = 0;
		for(int i = 0; i < arr.length; i++){
			if(arr[i] != null){
				count++;
			}
			else{
				break;
			}
		}

		return count; 
	}

	// counts the score earned by the card just layed to the table
	// where the first card layed on the table is element zero, the second card layed down is element 1, etc.
	// the table passed in must have a card at every spot
	// use array list and convert to array, perhaps
	static int countTable(Card table[])
	{
		// initialize score
		int score = 0;

		// save table.length as a variable for convenience
		int len = arrLength(table);

		// declare a generic iterator
		int i = 0;

		// check for pairs
		// start with four of a kind and work backward
		if (len >= 4 
				&& table[len - 1].rank.intRank() == table[len - 2].rank.intRank() 
				&& table[len - 2].rank.intRank() == table[len - 3].rank.intRank() 
				&& table[len - 3].rank.intRank() == table[len - 4].rank.intRank()) score += 12;
		else if (len >= 3 
				&& table[len - 1].rank.intRank() == table[len - 2].rank.intRank() 
				&& table[len - 2].rank.intRank() == table[len - 3].rank.intRank()) score += 6; 
		else if (len >= 2 
				&& table[len - 1].rank.intRank() == table[len - 2].rank.intRank()) score += 2;

		

		// check for flush
		if (len >= 4 
				&& table[len - 1].suit.intSuit() == table[len - 2].suit.intSuit() 
				&& table[len - 2].suit.intSuit() == table[len - 3].suit.intSuit()
				&& table[len - 3].suit.intSuit() == table[len - 4].suit.intSuit()) {
			// there is a flush, so score accordingly
			score += 4;

			// save suit for recursive method
			int flushSuit = table[len - 1].suit.intSuit();

			// check to see if the flush may contain more cards, recursively
			if (len >= 5) score += isMoreFlush(table, len - 5, flushSuit); 
		}

		// check for straight

		return score;
	}

	static int isMoreFlush(Card table[], int cardToCheck, int flushSuit)
	{
		if (cardToCheck > 0 && table[cardToCheck].suit.intSuit() == flushSuit) return 1 + isMoreFlush(table, cardToCheck - 1, flushSuit);
		if (cardToCheck == 0 && table[cardToCheck].suit.intSuit() == flushSuit) return 1;
		// else
		return 0;
	}

	// counts the tally of the table
	// the table passed in must have a card at every spot
	// use array list and convert to array for hand in, perhaps
	static int getTally(Card table[])
	{
		int tally = 0; // the aggregate tally

		for (int i = 0; i < arrLength(table); ++i) {
			tally += table[i].rank.intCountValue();
		}
		return tally;
	}
}