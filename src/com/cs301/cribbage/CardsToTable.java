package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.GamePlayer;
/* 
   
 */
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * @author Will Christiansen
 * @author Devon Griggs
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * @version 12/2/2013
 */

class CardsToTable extends GameAction {

	// Instance Variables
	
	private static final long serialVersionUID = 4284096867696940029L;
	private Card cardsToTable;
	private GamePlayer player;
	
    public CardsToTable(GamePlayer player, Card card) {
            super(player);
            cardsToTable = card;    
    } // Constructor for the CardsToTable gameAction
    
    /**
     * Gets the cards from the action
     * @return cards stored
     */
    
    public Card cards(){
    	return cardsToTable;
    }
    
    /**
     * Gets the player who sent the action
     * @return  player
     */
    
    public GamePlayer player(){
    	return player;
    }
}