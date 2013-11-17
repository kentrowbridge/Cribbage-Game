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
	
    public CardsToTable(GamePlayer player, Card card) {
            super(player);
            cardsToTable = card;
    
    }
    //getter for card to table
    public Card cards(){
    	return cardsToTable;
    }


}