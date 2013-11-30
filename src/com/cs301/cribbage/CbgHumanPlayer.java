package com.cs301.cribbage;


import java.util.ArrayList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.up.cs301.animation.AnimationSurface;
import edu.up.cs301.animation.Animator;
import edu.up.cs301.card.Card;
import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;

class CbgHumanPlayer extends GameHumanPlayer implements OnClickListener, Animator{

	private CbgState state;//state reference variable
	private GameMainActivity myActivity;//activity reference variable

	private AnimationSurface mainAS;

	private TextView tallyCount;
	private TextView tutorial;
	private TextView p2Score;
	private TextView p1Score;

	private ProgressBar p2Progress;
	private ProgressBar p1Progress;

	private Button menu;
	private Button confirm;

	private RectF[] handCardPos = new RectF[6];//positions that save where the cards are drawn in the players hand
	private RectF[] tableCardPos = new RectF[10];//positions where table cards are drawn
	private RectF[] throwPos = new RectF[4];//position of cards in crib
	private RectF deckPos;

	private Card[] tempHand;//temporary hand for changing	
	private Card[] selectedCards = new Card[2];
	private Card[] cardsOnTable;//arraylist of cards currently on the table
	private Card[] currCrib;

	private GameAction action;//action that will be sent to the game

	public CbgHumanPlayer(String name) {
		super(name);
	}

	/**
	 * Method called when player clicks confirm button.
	 * This method takes the selected cards and creates an action based on the current stage
	 * 	and sends that action to the game.
	 */
	public final void confirm() { 
		action = null;//resets action		
		if(state.getGameStage() == CbgState.THROW_STAGE){//checks if game is in throw stage
			if(isFull(selectedCards)){
				for(int i = 0; i < selectedCards.length;i++){
					int cardPos = indexOfCard(tempHand, selectedCards[i]);
					if (cardPos >=0 && cardPos < tempHand.length){
						//removes cards
						tempHand[cardPos] = null;
					}
				}			
				action = new CardsToThrow(this, selectedCards);//sets action
			}
		}else if(state.getGameStage() == CbgState.PEG_STAGE){//checks if game is in peg stage
			action = new CardsToTable(this, selectedCards[0]);//sets action
			tempHand[indexOfCard(tempHand, selectedCards[0])] = null;//gets index of card played and removes the card
		}
		game.sendAction(action);//sends game action

		//resets selected cards
		selectedCards[0] = null;
		selectedCards[1] = null;
	}

	/**
	 * Updates the player's GUI based on updated values in the Game state
	 */
	private void updateDisplay(){
		//updates GUI to reflect score
		p1Progress.setProgress(state.getScore(CbgState.PLAYER_1));
		p2Progress.setProgress(state.getScore(CbgState.PLAYER_2));    	
		p1Score.setText(""+state.getScore(CbgState.PLAYER_1));
		p2Score.setText(""+state.getScore(CbgState.PLAYER_2));

		tutorial.setText(state.tutorialTexts[state.getGameStage()]);
	}

	/**
	 * Sets the listeners for the animation surface as well as the various views
	 */
	@Override
	public void setAsGui(GameMainActivity activity) {
		myActivity = activity;		
		myActivity.setContentView(R.layout.activity_main);

		mainAS = (AnimationSurface) myActivity.findViewById(R.id.mainAS);
		mainAS.setAnimator(this);

		tallyCount = (TextView) myActivity.findViewById(R.id.tallyCount);
		tutorial = (TextView) myActivity.findViewById(R.id.tutorialText);
		p2Score = (TextView) myActivity.findViewById(R.id.p2Score);
		p1Score = (TextView) myActivity.findViewById(R.id.p1score);

		p2Progress = (ProgressBar) myActivity.findViewById(R.id.player2progress);
		p1Progress = (ProgressBar) myActivity.findViewById(R.id.Player1progress);

		menu = (Button) myActivity.findViewById(R.id.quitButton);
		confirm = (Button) myActivity.findViewById(R.id.confirmButton);

		menu.setOnClickListener(this);
		confirm.setOnClickListener(this);

		Card.initImages(myActivity);
		if (state != null) {
			receiveInfo(state);
		}
	}

