package com.gorgo.pirates.controller;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.gorgo.pirates.Dialogs;
import com.gorgo.pirates.MainGamePanel;
import com.gorgo.pirates.model.Enemy;
import com.gorgo.pirates.model.Guybrush;
import com.gorgo.pirates.model.World;
import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Controller di World (Pirati intro/outro, sfondo, testi). Contiene metodi set/get per gestire tutto ciò che non è Guybrush o Enemy
 * 
 */

public class WorldController implements Controller{
	
	private static final int 
		GUYBRUSH = 1,
		PIRATA_SX = 2,
		PIRATA_DX = 3;

	private World world;
	private MainGamePanel surface; 
	private SpriteTile[] spriteArray;
	

	public WorldController(World world, MainGamePanel surface) {
		this.world = world;
		this.surface = surface;
		spriteArray = new SpriteTile[5]; // Array contenente le Sprite dei Pirati Intro/Outro
	}

	public void update() {
		if (world.isSpeaking(GUYBRUSH)) {
			world.setMoving(GUYBRUSH, true);
			world.setSpeaking(GUYBRUSH, true);
		}
		if (world.isSpeaking(PIRATA_SX)) {
			world.setMoving(PIRATA_SX, true);
			world.setSpeaking(PIRATA_SX, true);
		}
		if (world.isSpeaking(PIRATA_DX)) {
			world.setMoving(PIRATA_DX, true);
			world.setSpeaking(PIRATA_DX, true);
		}
	}

	//Il pirata 'iesimo' inizia a parlare
	public void startSpeaking(int i, String speech) {
		world.setSpeaking(i, true);
		world.setSpeech(i, speech);
		world.getText(i).setString(speech);
		world.setMoving(i, true);
		getSprite(i).setCanLoop();
	}
	
	//Il pirata 'i' smette di parlare
	public void stopSpeaking(int i) {
		world.setMoving(i, false);
		getSprite(i).setAnimationIdle();
	}
	
	//Ritorna la Sprite del pirata 'iesimo'
	public SpriteTile getSprite(int i) {
		return spriteArray[i];
	}

	//Imposta una Sprite al pirata 'iesimo'
	public void setSprite(SpriteTile sprite, int i) {
		this.spriteArray[i] = sprite;
	}
	
	public void initMap(int i){
		this.surface.initMap(i);
	}
	public void initLevel(){
		this.surface.InitLevels();
	}
	public void initSkip(){
		this.surface.initSkip();
	}	
	
	//Setter base
	//Richiamano i metodi del Model

	//Imposta il Pirata da visualizzare
	public void setPirate(int pirate) {
		world.setPirate(pirate);	
	}
	
	//Imposta il livello di gioco della Partita
	public void setLevel(int level) {
		world.setLevel(level);		
	}
	
	//Imposta l'eventuale skipIntro dell'utente
	public void setSkipped(int introSkipped) {
		world.setSkipped(introSkipped);	
	}
	
	// Setta il who-esimo Pirata che sta parlando
	public void setSpeaking(int who, boolean bool) {
		world.setSpeaking(who, bool);
	}
	
	// Imposta in che fase dell'intro siamo
	public void setIntro(int i) {
		world.setIntro(i);		
	}
	
	// Imposto il lettore Musicale
	public void setPlayer(MediaPlayer mPlayer) {
		world.setPlayer(mPlayer);	
	}
	
	// Imposto lo sfondo corrente
	public void setSfondo(Bitmap background) {
		world.setSfondo(background);	
	}
	
	// Imposto l'istanza di Text sul pirata i
	public void setText(Text istanceText, int i) {
		world.setText(istanceText, i);
	}
	
	// Imposta a 1 se giocatore pronto per Carla
	public void setOkCarla(int i) {
		world.setOkCarla(i);
	}	
	
	// Imposta a 1 se il gioco è finito 
	public void setEnd(int i) {
		world.setEnd(i);	
	}
	
	// Imposta al who-esimo pirata la stringa speech da dire
	public void setSpeech(int who, String speech) {
		world.setSpeech(who, speech);	
	}
	
	//Getter base
	//Richiamano i metodi del Model

	public int getPirate() {
		return world.getPirate();
	}
	public int getLevel() {
		return world.getLevel();
	}
	public int getSkipped() {
		return world.getSkipped();
	}
	public boolean getSpeaking(int i) {
		return world.isSpeaking(i);
	}
	public Bitmap getSfondo() {
		return world.getSfondo();
	}
	public int getIntro() {
		return world.getIntro();
	}
	public Text getText(int i) {
		return world.getText(i);
	}
	public String getSpeech(int i) {
		return world.getSpeech(i);
	}
	public String getAvgFps() {
		return world.getAvgFps();
	}
	public MediaPlayer getPlayer() {
		return world.getPlayer();
	}
	
	// Getter istanze
	public Guybrush getGuybrush() {
		return world.getGuybrush();
	}
	public Enemy getEnemy() {
		return world.getEnemy();
	}
	public Dialogs getDialogs() {
		return world.getDialogs();
	}
	public int getOkCarla() {
		return world.getOkCarla();
	}

}
