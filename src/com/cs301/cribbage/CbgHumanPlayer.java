package com.cs301.cribbage;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
	private Card[] tempHand;//temporary hand for changing	
	private Card[] selectedCards = new Card[2];
	private ArrayList<Card> cardsOnTable;//arraylist of cards currently on the table
	private GameAction action;//action that will be sent to the game

	public CbgHumanPlayer(String name) {
		super(name);
	}

	public final void confirm() { 
		action = null;//resets action		
		if(state.getGameStage() == state.THROW_STAGE){//checks if game is in throw stage
			if(isFull(selectedCards)){
				for(int i = 0; i < selectedCards.length;i++){
					int cardPos = indexOfCard(tempHand, selectedCards[i]);
					if (cardPos >0 && cardPos < tempHand.length){
						//removes cards
						tempHand[cardPos] = null;
					}
				}			
			action = new CardsToThrow(this, selectedCards);//sets action
			}
		}else if(state.getGameStage() == state.PEG_STAGE){//checks if game is in peg stage
			action = new CardsToTable(this, selectedCards[0]);//sets action
			tempHand[indexOfCard(tempHand, selectedCards[0])] = null;//gets index of card played and removes the card
		}
		selectedCards[0] = null;
		selectedCards[1] = null;
			
		game.sendAction(action);//sends game action
	}

	private void updateDisplay(){
		//updates GUI to reflect score
		p1Progress.setProgress(state.player1Score);
		p2Progress.setProgress(state.player2Score);    	
		p1Score.setText(""+state.player1Score);
		p2Score.setText(""+state.player2Score);

		tutorial.setText(state.tutorialTexts[state.gameStage]);
	}

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
	 * tick method for the main surface view.  Where all the cards will be drawn
	 * @param c  canvas to be drawn on
	 */
	public void tick(Canvas c) {
		if (state == null) return;//if state is null, quit method
		Card bonusCard = state.getDeck().peekAtTopCard();//saves top card of deck
		bonusCard.drawOn(c, new RectF(100, 100, 200, 0));//draws top card

		float cardWidth = c.getWidth()/9;
		float height = c.getHeight();
		//init rectangles for cards to be drawn on
		handCardPos[0] = new RectF(  cardWidth, height*2/3 , 2*cardWidth, height);
		handCardPos[1] = new RectF(2*cardWidth, height*2/3 , 3*cardWidth ,height);
		handCardPos[2] = new RectF(3*cardWidth, height*2/3 , 4*cardWidth, height);
		handCardPos[3] = new RectF(4*cardWidth, height*2/3 , 5*cardWidth, height);
		handCardPos[4] = new RectF(5*cardWidth, height*2/3 , 6*cardWidth, height);
		handCardPos[5] = new RectF(6*cardWidth, height*2/3 , 7*cardWidth, height);

		tableCardPos[0] = new RectF(0          , 0,   cardWidth , height/3);
		tableCardPos[1] = new RectF(cardWidth  , 0, 2*cardWidth , height/3);
		tableCardPos[2] = new RectF(2*cardWidth, 0, 3*cardWidth , height/3);
		tableCardPos[3] = new RectF(3*cardWidth, 0, 4*cardWidth , height/3);
		tableCardPos[4] = new RectF(4*cardWidth, 0, 5*cardWidth , height/3);
		tableCardPos[5] = new RectF(5*cardWidth, 0, 6*cardWidth , height/3);
		tableCardPos[6] = new RectF(6*cardWidth, 0, 7*cardWidth , height/3);
		tableCardPos[7] = new RectF(7*cardWidth, 0, 8*cardWidth , height/3);
		tableCardPos[8] = new RectF(8*cardWidth, 0, 9*cardWidth , height/3);
		tableCardPos[9] = new RectF(9*cardWidth, 0, 10*cardWidth, height/3);

		throwPos[0] = new RectF(9*cardWidth                , height*2/3, 10*cardWidth, height);
		throwPos[1] = new RectF(9*cardWidth + cardWidth/3  , height*2/3, 11*cardWidth, height);
		throwPos[2] = new RectF(9*cardWidth + cardWidth*2/3, height*2/3, 10*cardWidth, height);
		throwPos[3] = new RectF(10*cardWidth               , height*2/3, 11*cardWidth, height);

		//inits local variables from state variables (NEED THIS)
		tempHand = state.getHand(state.PLAYER_1);
		cardsOnTable = state.getTable();//gets cards on table
		
		//draws cards and the cover of the cards that have been sent away
		drawHand(c);
		
		drawTable(c);

		//highlights cards selected
		for(Card selected: selectedCards){
			if(selected != null){highLight(c, handCardPos[indexOfCard(tempHand, selected)]);}
		}
	}

	private void drawHand(Canvas c) {
		for(int i = 0; i < tempHand.length;i++){
			if(tempHand[i] != null){//if card is null, dont draw it
				tempHand[i].drawOn(c, handCardPos[i]);//draws the cards in each position
			}else if(tempHand[i] == null){
				c.drawRect(handCardPos[i], new Paint(Color.BLACK));//covers up old cards
			}
		}
	}

	private void drawTable(Canvas c) {
		if(!cardsOnTable.isEmpty()){//only if cards on table is non-empty
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

	@Override
	public void onClick(View v) {
		if(v.getId() == confirm.getId()){
			confirm();
		}else if (v.getId() == R.id.quitButton){
			System.exit(0);
		}
	}

	private void selectCard(Card c) {		
		if(state.getGameStage() == state.THROW_STAGE){
			if(selectedCards[0] == c){
				selectedCards[0] = null;//selects
			}else if(selectedCards[1] == c){
				selectedCards[1] = null;//selects
			}else if(selectedCards[0] == null){
				selectedCards[0] = c;//deselects
			}else if(selectedCards[1] == null){
				selectedCards[1] = c;//deselects
			}
		}else if(state.getGameStage() == state.PEG_STAGE){
			if(selectedCards[0] == null){
				selectedCards[0] = c;//selects
			}else if(selectedCards[0] == c){
				selectedCards[0] = null;//deselects
			}
		}		
	}

	private int indexOfCard(Card[] a, Card c){
		for(int i =0; i<a.length;i++){
			if(a[i]==c){
				return i;
			}
		}
		return -1;
	}

	private void highLight(Canvas c, RectF where){
		if(where != null){
			Paint highlight = new Paint(Color.BLACK);
			highlight.setAlpha(60);
			c.drawRect(where, highlight);
		}
	}

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

	@Override
	public void onTouch(MotionEvent event) {
		if(event.getAction() == event.ACTION_DOWN){
			float x = event.getX();
			float y = event.getY();	

			if(state != null && state.getGameStage() == CbgState.THROW_STAGE && state.getTurn() == state.PLAYER_1){//checks if state is not null, cecks stage
				//and checks whose turn it is
				for(int i = 0; i<tempHand.length; i++){
					if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
						selectCard(tempHand[i]);//selects card in 
					}
				}
			}
			if(state != null && state.getGameStage() == CbgState.PEG_STAGE && state.getTurn() == state.PLAYER_1){
				for(int i = 0; i<tempHand.length;i++){
					if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
						selectCard(tempHand[i]);
					}
				}
			}
		}	
	}
}

