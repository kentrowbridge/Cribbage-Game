package com.cs301.cribbage;
import edu.up.cs301.card.*;
/* 
   
 */
import edu.up.cs301.game.GameComputerPlayer;


abstract class CbgComputerPlayer extends GameComputerPlayer {
	String name;
	CbgComputerPlayer(String name){
		super(name);
		this.name = name;
	}

    private final Card[] findTopHand() {
    	Card[] hand = new Card[6];
    	return hand;
    }

    private final int count4Cards() {
    	int count = 0;
    	
    	return count;
    }


}
