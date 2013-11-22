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

	private SurfaceView mainAS;
	
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
	private Card[] tempHand;//temporary hand for changing
	private RectF[] throwPos = new RectF[4];//position of cards in crib
	private Card[] toThrow = new Card[2];//array that holds cards that have been selected to throw
	private Card toTable;//card that user has selected to send to table
	private Card[] crib;//crib reference array
	private ArrayList<Card> cardsOnTable = new ArrayList<Card>();//arraylist of cards currently on the table
	private GameAction action;//action that will be sent to the game
	
	public CbgHumanPlayer(String name) {
		super(name);
		handCardPos[0] = new RectF(0, 0, 0, 0);
		handCardPos[1] = new RectF(0, 0, 0, 0);
		handCardPos[2] = new RectF(0, 0, 0, 0);
		handCardPos[3] = new RectF(0, 0, 0, 0);
		handCardPos[4] = new RectF(0, 0, 0, 0);
		handCardPos[5] = new RectF(0, 0, 0, 0);
	}

	public final void confirm() { 
		action = null;//resets action		
		if(state.getGameStage() == state.THROW_STAGE){//checks if game is in throw stage
			action = new CardsToThrow(this, toThrow);//sets action
			for(int i = 0; i < 2;i++){
				int cardPos = indexOfCard(tempHand, toThrow[i]);
				if (cardPos >0 && cardPos < tempHand.length-1){
					//if card is in hand, remove that card
					tempHand[cardPos] = null;
				}
			}
		}else if(state.getGameStage() == state.PEG_STAGE){//checks if game is in peg stage
			action = new CardsToTable(this, toTable);//sets action
			tempHand[indexOfCard(tempHand, toTable)] = null;//gets index of card played and removes the card
		}
		game.sendAction(action);
		//sets tempHand to players hand
		state.setHand(tempHand, state.PLAYER_1);
	}

	private void updateDisplay(){
		//updates GUI to reflect score
		p1Progress.setProgress(state.player1Score);
		p2Progress.setProgress(state.player2Score);    	
		p1Score.setText(""+state.player1Score);
		p2Score.setText(""+state.player2Score);

		tutorial.setText(state.tutorialTexts[state.gameStage]);

		//TODO do we need to add players hand in here?
	}

	@Override
	public void setAsGui(GameMainActivity activity) {
		myActivity = activity;		
		myActivity.setContentView(R.layout.activity_main);

		mainAS = (AnimationSurface) myActivity.findViewById(R.id.mainAS);
		
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
		handCardPos[0] = new RectF(2*cardWidth, height*2/3 , 3*cardWidth, height);
		handCardPos[1] = new RectF(3*cardWidth, height*2/3 , 4*cardWidth ,height);
		handCardPos[2] = new RectF(4*cardWidth, height*2/3 , 5*cardWidth, height);
		handCardPos[3] = new RectF(5*cardWidth, height*2/3 , 6*cardWidth, height);
		handCardPos[4] = new RectF(6*cardWidth, height*2/3 , 7*cardWidth, height);
		handCardPos[5] = new RectF(7*cardWidth, height*2/3 , 8*cardWidth, height);
		tableCardPos[0] = new RectF(  cardWidth, 0,   cardWidth, height/3);
		tableCardPos[1] = new RectF(2*cardWidth, 0, 2*cardWidth, height/3);
		tableCardPos[2] = new RectF(3*cardWidth, 0, 3*cardWidth, height/3);
		tableCardPos[3] = new RectF(4*cardWidth, 0, 4*cardWidth, height/3);
		tableCardPos[4] = new RectF(5*cardWidth, 0, 5*cardWidth, height/3);
		tableCardPos[5] = new RectF(6*cardWidth, 0, 6*cardWidth, height/3);
		tableCardPos[6] = new RectF(7*cardWidth, 0, 7*cardWidth, height/3);
		tableCardPos[7] = new RectF(8*cardWidth, 0, 8*cardWidth, height/3);
		tableCardPos[8] = new RectF(9*cardWidth, 0, 9*cardWidth, height/3);
		tableCardPos[9] = new RectF(9*cardWidth, 0, 10*cardWidth, height/3);
		throwPos[0] = new RectF(9*cardWidth, height*2/3, 10*cardWidth, height);
		throwPos[1] = new RectF(9*cardWidth + cardWidth/3, height*2/3, 11*cardWidth, height);
		throwPos[2] = new RectF(9*cardWidth + cardWidth*2/3, height*2/3, 10*cardWidth, height);
		throwPos[3] = new RectF(10*cardWidth, height*2/3, 11*cardWidth, height);

		//inits local variables from state variables (NEED THIS)
		tempHand = state.getHand();
		cardsOnTable = state.getTable();
		crib = state.getCrib();
		if(tempHand != null){
			for(int i = 0; i < tempHand.length;i++){
				tempHand[i].drawOn(c, handCardPos[i]);//draws the cards in each position
			}
		}
		boolean cribNull = false;
		for (Card cards : crib){
			if (cards == null){
				cribNull = true;
			}
		}
		if(!cribNull){
			for(int i = 0; i < crib.length;i++){
				crib[i].drawOn(c,throwPos[i]);//draws the cards in each position
			}
		}

		if(cardsOnTable != null){
			int counter = 0;

			for(Card cOnT : cardsOnTable){
				cOnT.drawOn(c, tableCardPos[counter]);
				counter++;
			}

		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == confirm.getId()){
			if(state.getGameStage() == CbgState.THROW_STAGE){
				game.sendAction(new CardsToThrow(this, toThrow));
			}
			if(state.getGameStage() == CbgState.PEG_STAGE){
				game.sendAction(new CardsToTable(this, toTable));
			}
		}else if (v.getId() == R.id.quitButton){
			System.exit(0);
		}
	}

	private void selectCard(Card c) {
		if(state.getGameStage() == CbgState.THROW_STAGE){
			if(toThrow[0] == null && toThrow[1] == null){//check if toThrow is empty
				toThrow[0] = c;
			}else if(toThrow[0] !=null && toThrow[1] == null){//check if toThrow has only one entry
				toThrow[1] =c;
			}else{//all other situations do nothing
			}
		}
		else if(state.getGameStage() == CbgState.PEG_STAGE){
			toTable = c;
		}
	}

	public int indexOfCard(Card[] a, Card c){
		for(int i =0; i<a.length;i++){
			if(a[i]==c){
				return i;
			}
		}
		return -1;
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
		float x = event.getX();
		float y = event.getY();	

		if(state != null && state.getGameStage() == CbgState.THROW_STAGE && state.getTurn() == state.PLAYER_1){//checks if state is not null, cecks stage
			//and checks whose turn it is
			for(int i = 0; i<6; i++){
				if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
					selectCard(tempHand[i]);
				}
			}
		}
		if(state != null && state.getGameStage() == CbgState.PEG_STAGE && state.getTurn() == state.PLAYER_1){
			for(int i = 0; i<6;i++){
				if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
					selectCard(tempHand[i]);
				}
			}
		}
	}
	
	//TODO make a get playerIDX method
	
}
