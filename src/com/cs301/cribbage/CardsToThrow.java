package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/* 
   
 */


class CardsToThrow extends GameAction {

    private static final long serialVersionUID = -7411287322939386780L;
	/**
	 * Class that is used for throw actions
	 */
	private Card[] cardsToThrow;
	private GamePlayer player;
	
    public CardsToThrow(GamePlayer player, Card[] cards) {
    	super(player);
    	this.player = player;
    	cardsToThrow = cards;
    }
    
    /**
     * gets cards from action
     * @return  cards thrown
     */
    public Card[] cards(){
    	return cardsToThrow;
    }
    
    /** 
     * @return player
     */
    public GamePlayer player(){
    	return player;
    }


}
