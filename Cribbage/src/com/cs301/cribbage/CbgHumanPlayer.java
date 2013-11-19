package com.cs301.cribbage;

import android.graphics.Canvas;
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
	private AnimationSurface handSurface;
	private AnimationSurface cribSurface;
	private AnimationSurface deckSurface;

	private TextView tallyCount;
	private TextView tutorial;
	private TextView p2Score;
	private TextView p1Score;

	private ProgressBar p2Progress;
	private ProgressBar p1Progress;

	private Button menu;
	private Button confirm;

	private RectF[] handCardPos = new RectF[6];//positions that save where the cards are drawn in the players hand
	private Card[] tempHand = new Card[6];

	private Card[] toThrow = new Card[2];
	private Card toTable;

	GameAction action;
	/*
	 * Override
	 */

	public CbgHumanPlayer(String name) {
		super(name);
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

		tableSurface = (AnimationSurface) myActivity.findViewById(R.id.tableSurface);
		handSurface = (AnimationSurface) myActivity.findViewById(R.id.handSurface);
		cribSurface = (AnimationSurface) myActivity.findViewById(R.id.cribSurface);
		deckSurface = (AnimationSurface) myActivity.findViewById(R.id.deckSurface);
		tableSurface.setOnTouchListener(this);
		handSurface.setOnTouchListener(this);
		cribSurface.setOnTouchListener(this);
		deckSurface.setOnTouchListener(this);

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

		receiveInfo(state);
	}

	@Override
	public View getTopView() {
		//TODO Auto-generated method stub
		 return myActivity.findViewById(R.layout.activity_main);
	}

	@Override
	public void receiveInfo(GameInfo info) {
		if(info instanceof CbgState){
			this.state = (CbgState)info;
			updateDisplay();
		}		
	}

	@Override
	public int interval() {		
		return 50;
	}

	@Override
	public int backgroundColor() {		
		return 0x347C17;
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
	public void tick(Canvas c) {
		float cardWidth = c.getWidth()/6;
		//init rectangles for cards to be drawn on
		handCardPos[0] = new RectF(0, 0, cardWidth, c.getHeight());
		handCardPos[1] = new RectF(cardWidth, 0, (2*cardWidth) ,c.getHeight());
		handCardPos[2] = new RectF(2*cardWidth, 0, 3*cardWidth, c.getHeight());
		handCardPos[3] = new RectF(3*cardWidth, 0, 4*cardWidth, c.getHeight());
		handCardPos[4] = new RectF(4*cardWidth, 0, 5*cardWidth, c.getHeight());
		handCardPos[5] = new RectF(5*cardWidth, 0, 6*cardWidth, c.getHeight());

		tempHand = state.getHand(state.PLAYER_1);
		for(int i = 0; i < 6;i++){
			tempHand[i].drawOn(c,handCardPos[i]);//draws the cards in each position
		}		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == confirm.getId()){

		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();		
		for(int i = 0; i<6;i++){
			if(state.getGameStage() == state.THROW_STAGE){
				if(handCardPos[i].contains(x, y)){
					selectCard(tempHand[i]);//TODO only for test to succeed
				}
			}
		}
		return false;
	}

	private void selectCard(Card c) {
		// TODO Auto-generated method stub
		if(state.getGameStage() == state.THROW_STAGE){
			if(toThrow[0] == null && toThrow[1] == null){
				toThrow[0] = c;
			}else if(toThrow[0] !=null && toThrow[1] == null){
				toThrow[1] =c;
			}else{

			}
		}else if(state.getGameStage() == state.PEG_STAGE){
			toTable = c;
		}
	}

	@Override
	public void onTouch(MotionEvent event) {
		//TODO leave alone for now
	}
}
