package com.gorgo.pirates.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.gorgo.pirates.R;
import com.gorgo.pirates.controller.EnemyController;
import com.gorgo.pirates.controller.GameEngine;

/**
 * @author Gorgo
 * 
 * Renderer dello sfidante. Si occupa di visualizzare a video Animazioni e Testi
 * 
 */

public class EnemyRenderer implements Renderer {

	private static final String TAG = EnemyRenderer.class.getSimpleName();
	private final int 
		TEXT_PADDING = 25,
		TIMESHOW_STRING = 4000;
	
	private EnemyController controllerE;
	private Text textEnemy;
	private SpriteTile currentEnemy;

	public EnemyRenderer(EnemyController controllerE, Context context) {
		this.controllerE = controllerE;
		
		//Creo la Text del pirata
		textEnemy = new Text(context);
		controllerE.setText(textEnemy);

		if (controllerE.getSprite() == null) {
			Log.d(TAG, "Sprite Pirata caricata");
			currentEnemy = new SpriteTile(R.drawable.pirate1_talk,
					R.xml.pirate_talk, context);
			controllerE.setSprite(currentEnemy);
		} else
			currentEnemy = controllerE.getSprite(); //Aggiorno eventuale Sprite

		currentEnemy.setCurrentAnimation(controllerE.getAnimation(), false);
		Log.d(TAG, "Pirata CARICATO");
	}

	@Override
	public void render(Canvas canvas, GameEngine gameEngine) {
		// Controllo se è stato inizializzato il rettangolo di disegno sulla canvas
		if(currentEnemy.getDestRect() == null)	
			currentEnemy.setDestRect(new Rect((int) (canvas.getWidth() / 2.50),
					(int) (canvas.getHeight() / 2.11),
					(int) (canvas.getWidth() / 1.40),
					(int) (canvas.getHeight() / 1.15)));
		
		// Check SurfaceDestroyed
		if (canvas != null) {

			// Se la Sprite è stata cambiata, settala
			if (controllerE.getSprite() != currentEnemy && currentEnemy != null)
				currentEnemy = controllerE.getSprite();
			
			// Posiziono il testo
			textEnemy.setXY((canvas.getWidth() / 80), (canvas.getHeight() / 5));

			// Disegno l'animazione
			if (controllerE.getLock() != 1) //Lock 1 = Non disegnare il pirata 
				myDraw(canvas);
			else
				return;

			// Se il pirata sta parlando stampa cosa sta dicendo
			Speak(canvas, controllerE.getSpeech());

		}
	}

	// Metodo che controlla se il Pirata sta parlando via controller, e stampa a video
	private void Speak(Canvas canvas, String speech) {
		if (controllerE.getSpeaking()) {
			textEnemy.setTimeShow(TIMESHOW_STRING); // 4 sec per scritta
			textEnemy.setColor(controllerE.getColor());
			textEnemy.setString(speech);
			if(textEnemy.getLayout() == null){
				Log.d("Creo Layout", "CREO LAYOUT PIRATE");
				textEnemy.createLayout(canvas, TEXT_PADDING);
			}
			textEnemy.draw(canvas);
		}
		if (textEnemy.getFinished()) {
			controllerE.setSpeaking(false); //Se sono passati 4sec Guybrush smette di parlare
		}
	}

	// Metodo che controlla disegna il Pirata
	private void myDraw(Canvas canvas) {
		currentEnemy.draw(canvas);
		if (currentEnemy.hasAnimationFinished())
			controllerE.setMoving(false);
	}

}
