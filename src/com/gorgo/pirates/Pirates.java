package com.gorgo.pirates;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.gorgo.pirates.controller.GameEngine;
import com.gorgo.pirates.controller.GuybrushController;
import com.gorgo.pirates.controller.Turns;
import com.gorgo.pirates.controller.WorldController;
import com.gorgo.pirates.model.Guybrush;
import com.gorgo.pirates.model.World;
import com.gorgo.pirates.view.ListViewCustomAdapter;

/**
 * @author Gorgo
 * 
 * Activity del gioco. Inizializza il Layout, la SurfaceView che contiene la Canvas, il GameLoop e il GameEngine.
 * 
 */

public class Pirates extends Activity implements SurfaceHolder.Callback {

	private GameThread gameThread; //GameLoop Thread
	private MainGamePanel surface; //SurfaceView per la Canvas
	private SurfaceHolder holder; //Abstract interface per gestire Destroy/Creation della View
	private ListView mlistView; //ListView contenente dialoghi e insulti
	private GameEngine gameEngine; //Controller inizializzatore
	private mHandler handler; //Handler per gestire eventi tra thread diversi
	
	private boolean lViewVisibility;
	
	private static final String TAG = Pirates.class.getSimpleName();
	
	//Costanti Handler
	private static final int 
			LVIEW_INVISIBLE = 0,
			LVIEW_VISIBLE = 1, 
			INIT_INSULTS = 2,
			LISTVIEW_UPDATE = 3,
			INIT_CONTROINSULTS = 4,
			INIT_GUYBRUSH = 5,
			INIT_GUYBRUSH_CARLA = 6,
			MUSIC_PAUSE = 7, 
			MUSIC_ON = 8;
	
	//Turni da Salvare
	private static final int
			INIT_SCABB_INTRO = -8,
			INIT_MAP = -3,
			OUTRO_FUOCO = 20;
	
