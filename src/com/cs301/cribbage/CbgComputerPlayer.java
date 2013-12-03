package com.cs301.cribbage;
import edu.up.cs301.card.*;
/**
 * @author Devon Griggs
 * @version 11/24
   
 */
import edu.up.cs301.game.GameComputerPlayer;


abstract class CbgComputerPlayer extends GameComputerPlayer {
	String name;
	
	CbgComputerPlayer(String name) {
		super(name);
		this.name = name;
	} // Constructor for a CbgComputerPlayer

	/**
	 * Abstract method for the CbgComputerSmartPlayer's use
	 * @return Card[] hand  The best hand
	 */
	
    private final Card[] findTopHand() {
    	Card[] hand = new Card[6];
    	return hand;
    }

    /**
     * Abstract method for the CbgComputerSmartPlayer's use
     * @return int count  Score the player has earned
     */
    
    private final int count4Cards() {
    	int count = 0;
    	return count;
    }

}