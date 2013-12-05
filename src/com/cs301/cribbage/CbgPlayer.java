package com.cs301.cribbage;
import edu.up.cs301.card.*;

/**
 * @author Will Christiansen
 * @author Devon Griggs
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * @version 12/2/2013
 */

abstract class CbgPlayer {

	/**
	 * Abstract method for CbgHumanPlayer(s) to send a card to the crib
	 * @param Card card  The card to be thrown
	 */
	
    public final void  sendCardToCrib(Card card) {

    }
    
	/**
	 * Abstract method for CbgHumanPlayer(s) to send a card to the table
	 * @param Card card  The card to be played
	 */

    public final void sendCardToTable(Card card) {
    	
    }
}