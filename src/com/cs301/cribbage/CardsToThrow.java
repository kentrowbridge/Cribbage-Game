package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/* 
   
 */


class CardsToThrow extends GameAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7411287322939386780L;
	/**
	 * 
	 */
	private Card[] cardsToThrow;

    public CardsToThrow(GamePlayer player, Card[] cards) {
    	super(player);
    	cardsToThrow = cards;
    }
    // getter for cards thrown
    public Card[] cards(){
    	return cardsToThrow;
    }


}
