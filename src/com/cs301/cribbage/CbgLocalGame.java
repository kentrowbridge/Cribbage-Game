package com.cs301.cribbage;
import edu.up.cs301.card.*;

/* 

 */


class CbgLocalGame {
	private Deck deck;
	private CbgState state;
	public CbgLocalGame(){
		deck = new Deck();//creates a deck and shuffles it.
		deck.add52().shuffle();
		state = new CbgState();
		state.setDeck(deck);		

	}


	/*
	 * Checks the boolean isGameOver in gameState, and if its true it sets the return string to reflect the final score and the winner.
	 */

	private final String checkIfGameOver() {
		if(state.getGameOver()){
			int winner = state.getWinner();
			if(winner == CbgState.PLAYER_1){
				return ("Playre 1 won with " + state.getScore(CbgState.PLAYER_1) + " points.");
			}
			else if(winner == CbgState.PLAYER_2){
				return ("Playre 2 won with " + state.getScore(CbgState.PLAYER_1) + " points.");
			}
			else return "ERROR";//if something horrible happens
		}

		return "FALSE";//if game is not over

	}

	private final boolean isValidCard() {
		boolean isValid = false;

		return isValid;
	}

	private final int handCount() {
		int count = 0;

		return count;
	}

	private final void sendUpdatedStateTo() {

	}

	private final boolean canMove() {
		boolean canMove = false;

		return canMove;

	}

	private final boolean makeMove() {
		boolean makeMove = false;

		return makeMove;
	}

	private final int countTable(Card table[]) {
		int count = 0;

		return count;
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


}
