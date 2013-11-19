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
	
	/*
     * Override
     */

    public CbgHumanPlayer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public final void sendCardToCrib() {
		
    }

    public final void confirm() {
    
    }
    
    public final void sendCardToTable() {
    
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
		
		tableSurface.findViewById(R.id.tableSurface);
		handSurface.findViewById(R.id.handSurface);
		cribSurface.findViewById(R.id.cribSurface);
		deckSurface.findViewById(R.id.deckSurface);
		tableSurface.setOnTouchListener(this);
		handSurface.setOnTouchListener(this);
		cribSurface.setOnTouchListener(this);
		deckSurface.setOnTouchListener(this);
		
		tallyCount.findViewById(R.id.tallyCount);
		tutorial.findViewById(R.id.tutorialText);
		p2Score.findViewById(R.id.p2Score);
		p1Score.findViewById(R.id.p1score);
		
		p2Progress.findViewById(R.id.player2progress);
		p1Progress.findViewById(R.id.Player1progress);
		
		menu.findViewById(R.id.menuButton);
		confirm.findViewById(R.id.confirmButton);
		menu.setOnClickListener(this);
		confirm.setOnClickListener(this);
		
		receiveInfo(state);
	}

	@Override
	public View getTopView() {
		// TODO Auto-generated method stub
		return null;
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
		//TODOs
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onTouch(MotionEvent event) {
		// TODO Auto-generated method stub
		
	}
}
