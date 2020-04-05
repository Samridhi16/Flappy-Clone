package com.sam.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture dizzy;
	int manState =0;
	int pause=0;
	int manY =0;
	float gravity =0.2f;
	float velocity=0;

	int coinCount =0;
	int bombCount=0;

	int score=0;
	int gameState =0;

	Rectangle manRectangle;
	BitmapFont font;

	//Coin
	ArrayList<Integer> coinX =new ArrayList<>();
	ArrayList<Integer> coinY = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle =new ArrayList<>();
	Texture coin;

	//Bomb
	ArrayList<Integer> bombX =new ArrayList<>();
	ArrayList<Integer> bombY = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle =new ArrayList<>();
	Texture bomb;

	Random random;

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0]= new Texture("frame-1.png");
		man[1]= new Texture("frame-2.png");
		man[2]= new Texture("frame-3.png");
		man[3]= new Texture("frame-4.png");

		dizzy =new Texture("dizzy-1.png");

		manY= Gdx.graphics.getHeight()/2;

		//COIN
		coin = new Texture("coin.png");
		//BOMB
		bomb = new Texture("bomb.png");
		random = new Random();

		manRectangle = new Rectangle();

		font =new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeCoin(){

		float height =random.nextFloat() * Gdx.graphics.getHeight();
		coinY.add((int)height);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){

		float height =random.nextFloat() * Gdx.graphics.getHeight();
		bombY.add((int)height);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if(gameState == 1){
			//GAME IS LIVE
			//BOMB
			if(bombCount<260){
				bombCount++;
			}else{
				bombCount=0;
				makeBomb();
			}

			bombRectangle.clear();
			for(int i=0;i<bombX.size();i++){
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				bombX.set(i,bombX.get(i)-8);
				bombRectangle.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//COIN
			if(coinCount<100){
				coinCount++;
			}else{
				coinCount=0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i=0;i<coinX.size();i++){

				batch.draw(coin,coinX.get(i),coinY.get(i));
				coinX.set(i,coinX.get(i)-4);
				coinRectangle.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}

			if(Gdx.input.justTouched()){
				velocity=-10;
			}

			if(pause <8){
				pause++;
			}else {
				pause=0;
				if (manState < 3)
					manState++;
				else
					manState = 0;
			}

			velocity+=gravity;
			manY-=velocity;

			if(manY<=0) {
				manY = 0;
			}

		}else if (gameState == 0){
			//Waiting to start
			if(Gdx.input.justTouched()){
				gameState =1;
			}
		}else if(gameState ==2){
			//GAME OVER
			if(Gdx.input.justTouched()){
				gameState =1;
			}
			manY= Gdx.graphics.getHeight()/2;
			score=0;
			velocity=0;
			//COIN
			coinX.clear();
			coinY.clear();
			coinRectangle.clear();
			coinCount=0;
			//BOMB
			bombX.clear();
			bombY.clear();
			bombRectangle.clear();
			bombCount=0;
		}

		if(gameState==2) {
					batch.draw(dizzy, (Gdx.graphics.getWidth() / 2) - (man[manState].getWidth() / 2), manY);
		}else {
			batch.draw(man[manState], (Gdx.graphics.getWidth() / 2) - (man[manState].getWidth() / 2), manY);
		}
		manRectangle= new Rectangle(Gdx.graphics.getWidth()/2-man[manState].getWidth()/2,manY,man[manState].getWidth(),man[manState].getHeight());

		//COIN
		for(int i=0;i<coinRectangle.size();i++){
			if(Intersector.overlaps(coinRectangle.get(i),manRectangle)){
				score++;
				//Gdx.app.log("Coin!!","Collision");

				coinRectangle.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		}

		//BOMB
		for(int i=0;i<bombRectangle.size();i++){
			if(Intersector.overlaps(bombRectangle.get(i),manRectangle)){
				//Gdx.app.log("Bomb!!","Collision");
				gameState =2;

			}
		}

		font.draw(batch,String.valueOf(score),100,200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
