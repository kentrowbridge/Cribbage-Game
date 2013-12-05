package com.cs301.cribbage;
import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;

/**
 * This is the primary activity for Cribbage game
 * 
 * @author Andrew M. Nuxoll
 * @author Steven R. Vegdahl
 * @author Will Christiansen
 * @author Devon Griggs
 * @author Nick Sohm
 * @author Kenny Trowbridge
 * @version 12/2/2013
 */
public class MainActivity extends GameMainActivity {
	
	// The port number that this game will use when playing over the network
	private static final int PORT_NUMBER = 2234;

	/**
	 * Create the default configuration for this game:
	 * - one human player vs. one computer player
	 * - minimum of 1 player, maximum of 2
	 * - one kind of computer player and one kind of human player available
	 * 
	 * @return
	 * 		The new configuration object, representing the default configuration
	 */
	@Override
	public GameConfig createDefaultConfig() {
		
		// Define the allowed player types
		ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();
		
		// A human player player type (player type 0)
		playerTypes.add(new GamePlayerType("Local Human Player") {
			public GamePlayer createPlayer(String name) {
				return new CbgHumanPlayer(name);
			}});
		
		// A computer player type (player type 2)
		playerTypes.add(new GamePlayerType("Computer Random Player") {
			public GamePlayer createPlayer(String name) {
				return new CbgComputerRandomPlayer(name);
			}});

		// A computer player type (player type 1)
		playerTypes.add(new GamePlayerType("Computer Smart Player") {
			public GamePlayer createPlayer(String name) {
				return new CbgComputerSmartPlayer(name);
			}});
		
		// Create a game configuration class for Cribbage:
		// - player types as given above
		// - from 1 to 2 players
		// - name of game is "Cbg Game"
		// - port number as defined above
		GameConfig defaultConfig = new GameConfig(playerTypes, 1, 2, "Cbg Game",
				PORT_NUMBER);

		// Add the default players to the configuration
		defaultConfig.addPlayer("Human", 0); // player 1: a human player
		defaultConfig.addPlayer("Computer", 1); // player 2: a computer player
		
		// Set the default remote-player setup:
		// - player name: "Remote Player"
		// - IP code: (empty string)
		// - default player type: human player		
		defaultConfig.setRemoteData("Remote Player", "", 0);
		
		// Return the configuration
		return defaultConfig;
	} // createDefaultConfig

	/**
	 * Create a local game
	 * @return
	 * 		The local game, a Cbg game
	 */
	
	@Override
	public LocalGame createLocalGame() {
		return new CbgLocalGame();
	}
}