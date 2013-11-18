package com.cs301.cribbage;
import java.util.ArrayList;

import edu.up.cs301.card.*;
import edu.up.cs301.game.*;
import edu.up.cs301.game.actionMsg.GameAction;

/* 

 */


class CbgLocalGame extends LocalGame{
        
        private Deck deck;
        private CbgState state;
        private int currentGameStage;
        
        private int originalTally;
         
        public CbgLocalGame(){
                deck = new Deck();//creates a deck
                state = new CbgState();
                gameCycle();

        }

        private void gameCycle()
        {
                while (!state.getGameOver())
                {
                		deck.add52().shuffle();// creates a shuffled deck for each round
                		state.setDeck(deck); 
                        deal();
                        currentGameStage = state.getGameStage();//TODO write getgameStage Method
                        while (currentGameStage == state.THROW_STAGE)
                        {
                                
                                state.setGameStage(state.PEG_STAGE);//TODO make if statement that determines if it is the correct time to switch state
                        }
                        while (currentGameStage == state.PEG_STAGE)
                        {
                                //once the game is in the pegging stage, flips the top card of deck
                                state.getBonusCard();
                                state.setGameStage(state.COUNT_STAGE);
                        }
                        while (currentGameStage == state.COUNT_STAGE)
                        {
                                //after the score has been tallied, checks if the game is over
                                state.getGameOver();
                                originalTally = state.getTally();
                                if (state.getTally() > originalTally)
                                {
                                        state.setGameStage(state.THROW_STAGE);
                                }
                        }
                        deck.shuffle();
                }
        }//gameCycle

        /*
         * Checks the boolean isGameOver in gameState, and if its true it sets the return string to reflect the final score and the winner.
         */

        private final boolean isValidCard() {
                boolean isValid = false;
                // TODO
                return isValid;
        }

        private final int handCount() {
                int count = 0;
                // TODO
                return count;
        }

        private final boolean canMove() {
                boolean canMove = false;
                // TODO
                return canMove;

        }
        @Override
        protected final boolean makeMove(GameAction action) {
              if (action instanceof CardsToTable){//if card to table action
            	  ArrayList<Card> cardArr = new ArrayList<Card>(); 
            	  CardsToTable cards = (CardsToTable) action;
            	  cardArr = state.getTable();  // get table
            	  cardArr.add(cards.cards()); //add card
            	  state.setTable(cardArr); //send back to gamestate
            	  return true;
              }
              else if (action instanceof CardsToThrow){
            	  CardsToThrow cards = (CardsToThrow) action;
            	  Card[] cardArr = state.getCrib();
            	  Card[] cardsThrown = cards.cards();
            	  for(int i = 0; i < 2; i++){
            		  cardArr[cardArr.length] = cardsThrown[i];
            	  }
            	  return true;
              }
              else return false;
        }

        private final int countTable(Card table[]) {
                int count = 0;
                // TODO
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

        @Override
        protected void sendUpdatedStateTo(GamePlayer p) {
                // TODO Auto-generated method stub
                
        }

        @Override
        protected boolean canMove(int playerIdx) {
                // TODO Auto-generated method stub
                return false;
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

}