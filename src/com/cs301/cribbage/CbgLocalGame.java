package com.cs301.cribbage;
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
                deck = new Deck();//creates a deck and shuffles it.
                deck.add52().shuffle();
                state = new CbgState();
                state.setDeck(deck);                

        }

        private void gameCycle()
        {
                while (!state.getGameOver())
                {
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

        private final boolean makeMove() {
                boolean makeMove = false;
                // TODO
                return makeMove;
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

        @Override
        protected boolean makeMove(GameAction action) {
                // TODO Auto-generated method stub
                return false;
        }
}