	@Override
	public View getTopView() {
		return myActivity.findViewById(R.layout.activity_main);
	}

	/**
	 * Method called when the game state has changed and needs to update the player's GUI
	 * 	to reflect that change.
	 */
	@Override
	public void receiveInfo(GameInfo info) {
		if(info instanceof CbgState){
			state = (CbgState)info;
			updateDisplay();
		}		
	}

	public int backgroundColor() {		
		return 0x228b22;
	}

	/**
	 * Tick for the animation, every interval, these images will be drawn on the animation surface
	 * @param c  canvas to be drawn on
	 */
	public void tick(Canvas c) {
		if (state == null) return;//if state is null, quit method

		Card bonusCard = state.getDeck().peekAtTopCard();//saves top card of deck
		bonusCard.drawOn(c, new RectF(100, 100, 200, 0));//draws top card

		float cardWidth = c.getWidth()/9;
		float height = c.getHeight();
		//init rectangles for cards to be drawn on
		deckPos = new RectF(0, height*2/3, cardWidth, height);

		for(int i = 0; i<handCardPos.length;i++){
			handCardPos[i] = new RectF((i+1)*cardWidth, height*2/3, (i+2)*cardWidth, height);//inits pos of hand
		}

		for(int j = 0;j<tableCardPos.length;j++){
			tableCardPos[j] = new RectF(j*cardWidth, 0, (j+1)*cardWidth, height/3);//inits pos of table
		}

		throwPos[0] = new RectF(9*cardWidth                , height*2/3, 10*cardWidth                , height);
		throwPos[1] = new RectF(9*cardWidth + cardWidth/3  , height*2/3, 10*cardWidth + cardWidth/3  , height);
		throwPos[2] = new RectF(9*cardWidth + cardWidth*2/3, height*2/3, 10*cardWidth + cardWidth*2/3, height);
		throwPos[3] = new RectF(10*cardWidth               , height*2/3, 11*cardWidth                , height);

		//inits local variables from state variables
		tempHand = state.getHand();
		cardsOnTable = state.getTable().toArray(new Card[8]);//gets cards on table
		currCrib = state.getCrib();		

		//draws cards and the cover of the cards that have been sent away
		drawHand(c);

		drawTable(c);

		drawCrib(c);

		c.drawRect(new RectF(0,0,150,150), new Paint(Color.GREEN));

		c.drawRect(deckPos, new Paint(Color.RED));

		//highlights cards selected
		for(Card selected: selectedCards){
			if(selected != null){highLight(c, handCardPos[indexOfCard(tempHand, selected)]);}
		}
	}

	/**
	 * Draws the cards that are in the crib in the appropriate place
	 * @param c  canvas to draw on
	 */
	private void drawCrib(Canvas c) {
		for(int i = 0; i < currCrib.length; i++){
			if(currCrib[i] != null){
				currCrib[i].drawOn(c, throwPos[i]);
			}else if(currCrib[i] == null){
				c.drawRect(throwPos[i], new Paint(Color.RED));
			}
		}
	}

	/**
	 * Draws the cards that are in the players hand in the appropriate place
	 * @param c  canvas to draw on
	 */
	private void drawHand(Canvas c) {
		for(int i = 0; i < tempHand.length;i++){
			if(tempHand[i] != null){//if card is null, dont draw it
				tempHand[i].drawOn(c, handCardPos[i]);//draws the cards in each position
			}else if(tempHand[i] == null){
				c.drawRect(handCardPos[i], new Paint(Color.BLACK));//covers up old cards
			}
		}
	}

