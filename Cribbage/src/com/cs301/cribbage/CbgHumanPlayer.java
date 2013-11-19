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

class CbgHumanPlayer extends GameHumanPlayer implements Animator, OnClickListener, OnTouchListener{

	private CbgState state;//state reference variable
	private GameMainActivity myActivity;//activity reference variable

	private AnimationSurface tableSurface;


	private TextView tallyCount;
	private TextView tutorial;
	private TextView p2Score;
	private TextView p1Score;

	private ProgressBar p2Progress;
	private ProgressBar p1Progress;

	private Button menu;
	private Button confirm;

	private RectF[] handCardPos = new RectF[6];//positions that save where the cards are drawn in the players hand
	private RectF[] tableCardPos = new RectF[10];
	private Card[] tempHand = new Card[6];

	private Card[] toThrow = new Card[2];
	private Card toTable;

	private ArrayList<Card> cardsOnTable = new ArrayList<Card>();
	private GameAction action;
	/*
	 * Override
	 */

	public CbgHumanPlayer(String name) {
		super(name);
		handCardPos[0] = new RectF(0, 0, 0, 0);
		handCardPos[1] = new RectF(0, 0, 0, 0);
		handCardPos[2] = new RectF(0, 0, 0, 0);
		handCardPos[3] = new RectF(0, 0, 0, 0);
		handCardPos[4] = new RectF(0, 0, 0, 0);
		handCardPos[5] = new RectF(0, 0, 0, 0);


	}
	//
	//	public final void sendCardToCrib() {
	//		
	//    }

	public final void confirm() { 
		action = null;
		if(state.getGameStage() == state.THROW_STAGE){
			action = new CardsToThrow(this, toThrow);
		}else if(state.getGameStage() == state.PEG_STAGE){
			action = new CardsToTable(this, toTable);
		}
		game.sendAction(action);
	}
	//    
	//    public final void sendCardToTable() {
	//    
	//    }
	//    
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

		tableSurface = (AnimationSurface) myActivity.findViewById(R.id.tableView);

		tableSurface.setOnTouchListener(this);

		tableSurface.setAnimator(this);


		tallyCount = (TextView) myActivity.findViewById(R.id.tallyCount);
		tutorial = (TextView) myActivity.findViewById(R.id.tutorialText);
		p2Score = (TextView) myActivity.findViewById(R.id.p2Score);
		p1Score = (TextView) myActivity.findViewById(R.id.p1score);

		p2Progress = (ProgressBar) myActivity.findViewById(R.id.player2progress);
		p1Progress = (ProgressBar) myActivity.findViewById(R.id.Player1progress);

		menu = (Button) myActivity.findViewById(R.id.menuButton);
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

	public int interval() {		
		return 50;
	}

	public int backgroundColor() {		
		return 0xffffff;
	}

	public boolean doPause() {
		return false;
	}

	public boolean doQuit() {
		return false;
	}

	public void tick(Canvas c) {
		if (state == null) return;
		Card card = state.getDeck().peekAtTopCard();
		card.drawOn(c, new RectF(100, 100, 200, 0));

		float cardWidth = c.getWidth()/10;
		float height = c.getHeight();
		//init rectangles for cards to be drawn on
		handCardPos[0] = new RectF(2*cardWidth, 550, 3*cardWidth, height);
		handCardPos[1] = new RectF(3*cardWidth, 550, 4*cardWidth ,height);
		handCardPos[2] = new RectF(4*cardWidth, 550, 5*cardWidth, height);
		handCardPos[3] = new RectF(5*cardWidth, 550, 6*cardWidth, height);
		handCardPos[4] = new RectF(6*cardWidth, 550, 7*cardWidth, height);
		handCardPos[5] = new RectF(7*cardWidth, 550, 8*cardWidth, height);
		tableCardPos[0] = new RectF(1*cardWidth, 0, 1*cardWidth, 100);
		tableCardPos[1] = new RectF(2*cardWidth, 0, 2*cardWidth ,100);
		tableCardPos[2] = new RectF(3*cardWidth, 0, 3*cardWidth, 100);
		tableCardPos[3] = new RectF(4*cardWidth, 0, 4*cardWidth, 100);
		tableCardPos[4] = new RectF(5*cardWidth, 0, 5*cardWidth, 100);
		tableCardPos[5] = new RectF(6*cardWidth, 0, 6*cardWidth, 100);
		tableCardPos[6] = new RectF(7*cardWidth, 0, 7*cardWidth ,100);
		tableCardPos[7] = new RectF(8*cardWidth, 0, 8*cardWidth, 100);
		tableCardPos[8] = new RectF(9*cardWidth, 0, 9*cardWidth, 100);
		tableCardPos[9] = new RectF(9*cardWidth, 0, 10*cardWidth, 100);



		tempHand = state.getHand();
		cardsOnTable = state.getTable();
		if(tempHand != null){
			for(int i = 0; i < tempHand.length;i++){
				tempHand[i].drawOn(c,handCardPos[i]);//draws the cards in each position
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
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();	

		if( state != null && state.getGameStage() == CbgState.THROW_STAGE){//TODO make so cards have a selected state and delete 
			//the cards from hand
			for(int i = 0; i<6;i++){
				if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
					selectCard(tempHand[i]);//TODO only for test to succeed

				}
			}
		}
		if(state != null && state.getGameStage() == CbgState.PEG_STAGE){
			for(int i = 0; i<6;i++){
				if(handCardPos[i] != null && handCardPos[i].contains(x, y)){
					selectCard(tempHand[i]);//TODO only for test to succeed
				}
			}
		}
		return false;
	}

	private void selectCard(Card c) {
		// TODO Auto-generated method stub
		if(state.getGameStage() == CbgState.THROW_STAGE){
			if(toThrow[0] == null && toThrow[1] == null){
				toThrow[0] = c;
			}else if(toThrow[0] !=null && toThrow[1] == null){
				toThrow[1] =c;
			}else{

			}
		}else if(state.getGameStage() == CbgState.PEG_STAGE){
			toTable = c;
		}

	}

	@Override
	public void onTouch(MotionEvent event) {
		//TODO leave alone for now
	}

	//TODO make a get playerIDX method
}
