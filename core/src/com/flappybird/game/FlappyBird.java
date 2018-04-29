package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.compression.lzma.Base;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private Texture Background_day;
	private Texture Background_nigth;
	private Texture[] Bird_Blue;
	private Texture[] Bird_Red;
	private Texture[] Bird_Yellow;
	private Sprite[] spritebirdyellow;
	private Texture GameOver;
	private  Texture StartMesaje;
	private  Texture dificultMesaje;
	private Texture topTube_red;
	private  Texture bottomTube_red;
	private Texture topTube_green;
	private  Texture bottomTube_green;

	private float birdY;
	private float gap;
	private int numberOfPipes = 4;
	private float pipeVelocity;
	private float pipeX[] = new float[numberOfPipes];
	private float pipeYOffset[] = new float[numberOfPipes];
	private Random random;
	private float maxLine;
	private float minLine;
	private float distance;
	private float velocity;
	private float gravity;
	private int BirdState;
	private int GameState;
	private int BirdColor;
	private int pipeActivo;
	private int score;
	private BitmapFont font;

	private Sound wing;
	private Sound die;
	private  Sound point;
	private Sound hit;

	private Circle birdCircle;
	private Rectangle[] topPipes;
	private Rectangle[] bottomPipes;



	private Boolean day;
	@Override
	public void create () {
		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		day = true;	//Bandera para decir si es de dia o de noche;

		Background_day = new Texture("background-day.png"); //Cargar textura del dia
		Background_nigth = new Texture("background-night.png"); //Cargar textura de la noche

		StartMesaje = new Texture("message.png");
		dificultMesaje= new Texture("message1.png");


		//sonidos
		wing =  Gdx.audio.newSound(Gdx.files.internal("wing.wav"));
		die = Gdx.audio.newSound(Gdx.files.internal("die.wav"));
		point = Gdx.audio.newSound(Gdx.files.internal("point.wav"));
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));

		//Pajaro amarilo
		Bird_Yellow = new Texture[3];
		Bird_Yellow[0]= new Texture("yellowbird-downflap.png");
		Bird_Yellow[1]= new Texture("yellowbird-midflap.png");
		Bird_Yellow[2]= new Texture("yellowbird-upflap.png");
		spritebirdyellow = new  Sprite[3];
		spritebirdyellow[0] = new Sprite(Bird_Yellow[0]);
		spritebirdyellow[1] = new Sprite(Bird_Yellow[1]);
		spritebirdyellow[2] = new Sprite(Bird_Yellow[2]);

		//Pajaro azul
		Bird_Blue = new Texture[3];
		Bird_Blue[0]= new Texture("bluebird-downflap.png");
		Bird_Blue[1]= new Texture("bluebird-midflap.png");
		Bird_Blue[2]= new Texture("bluebird-upflap.png");

		//Pajaro rojo
		Bird_Red = new Texture[3];
		Bird_Red[0]= new Texture("redbird-downflap.png");
		Bird_Red[1]= new Texture("redbird-midflap.png");
		Bird_Red[2]= new Texture("redbird-upflap.png");

		//Tubos verdes
		bottomTube_green = new Texture("pipedown-green.png");
		topTube_green = new Texture("pipeup-green.png");

		//Tubos rojos
		bottomTube_red = new Texture("pipedowm-red.png");
		topTube_red = new Texture("pipeup-red.png");
		birdCircle = new Circle();
		topPipes = new Rectangle[numberOfPipes];
		bottomPipes = new Rectangle[numberOfPipes];



		GameOver =  new Texture("gameover.png");

		//topPipes = new Rectangle[numberOfPipes];
		//bottomPipes = new Rectangle[numberOfPipes];
		gap = 500;
		random = new Random();
		distance = Gdx.graphics.getWidth() * 3/5;
		maxLine = Gdx.graphics.getHeight()* 3/4;
		minLine = Gdx.graphics.getHeight()* 1/4;
		BirdState = 0;  //Estado del pajaro (Alas arriba , Alas medio , Alas abajo)
		GameState = 0;
		BirdColor = 0;	//Color de los pajaros;
		gravity = 0.8f;
		birdY = Gdx.graphics.getHeight()/2 - Bird_Blue[BirdState].getHeight()/2 + 200;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);



	}
	private void startGame(){
		birdY = Gdx.graphics.getHeight()/2 - Bird_Blue[BirdState].getHeight()/2 + 200;
		for (int i = 0; i<numberOfPipes; i++){
			pipeYOffset[i] = (random.nextFloat()*(maxLine-minLine)+minLine);
			pipeX[i] = Gdx.graphics.getWidth()/2 - topTube_red.getWidth() + Gdx.graphics.getWidth() + distance*i;

			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}

	}


	@Override
	public void render () {
		batch.begin();
		if(day){
			batch.draw(Background_day, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}else {
			batch.draw(Background_nigth, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}


		if(GameState == 0){  //El juego no ha iniciado
			batch.draw(StartMesaje,(Gdx.graphics.getWidth()/2)-250,400,500,500);
			batch.draw(dificultMesaje,(Gdx.graphics.getWidth()/2)-250,1200,500,500);

			score = 0;
			pipeActivo = 0;
			velocity = 0;
			if(Gdx.input.isTouched())
			{
				Vector3 tmp=new Vector3(Gdx.input.getX(),Gdx.input.getY(), 0);
				Rectangle textureBounds = new Rectangle(Gdx.graphics.getWidth() / 2 - ((Bird_Yellow[BirdState].getWidth()*4)/2),  birdY-500,  Bird_Yellow[BirdState].getWidth()*5, Bird_Yellow[BirdState].getHeight()*5);
				Rectangle textureBounds2 = new Rectangle((Gdx.graphics.getWidth()/2)-250,1100,500,500);
				if(textureBounds.contains(tmp.x,tmp.y)) {
					if(BirdColor > 2){
						BirdColor = 0;
					}else {
						BirdColor = BirdColor+1;
					}

				}else if (textureBounds2.contains(tmp.x,tmp.y)){
					startGame();
					GameState = 1;
				}
			}

		}else if (GameState == 1) { //Inicia el juego
			if (pipeX[pipeActivo] < Gdx.graphics.getWidth()/2 - topTube_green.getWidth()){
				point.play();
				score++;

				if (pipeActivo < numberOfPipes - 1){
					pipeActivo++;
				}
				else {
					pipeActivo = 0;
				}

				Gdx.app.log("score", Integer.toString(score));
			}
			//Crea el circulo(shape) del bird para manejar coliciones.
			birdCircle.set(Gdx.graphics.getWidth() / 2 - ((Bird_Yellow[BirdState].getWidth()*5)/2),  birdY, (Bird_Yellow[BirdState].getWidth()*4)/2);
			//Dibuja los shapes para entender el proceso de colisiones.
			/*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.MAGENTA);
			shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);*/


			for (int i = 0; i < numberOfPipes; i++) {
				if (pipeX[i] < -topTube_green.getWidth()) {
					pipeYOffset[i] = (random.nextFloat() * (maxLine - minLine) + minLine);
					pipeX[i] += distance * (numberOfPipes);
				} else {
					pipeX[i] = pipeX[i] - pipeVelocity;
				}
				//Dibujar los tubos (si es > 0 dibuje tubos rojos, sino son verdes)
				if(BirdColor > 0 ){
					batch.draw(topTube_red,
							pipeX[i],
							pipeYOffset[i] + gap / 2,
							topTube_red.getWidth() * 5,
							topTube_red.getHeight() * 5);
					batch.draw(bottomTube_red,
							pipeX[i],
							pipeYOffset[i] - (bottomTube_red.getHeight() * 5) - gap / 2,
							bottomTube_red.getWidth() * 5,
							bottomTube_red.getHeight() * 5);
				}else {
					batch.draw(topTube_green,
							pipeX[i],
							pipeYOffset[i] + gap / 2,
							topTube_green.getWidth() * 5,
							topTube_green.getHeight() * 5);
					batch.draw(bottomTube_green,
							pipeX[i],
							pipeYOffset[i] - (bottomTube_green.getHeight() * 5) - gap / 2,
							bottomTube_green.getWidth() * 5,
							bottomTube_green.getHeight() * 5);
				}

				//Crea los shapes de los tubos
				topPipes[i] = new Rectangle(pipeX[i],
						pipeYOffset[i]+gap/2,
						topTube_red.getWidth()*5,
						topTube_red.getHeight()*5);
				bottomPipes[i] = new Rectangle(pipeX[i],
						(pipeYOffset[i]-bottomTube_red.getHeight()*5)-gap/2,
						bottomTube_red.getWidth()*5,
						bottomTube_red.getHeight()*5);

				//Dibuja los shapes de los tubos para entender las coliciones
				/*shapeRenderer.rect(topPipes[i].x, topPipes[i].y, topTube_red.getWidth()*5,
						topTube_red.getHeight()*5);
				shapeRenderer.rect(bottomPipes[i].x, bottomPipes[i].y, bottomTube_red.getWidth()*5,
						bottomTube_red.getHeight()*5);*/

				if (Intersector.overlaps(birdCircle, topPipes[i])){
					Gdx.app.log("Intersector", "top pipe overlap");
					hit.play();
					die.play();
					GameState = 2;
				}
				else if (Intersector.overlaps(birdCircle, bottomPipes[i])){
					Gdx.app.log("Intersector", "bottom pipe overlap");
					hit.play();
					die.play();
					GameState = 2;
				}

			}


			if (Gdx.input.justTouched()){
				velocity = velocity - 15;
				/*float rotar = 5;
				rotar += spritebirdyellow[BirdState].getRotation();
				spritebirdyellow[BirdState].rotate(rotar);
				spritebirdyellow[BirdState].draw(batch);*/
				wing.play();
			}

			BirdState = BirdState == 0 ? 2 : 0;


			velocity = velocity + gravity;


			if (birdY < 0 || birdY > Gdx.graphics.getHeight()){
				die.play();
				GameState = 2;
			}
			else {
				birdY = birdY - velocity;
			}
			shapeRenderer.end();
		}else if(GameState == 2){ //Se acabo el juego

			batch.draw(GameOver,(Gdx.graphics.getWidth() / 2 - ((GameOver.getWidth()*5)/2)), Gdx.graphics.getHeight()/2-GameOver.getHeight()/2,GameOver.getWidth()*5,GameOver.getHeight()*5 );
			if (Gdx.input.justTouched()){
				birdY = Gdx.graphics.getHeight()/2 - Bird_Blue[BirdState].getHeight()/2 + 200;
				GameState = 0;

			}

		}
		if (BirdColor ==0){
			day = true;
			batch.draw(Bird_Yellow[BirdState], Gdx.graphics.getWidth() / 2 - ((Bird_Yellow[BirdState].getWidth()*5)/2),  birdY,  Bird_Yellow[BirdState].getWidth()*5, Bird_Yellow[BirdState].getHeight()*5);
			pipeVelocity = 5;
		}else if(BirdColor == 1){
			batch.draw(Bird_Blue[BirdState], Gdx.graphics.getWidth() / 2 - ((Bird_Blue[BirdState].getWidth()*5)/2),  birdY,  Bird_Blue[BirdState].getWidth()*5, Bird_Blue[BirdState].getHeight()*5);
			pipeVelocity = 10;
		}else {
			pipeVelocity = 15;
			day = false;
			batch.draw(Bird_Red[BirdState], Gdx.graphics.getWidth() / 2 - ((Bird_Red[BirdState].getWidth()*5)/2),  birdY,  Bird_Red[BirdState].getWidth()*5, Bird_Red[BirdState].getHeight()*5);
		}
		if (GameState == 1){
			font.draw(batch, Integer.toString(score), Gdx.graphics.getWidth()*1/32, Gdx.graphics.getHeight()-50);
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		if (day){
			Background_day.dispose();
		}else {
			Background_nigth.dispose();
		}
	}

}
