package com.cs301.cribbage;
/**
 * @author Devon Griggs
 * @version 11/24
   
 */
import edu.up.cs301.game.GameComputerPlayer;


abstract class CbgComputerPlayer extends GameComputerPlayer {
	String name;
	CbgComputerPlayer(String name){
		super(name);
		this.name = name;
	}



}

