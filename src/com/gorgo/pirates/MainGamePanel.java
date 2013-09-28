package com.gorgo.pirates;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import com.gorgo.pirates.controller.WorldController;

/**
 * @author Gorgo
 * 
 * SurfaceView è la classe per gestire la Canvas (SurfaceHolder) e il TouchScreen
 * 
 */

public class MainGamePanel extends SurfaceView {

	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	private static final int
		WAITING_PIRATE = 0,
		NO_LEVEL_SELECTED = 0,
		NO_INTRO_SKIPPED = 0,
		IDLE_MODE = 0,	
		INTRO_SKIPPED = 1,
		MAP_MODE = 1,
		OK_CARLA = 1,
		LEVEL_MODE = 2,
		SKIP_MODE = 3;
	
	//Pirati
	private static final int
		PIRATA_BIONDO = 1,
		PIRATA_CAPPELLO = 2,
		PIRATA_BANDANA = 3,
		PIRATA_PELATO = 4,
		CARLA = 5;
	
	//Livelli
	private static final int
		FACILE = 1,
		MEDIO = 2,
		DIFFICILE = 3;
	 

	//Coordinate quadrati mappa
	private float x1_cappello, x2_cappello, y1_cappello, y2_cappello;
	private float x1_bandana, x2_bandana, y1_bandana, y2_bandana;
	private float x1_pelato, x2_pelato, y1_pelato, y2_pelato;
	private float x1_biondo, x2_biondo, y1_biondo, y2_biondo;
	private float x1_carla, x2_carla, y1_carla, y2_carla;
	private float x1_facile, x2_facile, y1_facile, y2_facile;
	private float x1_medio, x2_medio, y1_medio, y2_medio;
	private float x1_difficile, x2_difficile, y1_difficile, y2_difficile;
	
	//Coordinate tasto SkipIntro
	private float x1_skip, x2_skip, y1_skip, y2_skip;
	
	private WorldController controllerW;
	private int mode;

	public MainGamePanel(Context context) {
		this(context, null);
	}

	public MainGamePanel(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.style.Theme_Black);
	}

	public MainGamePanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (controllerW.getPirate() == WAITING_PIRATE || controllerW.getLevel() == NO_LEVEL_SELECTED || controllerW.getSkipped() == NO_INTRO_SKIPPED) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				
				// Salvo X/Y tocco
				float posX = event.getX();
				float posY = event.getY();

				if (mode == MAP_MODE) {

					if ((posX >= x1_cappello && posX <= x2_cappello)
							&& (posY >= y1_cappello && posY <= y2_cappello)) {
						controllerW.setPirate(PIRATA_CAPPELLO);
						
					} else if ((posX >= x1_bandana && posX <= x2_bandana)
							&& (posY >= y1_bandana && posY <= y2_bandana)) {
						controllerW.setPirate(PIRATA_BANDANA);
						
					} else if ((posX >= x1_pelato && posX <= x2_pelato)
							&& (posY >= y1_pelato && posY <= y2_pelato)) {
						controllerW.setPirate(PIRATA_PELATO);
						
					} else if ((posX >= x1_biondo && posX <= x2_biondo)
							&& (posY >= y1_biondo && posY <= y2_biondo)) {
						controllerW.setPirate(PIRATA_BIONDO);
						
					} else if ((posX >= x1_carla && posX <= x2_carla)
							&& (posY >= y1_carla && posY <= y2_carla)) {
						controllerW.setPirate(CARLA);
					} else {
						Log.d(TAG,"Non è stato selezionato nessuno");
					}
					break;
					
				} else if (mode == LEVEL_MODE){
					if ((posX >= x1_facile && posX <= x2_facile)
							&& (posY >= y1_facile && posY <= y2_facile)) {
						controllerW.setLevel(FACILE);
						mode = IDLE_MODE;
						
					} else if ((posX >= x1_medio && posX <= x2_medio)
							&& (posY >= y1_medio && posY <= y2_medio)) {
						controllerW.setLevel(MEDIO);
						mode = IDLE_MODE;
						
					} else if ((posX >= x1_difficile && posX <= x2_difficile)
							&& (posY >= y1_difficile && posY <= y2_difficile)) {
						controllerW.setLevel(DIFFICILE);
						mode = IDLE_MODE;
						
					} else {
						Log.d(TAG, "Non è stato selezionato nessun livello");
					}
					break;
					
				} else if(mode == SKIP_MODE){
					if ((posX >= x1_skip && posX <= x2_skip)
							&& (posY >= y1_skip && posY <= y2_skip)) {
						controllerW.setSkipped(INTRO_SKIPPED);
						mode = IDLE_MODE;
					}
					else{
						Log.d(TAG,"Non è stato skippato");
					}
				}
			}
			case MotionEvent.ACTION_UP: {
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				break;
			}
			}
		}
		return false;
	}
	
	//Inizializza le coordinate della mappa per il Touch
	public void initMap(int checkCarla) {

		mode = MAP_MODE;

		// Pirata Cappello
		x1_cappello = (float) (getWidth() / 8.15);
		x2_cappello = (float) (getWidth() / 4.72);
		y1_cappello = (float) (getHeight() / 2.88);
		y2_cappello = (float) (getHeight() / 2.08);

		// Pirata Bandana
		x1_bandana = (float) (getWidth() / 2.62);
		x2_bandana = (float) (getWidth() / 2.14);
		y1_bandana = (float) (getHeight() / 1.97);
		y2_bandana = (float) (getHeight() / 1.57);

		// Pirata pelato
		x1_pelato = (float) (getWidth() / 1.66);
		x2_pelato = (float) (getWidth() / 1.45);
		y1_pelato = (float) (getHeight() / 1.55);
		y2_pelato = (float) (getHeight() / 1.29);

		// Pirata biondo
		x1_biondo = (float) (getWidth() / 1.92);
		x2_biondo = (float) (getWidth() / 1.64);
		y1_biondo = (float) (getHeight() / 13.11);
		y2_biondo = (float) (getHeight() / 4.73);

		if(checkCarla == OK_CARLA){
		// Carla
		x1_carla = (float) (getWidth() / 3.67);
		x2_carla = (float) (getWidth() / 2.76);
		y1_carla = (float) (getHeight() / 6.01);
		y2_carla = (float) (getHeight() / 3.34);
		}
	}

	//Inizializza le coordinate dei riquadri dei livelli per il Touch
	public void InitLevels() {
		
		mode = LEVEL_MODE;	
		
		// Facile
		x1_facile = (float) (getWidth() / 61.53);
		x2_facile = (float) (getWidth() / 3.13);
		y1_facile = (float) (getHeight() / 28.23);
		y2_facile = (float) (getHeight() / 1.25);

		// Medio
		x1_medio = (float) (getWidth() / 2.87);
		x2_medio = (float) (getWidth() / 1.53);
		y1_medio = (float) (getHeight() / 28.23);
		y2_medio = (float) (getHeight() / 1.25);

		// Difficile
		x1_difficile = (float) (getWidth() / 1.47);
		x2_difficile = (float) (getWidth() / 1.01);
		y1_difficile = (float) (getHeight() / 28.23);
		y2_difficile = (float) (getHeight() / 1.25);
	}
	
	//Inizializza le coordinate del tasto SkipIntro per il Touch
	public void initSkip(){
		
		this.mode = SKIP_MODE;
		
		x1_skip = 0;
		x2_skip = (float) (getWidth() / 17.02);
		y1_skip = (float) (getHeight() /  1.07);
		y2_skip = getHeight();
	}
	

	public void setWorldController(WorldController controllerW) {
		this.controllerW = controllerW;
	}
}