	//Costanti Activity
	private static final int
		NO_PIRATE_SELECTED = 0,
		GAME_NOT_END = 0,
		GAME_END = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pirates);

		// Fullscreen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// Inizializzo SurfaceView
		surface = (MainGamePanel) findViewById(R.id.surface);
		holder = surface.getHolder();
		holder.addCallback(this);

		// Handler per gestire i messaggi tra SurfaceView, GameThread e Listview
		handler = new mHandler(this);

		// Setting ListView
		mlistView = (ListView) findViewById(R.id.mylist);
		mlistView.setVisibility(View.GONE);

		// GameEngine si occupa di inizializzare classi, renderer e update
		gameEngine = new GameEngine(this, surface, mlistView);
		
		
		//ListView listener
		//Salva la selezione dell'utente in Guybrush via Controller
		mlistView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				GuybrushController controllerG = gameEngine.getControllerG();

				String selectedFromList = (String) (mlistView.getItemAtPosition(position));
				//Log.d("setOnItemClickListener", "Hai Selezionato "+ selectedFromList + " in posizione nella lView " + position);
				controllerG.getText().setString(selectedFromList);
				controllerG.setSpeech(selectedFromList, position);
				
				controllerG.setInputWaiting(false); //L'utente ha scelto
				gameThread.notifyMessage(LVIEW_INVISIBLE); //lView off
			}
		});
	}
	
	
	//Metodi del SurfaceHolder
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//Avvio il gameLoop Thread
		gameThread.setRunning(true);
		gameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.d(TAG, "Surface is being destroyed");
		// Segnala al Thread che deve arrestarsi e attende
		// Shutdown pulito del Thread
		boolean retry = true;
		if (this.gameThread.isAlive()) {
			this.gameThread.setRunning(false);
			while (retry) {
				try {
					gameThread.join();
					retry = false;
				} catch (InterruptedException e) {
				}
			}
			this.gameThread.setRunning(false);
			Log.d(TAG, "Thread was shut down cleanly");
		}
	}
	
	
	// Metodi dell'Activity

	protected void onSaveInstanceState(Bundle state) {
		super.onSaveInstanceState(state);
		Log.d(TAG, "App in background. Salvataggio informazioni utili al ripristino...");

		handler.sendEmptyMessage(MUSIC_PAUSE); //Pause Music
		state.putSerializable("turnDialog", gameEngine.getTurnsInstance().getTurnDialog()); //Salvo turnDialog di Turns
		state.putSerializable("level", gameEngine.getWorldInstance().getLevel());
		state.putSerializable("vittorie", gameEngine.getControllerG().getVittorie());
		
		//Check Carla
		state.putSerializable("justSeenCarla", gameEngine.getTurnsInstance().getSeenCarla());
		state.putSerializable("carlaInit", gameEngine.getTurnsInstance().getCarlaInit());
		state.putSerializable("okCarla", gameEngine.getControllerW().getOkCarla());
		

		//Se sono già stati fatti duelli salvo gli insulti/controInsulti imparati
		if (gameEngine.getTurnsInstance().getTurnDialog() >= INIT_MAP) {

			//Per salvare lo SparseArray<String> nel Bundle salvo solo le Key in un int[]
			//Nella onRestore verrà ricostruito lo SparseArray<String>
			int insultKeyArray[] = gameEngine.getDialogsInstance().DisassembleInsultKeyArray();
			int controInsultKeyArray[] = gameEngine.getDialogsInstance().DisassembleControInsultKeyArray();
			state.putIntArray("guybrushInsult", insultKeyArray);
			state.putIntArray("guybrushControInsult", controInsultKeyArray);
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		Log.d(TAG, "App in foreground. Ripristino informazioni salvate..");
		
		int mTurnDialog = (Integer) savedInstanceState.getSerializable("turnDialog");
		int mLevel = (Integer) savedInstanceState.getSerializable("level");
		int mVittorie = (Integer) savedInstanceState.getSerializable("vittorie");
		
		//Check Carla
		int mJustSeen = (Integer) savedInstanceState.getSerializable("justSeenCarla");
		int mOkCarla = (Integer) savedInstanceState.getSerializable("okCarla");
		int mCarlaInit = (Integer) savedInstanceState.getSerializable("carlaInit");
		
		
		
		int insultKeyArray[] = savedInstanceState.getIntArray("guybrushInsult");
		int controInsultKeyArray[] = savedInstanceState.getIntArray("guybrushControInsult");
		
		Turns mTurns = gameEngine.getTurnsInstance();
		World mWorld = gameEngine.getWorldInstance();
		Dialogs mDialogs = gameEngine.getDialogsInstance();
		GuybrushController mControllerG = gameEngine.getControllerG();
		WorldController mControllerW = gameEngine.getControllerW();
		
		if (mTurnDialog < INIT_MAP){ // Intro
			mTurns.setTurn(INIT_SCABB_INTRO); // Restart Game
			if(mWorld.getPlayer() != null)
				mWorld.getPlayer().release();
		}
		
		else if (mTurnDialog == OUTRO_FUOCO)
			mTurns.setTurn(OUTRO_FUOCO); // Fine gioco
		
		else {
			mTurns.setTurn(INIT_MAP); // Mappa
			mTurns.setSeenCarla(mJustSeen);
			mTurns.setCarlaInit(mCarlaInit);
			mWorld.setPirate(NO_PIRATE_SELECTED);
			mWorld.setLevel(mLevel);
			mControllerG.setVittorie(mVittorie);
			mControllerW.setOkCarla(mOkCarla);
			

			// Ripristino insulti
			mDialogs.buildInsultArray(insultKeyArray, mLevel);
			mDialogs.buildControInsultArray(controInsultKeyArray, mLevel);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.runFinalizersOnExit(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		//Re-inizializzo il GameThread
		gameThread = new GameThread(holder, gameEngine, handler);
		gameThread.setRunning(true);
		
		//Resume Music
		handler.sendEmptyMessage(MUSIC_ON);
		
		//lView on se visibile prima dello standby
		if (lViewVisibility)
			handler.sendEmptyMessage(LVIEW_VISIBLE);
		
		World mWorld = gameEngine.getWorldInstance();
		
		//Se stai ripristinando un gioco concluso, ricarica dall'inizio
		if(mWorld.getEnd() == GAME_END){
			mWorld.setEnd(GAME_NOT_END);
			mWorld.getPlayer().release();
			gameEngine.getTurnsInstance().setTurn(INIT_SCABB_INTRO);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//Pause Music
		handler.sendEmptyMessage(MUSIC_PAUSE);
		
		//Controllo se la lView è visibile e salvo boolean
		if (gameEngine.getControllerG().getInputWaiting())
			lViewVisibility = true;
		else
			lViewVisibility = false;
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}

	
	// Handler per gestire visibilità della ListView
	static class mHandler extends Handler {
		private final WeakReference<Pirates> mTarget;
		private ListViewCustomAdapter adapter;

		mHandler(Pirates target) {
			mTarget = new WeakReference<Pirates>(target);
		}

		@Override
		public void handleMessage(Message msg) {
			Pirates target = mTarget.get();

			if (msg.what == INIT_INSULTS) {
				//Inserisco gli insulti nella ListView
				Guybrush guybrush = target.gameEngine.getGuybrush();
				adapter = new ListViewCustomAdapter(target, guybrush.getInsulti());
				target.mlistView.setAdapter(adapter);
			}
			
			if (msg.what == INIT_CONTROINSULTS) {
				//Inserisco i ControInsulti nella ListView
				Guybrush guybrush = target.gameEngine.getGuybrush();
				adapter = new ListViewCustomAdapter(target,
						guybrush.getControInsulti());
				target.mlistView.setAdapter(adapter);
			}

			if (msg.what == MUSIC_PAUSE) {
				//Metto in pausa la Musica
				World world = target.gameEngine.getWorldInstance();
				Log.d(TAG, "PAUSE MUSIC " + world.getPlayer());
				if (world.getPlayer() != null)
					world.getPlayer().pause();
			}

			if (msg.what == MUSIC_ON) {
				//Metto in start la Musica
				World world = target.gameEngine.getWorldInstance();
				Log.d(TAG, "MUSIC ON " + world.getPlayer());
				if (world.getPlayer() != null)
					world.getPlayer().start();
			}

			
			if (msg.what == INIT_GUYBRUSH) {
				//Inizializzo discorso Guybrush/Pirati listView
				Dialogs dialogs = target.gameEngine.getDialogsInstance();
				adapter = new ListViewCustomAdapter(target,
						dialogs.getGuybrushIntro());
				target.mlistView.setAdapter(adapter);
			}

			if (msg.what == INIT_GUYBRUSH_CARLA) {
				Dialogs dialogs = target.gameEngine.getDialogsInstance();
				adapter = new ListViewCustomAdapter(target,
						dialogs.getGuybrushStartCarla());
				target.mlistView.setAdapter(adapter);
			}

			if (msg.what == LISTVIEW_UPDATE)
				adapter.notifyDataSetChanged();

			if (msg.what == LVIEW_VISIBLE)
				target.mlistView.setVisibility(View.VISIBLE);
			
			if (msg.what == LVIEW_INVISIBLE)
				target.mlistView.setVisibility(View.GONE);
		}
	};

}