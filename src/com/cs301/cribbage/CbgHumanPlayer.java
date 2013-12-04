package com.cs301.cribbage;

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

	private Paint cardBackOutline = new Paint(Color.BLACK);
	private Paint cardBackFill = new Paint(Color.BLUE);


	public CbgHumanPlayer(String name) {
		super(name);
	}

	/**
	 * Method called when player clicks confirm button.
	 * This method takes the selected cards and creates an action based on the current stage
	 * 	and sends that action to the game.
	 */
	public final void confirm() { 
		if(!isEmpty(selectedCards)){//do only if cards have been selected
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
					game.sendAction(action);//sends game action
				}
			}
			else if(state.getGameStage() == CbgState.PEG_STAGE){//checks if game is in peg stage
				if(CbgCounter.canMove(selectedCards[0], state)){
					action = new CardsToTable(this, selectedCards[0]);//sets action
					int cardPos = indexOfCard(tempHand, selectedCards[0]);


					game.sendAction(action);//sends game action
					if (cardPos >=0 && cardPos < tempHand.length){
						tempHand[cardPos] = null;//gets index of card played and removes the card
					}
				}
			}
			//resets selected cards
			selectedCards[0] = null;
			selectedCards[1] = null;

		}
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

		tallyCount.setText("" + state.getTally());

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
		return 0xff228b22;
	}

	/**
	 * Tick for the animation, every interval, these images will be drawn on the animation surface
	 * @param c  canvas to be drawn on
	 */
	public void tick(Canvas c) {
		if (state == null) return;//if state is null, quit method

		float cardWidth = c.getWidth()/9;
		float height = c.getHeight();
		//init rectangles for cards to be drawn on
		deckPos = new RectF(0, height*2/3, cardWidth, height);

		for(int i = 0; i<handCardPos.length;i++){//TODO these inits should be outside tick so they don't happen every time
			handCardPos[i] = new RectF(((i+1)*cardWidth)+10, height*2/3, ((i+2)*cardWidth)+10, height);//inits pos of hand
		}

		for(int j = 0;j<tableCardPos.length;j++){
			tableCardPos[j] = new RectF(j*cardWidth, 0, (j+1)*cardWidth, height/3);//inits pos of table
		}

		throwPos[0] = new RectF(7*cardWidth + cardWidth/2, height*2/3 - 50, 8*cardWidth + cardWidth/2, height - 50);
		throwPos[1] = new RectF(7*cardWidth + cardWidth/2, height*2/3     , 8*cardWidth + cardWidth/2, height     );
		
		throwPos[2] = new RectF(8*cardWidth		 , height*2/3 - 50, 9*cardWidth              , height - 50);
		throwPos[3] = new RectF(8*cardWidth              , height*2/3     , 9*cardWidth              , height);

		//inits local variables from state variables
		tempHand = state.getHand();
		cardsOnTable = state.getTable().toArray(new Card[8]);//gets cards on table
		currCrib = state.getCrib();		

		if(state.getGameStage() == CbgState.PEG_STAGE){
			Card bonusCard = state.getBonusCard();//saves top card of deck
			bonusCard.drawOn(c, deckPos);//draws top card
		}
		else {
			drawCardBack(c, deckPos);
		}

		//draws cards and the cover of the cards that have been sent away
		drawHand(c);

		drawTable(c);

		drawCrib(c);

		//highlights cards selected

		for(Card selected: selectedCards){
			synchronized("sync"){
				if(selected != null){//think issue here with syncronization, card nulled after null check TODO
					highLight(c, handCardPos[indexOfCard(tempHand, selected)], new Paint(Color.BLACK));
				}
			}
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
			}
		}
	}

	/**
	 * Draws the cards that are in the table in the appropriate place
	 * @param c  canvas to draw on
	 */
	private void drawTable(Canvas c) {
		for(int i = 0; i < cardsOnTable.length; i++){//iterate through the array of cards on table
			if(cardsOnTable[i] != null){
				cardsOnTable[i].drawOn(c, tableCardPos[i]);//draws cards on table
			}
		}
	}

	private void drawCardBack(Canvas c, RectF where){
		RectF innerFill = new RectF(where.left+10, where.top+10, where.right-10, where.bottom-10);	

		c.drawRect(where, cardBackOutline);
		c.drawRect(innerFill, cardBackFill);

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
	private void selectCard(Card card) {		
		if(state.getGameStage() == CbgState.THROW_STAGE && isFull(tempHand)){//if the temp hand is full and its the throw stage
			if(selectedCards[0] == card){
				selectedCards[0] = null;//deselects
			}else if(selectedCards[1] == card){
				selectedCards[1] = null;//deselects
			}else if(selectedCards[0] == null){
				selectedCards[0] = card;//selects
			}else if(selectedCards[1] == null){
				selectedCards[1] = card;//selects
			}			
		}else if(state.getGameStage() == CbgState.PEG_STAGE){
			if(selectedCards[0] == null){
				selectedCards[0] = card;//selects
			}else if(selectedCards[0] == card){
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
		for(int i =0; i<a.length;i++){//iterate through array searching for card
			if(a[i]==c){
				return i;//index of card touched
			}
		}
		return 0;//error value
	}

	/** 
	 * Highlights the array of selected cards
	 * @param c  Canvas to draw on
	 * @param where  Where to highlight
	 */
	private void highLight(Canvas c, RectF where, Paint p){
		if(where != null){
			p.setAlpha(60);//creates slightly transparent black paint
			c.drawRect(where, p);//paints over card to indicate selection
		}
	}

	/**
	 * Checks if the specified array is full
	 * @param arr  Array to check
	 * @return  if it is full or not
	 */
	private boolean isFull(Card[] arr){
		for(Card c: arr){//checks if every element in the array is not null
			if(c==null){return false;}
		}
		return true;
	}

	private boolean isEmpty(Card[] arr){
		for(Card c: arr){
			if(c!=null)return false;
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
		if(event.getAction() == MotionEvent.ACTION_DOWN){//only selected on down motion
			float x = event.getX();//coordinates of touch
			float y = event.getY();	

			if(state != null && state.getTurn() == CbgState.PLAYER_1){//checks if state is not null, cecks stage
				//and checks whose turn it is				
				for(int i = 0; i<tempHand.length; i++){
					if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
						selectCard(tempHand[i]);//selects card that user touched				
					}
				}
			}
		}	
	}
}
