package com.cs301.cribbage;
import edu.up.cs301.card.*;
import edu.up.cs301.game.GamePlayer;
/* 
   
 */
import edu.up.cs301.game.actionMsg.GameAction;


class CardsToTable extends GameAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4284096867696940029L;
	private Card cardsToTable;
	private GamePlayer player;
    public CardsToTable(GamePlayer player, Card card) {
            super(player);
            cardsToTable = card;    
    }
    
    /**
     * gets the cards from the action
     * @return cards stored
     */
    public Card cards(){
    	return cardsToTable;
    }
    
    /**
     * gets the player who sent the action
     * @return  player
     */
    public GamePlayer player(){
    	return player;
    }


}