package com.gorgo.pirates.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.ListView;

import com.gorgo.pirates.Dialogs;
import com.gorgo.pirates.GameThread;
import com.gorgo.pirates.MainGamePanel;
import com.gorgo.pirates.model.Enemy;
import com.gorgo.pirates.model.Guybrush;
import com.gorgo.pirates.model.World;
import com.gorgo.pirates.view.GuybrushRenderer;
import com.gorgo.pirates.view.ListViewCustomAdapter;
import com.gorgo.pirates.view.EnemyRenderer;
import com.gorgo.pirates.view.Renderer;
import com.gorgo.pirates.view.WorldRenderer;

/**
 * @author Gorgo
 * 
 * La classe si occupa di istanziare tutti i protagonisti del pattern MVC.
 * Inizializza la classe Dialogs contenente i discorsi tra pirati e gli insulti del gioco, 
 * il custom Adapter per la Listview, i controller e i renderer. 
 * 
 */

public class GameEngine {
	
	private static final String TAG = GameEngine.class.getSimpleName();

	//Models
	private Guybrush guybrush;
	private Enemy enemy;
	private World world;

	//Controllers
	private GuybrushController controllerGuybrush;
	private EnemyController controllerEnemy;
	private WorldController controllerWorld;
	
	private Turns mTurns; //Classe che gestisce i turni del Gioco
	
	private Dialogs mDialogs; //Classe che gestisce i dialoghi
	private ListViewCustomAdapter mAdapter;
	
	//View Renders
	private Renderer guybrushRenderer, pirateRenderer, worldRenderer;
	
	public GameEngine(Context context, MainGamePanel surface, ListView mlistView) {

		// Carico Guybrush e la sua animazione iniziale
		guybrush = new Guybrush();
		guybrush.setAnimation("parla");

		// Carico il pirata e la sua animazione iniziale
		enemy = new Enemy();
		enemy.setAnimation("pirata-parla");

		// Carico i dialoghi di Guybrush
		mDialogs = new Dialogs(context);
		mAdapter = new ListViewCustomAdapter(context, mDialogs.getGuybrushIntro());
		mlistView.setAdapter(mAdapter);
		Log.d(TAG, "DIALOGHI CARICATI");
		
		// Carico il World che controlla gli elementi inizializzati precedentemente
		world = new World(guybrush, enemy, mDialogs);		
		Log.d(TAG, "MODELLI CARICATI");
				
		// Carico i Turni di gioco e i Controllers
		controllerGuybrush = new GuybrushController(guybrush);
		controllerEnemy = new EnemyController(enemy);
		controllerWorld = new WorldController(world, surface);
		mTurns = new Turns(controllerGuybrush, controllerEnemy, controllerWorld, context);
		
		//Setto il controller della SurfaceView
		surface.setWorldController(controllerWorld);	

		// Carico i Renderer degli elementi
		guybrushRenderer = new GuybrushRenderer(controllerGuybrush, context);
		pirateRenderer = new EnemyRenderer(controllerEnemy, context);
		worldRenderer = new WorldRenderer(controllerWorld, context);

	}

	public void render(Canvas canvas) {
		worldRenderer.render(canvas, this);
		guybrushRenderer.render(canvas, this);
		pirateRenderer.render(canvas, this);
	}

	public void update(GameThread mainThread) {
		mTurns.update(mainThread);	
		controllerWorld.update();
		controllerGuybrush.update();
		controllerEnemy.update();	
	}

	public void setAvgFps(String avgFps) {
		world.setAvgFps(avgFps);
	}
	
	// Metodi Get() Istanze
	public Guybrush getGuybrush() {
		return this.guybrush;
	}	
	public World getWorldInstance(){
		return this.world;
	}
	public Turns getTurnsInstance(){
		return this.mTurns;
	}
	public Dialogs getDialogsInstance() {
		return this.mDialogs;
	}
	public GuybrushController getControllerG(){
		return this.controllerGuybrush;
	}
	public WorldController getControllerW() {
		return this.controllerWorld;
	}
}