package com.gorgo.pirates.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import com.gorgo.pirates.controller.GameEngine;
import com.gorgo.pirates.controller.WorldController;

/**
 * @author Gorgo
 * 
 * Renderer di World. Si occupa di visualizzare a video scritte del gioco o animazioni introduzione/outro
 * 
 */

public class WorldRenderer implements Renderer {

	private static final String TAG = WorldRenderer.class.getSimpleName();
	
	private final int 
		INTRO_SCABB = 1,
		FUOCO_INIZIO = 2,
		FUOCO_FINE = 3;
	
	// Sprite
	private final int
		FUOCHERELLO = 0,
		GUYBRUSH = 1,
		PIRATA_SX = 2,
		PIRATA_DX = 3,
		FUOCONE = 4;
				

	private WorldController controllerW;
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public WorldRenderer(WorldController controllerW, Context context) {
		this.controllerW = controllerW;
	}

	@Override
	public void render(Canvas canvas, GameEngine gameEngine) {
		//Controllo i rettangoli di disegno
		checkRectDest(canvas);
		
		// Check SurfaceDestroyed
		if (canvas != null) {
			if (controllerW.getSfondo() != null)
				canvas.drawBitmap(controllerW.getSfondo(), null, new Rect(0, 0,
						canvas.getWidth(), canvas.getHeight()), paint);
			
			// INTRO SCABB ISOLA
			if(controllerW.getIntro() == INTRO_SCABB){	
				if(controllerW.getText(FUOCHERELLO).getLayout() == null){
					Text textIntro = controllerW.getText(FUOCHERELLO);
					textIntro.setXY((float)(canvas.getWidth()/10.34),
							(float)(canvas.getHeight()/1.22));
					textIntro.createLayout(canvas, canvas.getWidth()/6);
					Log.d(TAG, "LAYOUT MONDO CREATO");	
				}
				
				controllerW.getText(FUOCHERELLO).draw(canvas);
				controllerW.getSprite(FUOCHERELLO).draw(canvas);
			}
			// INTRO SCABB FUOCO
			// Creo i Layout delle scritte
			else if(controllerW.getIntro() == FUOCO_INIZIO){
				if(controllerW.getText(GUYBRUSH).getLayout() == null){
					controllerW.getText(GUYBRUSH).setXY((float)(canvas.getWidth()/2.16),(float)(canvas.getHeight()/5.28));
					controllerW.getText(GUYBRUSH).createLayout(canvas, canvas.getWidth()/2);
					Log.d(TAG, "CREATO LAYOUT GUYBRUSH");
				}
				if(controllerW.getText(PIRATA_SX).getLayout() == null){
					controllerW.getText(PIRATA_SX).setXY((float)(canvas.getWidth()/33.68),(float)(canvas.getHeight()/7.11));
					controllerW.getText(PIRATA_SX).createLayout(canvas, canvas.getWidth()/2);
					Log.d(TAG, "CREATO LAYOUT PIRATE SX");
				}
				if(controllerW.getText(PIRATA_DX).getLayout() == null){
					controllerW.getText(PIRATA_DX).setXY((float)(canvas.getWidth()/2.57),(float)(canvas.getHeight()/9.9));
					controllerW.getText(PIRATA_DX).createLayout(canvas, canvas.getWidth()/2);
					Log.d(TAG, "CREATO LAYOUT PIRATE DX");
				}
				
				// Disegno i 3 pirati e Fuoco
				controllerW.getSprite(GUYBRUSH).draw(canvas); 
				controllerW.getSprite(PIRATA_SX).draw(canvas); 
				controllerW.getSprite(PIRATA_DX).draw(canvas); 
				controllerW.getSprite(FUOCONE).draw(canvas); 
				
				// Mostro gli eventuali discorsi
				Speak(canvas, controllerW.getSpeech(GUYBRUSH), GUYBRUSH);
				Speak(canvas, controllerW.getSpeech(PIRATA_SX), PIRATA_SX);
				Speak(canvas, controllerW.getSpeech(PIRATA_DX), PIRATA_DX);
			}
			// OUTRO GUYBRUSH SOLO
			else if(controllerW.getIntro() == FUOCO_FINE){
				if(controllerW.getText(GUYBRUSH).getLayout() == null){
					controllerW.getText(GUYBRUSH).setXY((float)(canvas.getWidth()/2.16),(float)(canvas.getHeight()/5.28));
					controllerW.getText(GUYBRUSH).createLayout(canvas, canvas.getWidth()/2);
					Log.d(TAG, "CREATO LAYOUT GUYBRUSH");
				}
				controllerW.getSprite(GUYBRUSH).draw(canvas); 
				controllerW.getSprite(FUOCONE).draw(canvas);
				Speak(canvas, controllerW.getSpeech(GUYBRUSH), GUYBRUSH);
			}	
			
			// Visualizzo FPS
			displayFps(canvas, controllerW.getAvgFps());
		}
	}

	// Metodo che controlla se sono stati inizializzati i rettangoli di disegno sulla canvas
	private void checkRectDest(Canvas canvas) {
		switch(controllerW.getIntro()){
		case 1:
			//Fuocherello
				if(controllerW.getSprite(0).getDestRect() == null)	
					controllerW.getSprite(0).setDestRect(new Rect(
							(int) (canvas.getWidth() / 1.99),
							(int) (canvas.getHeight() / 1.85),
							(int) (canvas.getWidth() / 1.89),
							(int) (canvas.getHeight() / 1.42)));
				break;
		case 2:
				
				//Pirata SX
				if(controllerW.getSprite(2).getDestRect() == null)	
					controllerW.getSprite(2).setDestRect(new Rect(
							(int) (canvas.getWidth() / 13.91),
							(int) (canvas.getHeight() / 4.60),
							(int) (canvas.getWidth() / 3.10),
							(int) (canvas.getHeight() / 1.20)));
				//Pirata DX
				if(controllerW.getSprite(3).getDestRect() == null)	
					controllerW.getSprite(3).setDestRect(new Rect(
							(int) (canvas.getWidth() / 2.08),
							(int) (canvas.getHeight() / 4.68),
							(int) (canvas.getWidth() / 1.32),
							(int) (canvas.getHeight() / 1.40)));
		case 3:
				//Guybrush
				if(controllerW.getSprite(1).getDestRect() == null)	
					controllerW.getSprite(1).setDestRect(new Rect(
							(int) (canvas.getWidth() / 1.42),
							(int) (canvas.getHeight() / 2.39),
							(int) (canvas.getWidth() / 1.05),
							(int) (canvas.getHeight() / 1.15)));
			
				//Fuocone
				if(controllerW.getSprite(4).getDestRect() == null)	
					controllerW.getSprite(4).setDestRect(new Rect(
							(int) (canvas.getWidth() / 2.74),
							(int) (canvas.getHeight() / 2.60),
							(int) (canvas.getWidth() / 1.94),
							(int) (canvas.getHeight() / 1.20)));	
				break;
		}
	}

	// Metodo che stampa nella canvas le scritte dei discorsi
	private void Speak(Canvas canvas, String speech, int who) {
		if (controllerW.getSpeaking(who)) {
			controllerW.getText(who).draw(canvas);
			
			if (controllerW.getText(who).getFinished()) {
				controllerW.setSpeaking(who, false);
			}
		}	
	}

	// Metodo che visualizza gli FPS
	private void displayFps(Canvas canvas, String fps) {
		if (canvas != null && fps != null) {
			Paint paint = new Paint();
			paint.setARGB(255, 255, 255, 255);
			canvas.drawText(fps, canvas.getWidth() - 50, 20, paint);
		}
	}

}