	/**
	 * Draws the cards that are in the table in the appropriate place
	 * @param c  canvas to draw on
	 */
	private void drawTable(Canvas c) {
		for(int i = 0; i<cardsOnTable.length;i++){//iterate through the arraylist of cards on table
			if(cardsOnTable[i] != null){
				cardsOnTable[i].drawOn(c, tableCardPos[i]);//draws cards on table
			}else if(cardsOnTable[i] == null){
				c.drawRect(tableCardPos[i], new Paint(Color.RED));//draws red rectangle for null cards
				int count = 0;//counter for the graphics position of each card
				for(Card tableCard: cardsOnTable){//iterate through the arraylist of cards on table
					if(tableCard != null){
						tableCard.drawOn(c, tableCardPos[count]);//draws cards on table
					}else if(tableCard == null){
						c.drawRect(tableCardPos[count], new Paint(Color.RED));//draws red rectangle for null cards
					}
					count++;
				}
			}
		}
	}

	/**
	 * Method that handles what to do when the user presses a certain button.
	 * If the confirm button is pressed, the confirm() method is called.
	 * If the quit button is pressed, the program ends.
	 */
	@Override
	public void onClick(View v) {
		if(v.getId() == confirm.getId()){
			confirm();//confirms card selection
		}else if (v.getId() == R.id.quitButton){
			System.exit(0);//quits program
		}
	}

	/**
	 * Method that handles selecting a card that the user has chosen.
	 * If the card has not been already chosen, it adds that card to the cards selected (Max 2)
	 * If the card has already been chosen, it deselects that card
	 * @param card  Card that was touched
	 */

	private void selectCard(Card c) {		
		if(state.getGameStage() == state.THROW_STAGE && isFull(tempHand)){
			if(selectedCards[0] == c){
				selectedCards[0] = null;//selects
			}else if(selectedCards[1] == c){
				selectedCards[1] = null;//selects
			}else if(selectedCards[0] == null){
				selectedCards[0] = c;//deselects
			}else if(selectedCards[1] == null){
				selectedCards[1] = c;//deselects
			}			
		}
		else if(state.getGameStage() == CbgState.PEG_STAGE){
			selectedCards[1] = c;//deselects
		}			
		else if(state.getGameStage() == state.PEG_STAGE){
			if(selectedCards[0] == null){
				selectedCards[0] = c;//selects
			}else if(selectedCards[0] == c){
				selectedCards[0] = null;//deselects
			}
		}		
	}


	/**
	 * Return the index of the specified card in the specified array
	 * @param a  Array to search in
	 * @param c  Card to search for
	 * @return  Index of the card, -1 if card is not found
	 */
	private int indexOfCard(Card[] a, Card c){
		for(int i =0; i<a.length;i++){
			if(a[i]==c){
				return i;
			}
		}
		return -1;
	}

	/** 
	 * Highlights the array of selected cards
	 * @param c  Canvas to draw on
	 * @param where  Where to highlight
	 */
	private void highLight(Canvas c, RectF where){
		if(where != null){
			Paint highlight = new Paint(Color.BLACK);
			highlight.setAlpha(60);
			c.drawRect(where, highlight);
		}
	}

	/**
	 * Checks if the specified array is full
	 * @param arr  Array to check
	 * @return  if it is full or not
	 */
	private boolean isFull(Card[] arr){
		for(Card c: arr){
			if(c==null){return false;}
		}
		return true;
	}

	@Override

	public int interval() {
		return 50;
	}

	@Override

	public boolean doPause() {
		return false;
	}

	@Override

	public boolean doQuit() {
		return false;
	}

	/**
	 * Method that responds to a user touch.
	 * This method selects the cards that occupy the space that the user touched
	 */
	@Override
	public void onTouch(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();	

			if(state != null && state.getGameStage() == CbgState.THROW_STAGE && state.getTurn() == CbgState.PLAYER_1){//checks if state is not null, cecks stage
				//and checks whose turn it is				
				for(int i = 0; i<tempHand.length; i++){
					if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
						selectCard(tempHand[i]);//selects card in 						
					}
				}
			}
			if(state != null && state.getGameStage() == CbgState.PEG_STAGE && state.getTurn() == CbgState.PLAYER_1){
				for(int i = 0; i<tempHand.length;i++){
					if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
						selectCard(tempHand[i]);
					}
				}
			}
		}	
	}
}
