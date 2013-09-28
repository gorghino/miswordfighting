package com.gorgo.pirates.model;

import android.graphics.Bitmap;
import android.media.MediaPlayer;

import com.gorgo.pirates.Dialogs;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Classe che definisce tutto ciò che non è Guybrush o Enemy
 * 
 */

public class World {
	
	private static final int 
	NO_PIRATE_SELECTED = -1,
	NO_SKIP_SELECTED = -1;
	
	// Personaggi
	private Guybrush guybrush; // ThreepWood, temibile pirata!
	private Enemy enemy;
	private int pirate = NO_PIRATE_SELECTED; //-1 Init, 0 Mappa, 1 Biondo, 2 Cappello, 3 Bandana, 4 Pelato, 5 Carla
	
	// Testi
	private Dialogs dialogs;
	private Text[] textArray; // Array dei testi dei pirati durante Intro/Outro
	private String[] speech; // Testi dei pirati durante Intro/Outro
	private boolean[] isSpeaking; // True se i-esimo pirata sta parlando
	
	// Animazioni
	private boolean[] isMoving; // True se i-esimo pirata si sta muovendo
	private String avgFps; // FPS del gioco
	private Bitmap sfondo; // Sfondo da visualizzare
	
	// Modalità
	private int intro; // 1: Scabb Intro, 2: Fuoco pirati, 3: Outro, 0: Else
	private int level; // 1: Facile, 2: Medio, 3: Difficile
	private int skipped = NO_SKIP_SELECTED; // 1: Intro skipped, 0: Stop Intro, -1: Da skippare
	private int end; // 1 se il gioco è finito, 0 else
	private int okCarla; // 1 se Carla disponibile, 0 else
	
	// Musica
	private MediaPlayer mPlayer;


	public World(Guybrush guybrush, Enemy enemy, Dialogs dialogs) {
		this.guybrush = guybrush;
		this.enemy = enemy;
		this.dialogs = dialogs;
		textArray = new Text[5];
		speech = new String[5];
		isSpeaking = new boolean[5];
		isMoving = new boolean[5];
	}

	// SETTER

	public void setAvgFps(String avgFps) {
		this.avgFps = avgFps;
	}	
	public void setPirate(int pirate) {
		this.pirate = pirate;
		enemy.setType(pirate);
	}
	public void setSfondo(Bitmap sfondo) {
		this.sfondo = sfondo;
	}
	public void setIntro(int i) {
		this.intro = i;
	}	
	public void setText(Text scabbTextIntro, int i) {
		this.textArray[i] = scabbTextIntro;
	}
	public void setSpeaking(int i, boolean speaking) {
		this.isSpeaking[i] = speaking;	
	}	
	public void setSpeech(int i, String speech) {
		this.speech[i] = speech;	
	}
	public void setMoving(int i, boolean moving) {
		this.isMoving[i] = moving;		
	}
	public void setLevel(int i) {
		this.level = i;
	}
	public void setSkipped(int i) {
		this.skipped  = i;
	}
	public void setPlayer(MediaPlayer mPlayer){
		this.mPlayer = mPlayer;
	}
	public void setEnd(int i) {
		this.end = i;
	}
	public void setOkCarla(int i) {
		this.okCarla = i;
	}	
	
	// GETTER
	
	public String getAvgFps() {
		return this.avgFps;
	}
	public Enemy getEnemy() {
		return this.enemy;
	}
	public Guybrush getGuybrush() {
		return guybrush;
	}
	public Dialogs getDialogs() {
		return this.dialogs;
	}
	public int getPirate() {
		return this.pirate;
	}
	public Bitmap getSfondo() {
		return this.sfondo;
	}
	public int getIntro() {
		return this.intro;
	}	
	public int getOkCarla(){
		return this.okCarla;
	}
	public int getEnd(){
		return this.end;
	}
	public int getSkipped() {
		return this.skipped;
	}
	public MediaPlayer getPlayer(){
		return this.mPlayer;
	}
	public boolean isMoving(int i) {
		return this.isMoving(i);
	}
	public int getLevel(){
		return this.level;
	}
	public boolean isSpeaking(int i){
		return this.isSpeaking[i];
	}
	public String getSpeech(int i) {
		return speech[i];
	}
	public Text getText(int i) {
		return this.textArray[i];
	}	
}
