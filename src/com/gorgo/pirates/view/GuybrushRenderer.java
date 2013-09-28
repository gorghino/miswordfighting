package com.gorgo.pirates.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.gorgo.pirates.R;
import com.gorgo.pirates.controller.GameEngine;
import com.gorgo.pirates.controller.GuybrushController;

/**
 * @author Gorgo
 * 
 * Renderer di Guybrush. Si occupa di visualizzare a video Animazioni e Testi
 * 
 */

public class GuybrushRenderer implements Renderer {

	private static final String TAG = GuybrushRenderer.class.getSimpleName();
	private final int 
		TEXT_PADDING = 25,
		TIMESHOW_STRING = 4000;
	
	private GuybrushController controllerG;
	private Text textGuy; // Testo da renderizzare
	private SpriteTile currentGuy; // Sprite da renderizzare

	public GuybrushRenderer(GuybrushController controllerG, Context context) {
		this.controllerG = controllerG;
		
		//Creo la Text di Guybrush
		textGuy = new Text(context); 
		controllerG.setText(textGuy);

		if (controllerG.getSprite() == null) {
			Log.d(TAG, "Sprite Guybrush caricata");
			currentGuy = new SpriteTile(R.drawable.guybrush_talk,
					R.xml.guybrush_talk, context);
			controllerG.setSprite(currentGuy);
		} else
			currentGuy = controllerG.getSprite(); //Aggiorno eventuale Sprite

		currentGuy.setCurrentAnimation(controllerG.getAnimation(), false);
		Log.d(TAG, "Guybrush CARICATO");
	}

	public void render(Canvas canvas, GameEngine gameEngine) {
		// Controllo se è stato inizializzato il rettangolo di disegno sulla canvas
		if(currentGuy.getDestRect() == null)	
			currentGuy.setDestRect(new Rect((int) (canvas.getWidth() / 4.63),
					(int) (canvas.getHeight() / 2.25),
					(int) (canvas.getWidth() / 1.63),
					(int) (canvas.getHeight() / 1.05)));
		
		// Check SurfaceDestroyed
		if (canvas != null) {

			// Se la Sprite è stata cambiata, settala
			if (controllerG.getSprite() != currentGuy && currentGuy != null)
				currentGuy = controllerG.getSprite();
		
			// Posiziono il testo
			textGuy.setXY((canvas.getWidth() / 80), (canvas.getHeight() / 5));

			// Disegno l'animazione
			if (controllerG.getLock() != 1) //Lock 1 = Non disegnare Guybrushs 
				myDraw(canvas);
			else
				return;

			// Se Guybrush sta parlando stampa cosa sta dicendo
			Speak(canvas, controllerG.getSpeech());
		}
	}

	// Metodo che controlla se Guybrush sta parlando via controller, e stampa a video
	private void Speak(Canvas canvas, String speech) {
		if (controllerG.getSpeaking()) {
			textGuy.setTimeShow(TIMESHOW_STRING); // 4 sec per scritta
			textGuy.setColor(Color.WHITE);
			if(textGuy.getLayout() == null){
				Log.d(TAG, "CREO LAYOUT GUYBRUSH");
				textGuy.createLayout(canvas, TEXT_PADDING);
			}	
			textGuy.draw(canvas);
		}
		if (textGuy.getFinished()) {
			controllerG.setSpeaking(false); //Se sono passati 4sec Guybrush smette di parlare
		}
	}

	// Metodo che disegna Guybrush
	private void myDraw(Canvas canvas) {
		currentGuy.draw(canvas);
		if (currentGuy.hasAnimationFinished())
			controllerG.setMoving(false); //Se l'animazione è finita, smette di muoversi
	}
}
