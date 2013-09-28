package com.gorgo.pirates.model;

import android.util.SparseArray;

import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Classe che definisce Guybrush
 * 
 */

public class Guybrush {
	
	
	private final int NO_DRAW = 1;
	
	// Animazioni
	private String currentAnimation; // Animazione corrente di Guybrush
	private SpriteTile sprite;
	private int lock = NO_DRAW;
	
	// Azioni
	private boolean isSpeaking = false; // Se vero, Guybrush sta al momento parlando = Scritta+Animazione
	private boolean isMoving = false;
	private boolean idle = true; // Se vero Guybrush aspetta che il pirata abbia finito di parlare
	
	// Punteggio
	int points; // Duello
	private int vittorie; // Partita

	// Testo
	private Text textGuybrush;
	private String speech; // Contiene la frase da stampare a video

	// Input User
	public boolean inputWaiting = false; // Se vero, è visibile la scelta dei dialoghi e si aspetta l'input utente
	private int speechIndex; // Indice della frase selezionata
	
	// Insulti
	private SparseArray<String> insulti, controInsulti;

	// SETTER
	
	public void setAnimation(String animation) {
		this.currentAnimation = animation;
	}	
	public void setSprite(SpriteTile sprite) {
		this.sprite = sprite;
	}
	public void setSpeech(String speech, int position) {
		//Metodo automatico da ListView
		this.speech = speech;
		this.speechIndex = position;
	}
	public void setSpeech(String speech) {
		//Metodo forced dal Controller
		this.speech = speech;
	}
	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	public void setInputWaiting(boolean inputWaiting) {
		this.inputWaiting = inputWaiting;
	}
	public void setPoints(int points) {
		this.points = points;
	}
	public void setMoving(boolean moving) {
		this.isMoving = moving;		
	}
	public void setSpeaking(boolean speaking) {
		this.isSpeaking = speaking;
	}	
	public boolean isSpeaking(){
		return isSpeaking;
	}
	public void setInsulti(SparseArray<String> insulti_temp) {
		this.insulti = insulti_temp;
	}	
	public void setControInsulti(SparseArray<String> controInsulti_temp) {
		this.controInsulti = controInsulti_temp;
	}	
	public void setLock(int lock){
		this.lock = lock;
	}
	public void setText(Text textGuybrush) {
		this.textGuybrush = textGuybrush;		
	}
	public void setVittorie(int vittorie) {
		this.vittorie = vittorie;
	}
	
	// GETTER

	public boolean getIdle() {
		return this.idle;
	}
	public boolean getInputWaiting() {
		return this.inputWaiting;
	}
	public int getSpeechIndex() {
		return this.speechIndex;
	}
	public int getPoints(){
		return this.points;
	}
	public boolean isMoving() {
		return this.isMoving;	
	}
	public SparseArray<String> getInsulti() {
		return this.insulti;
	}
	public SparseArray<String> getControInsulti() {
		return this.controInsulti;
	}
	public int getLock() {
		return this.lock ;
	}
	public Text getText(){
		return this.textGuybrush;
	}
	public int getVittorie() {
		return this.vittorie;
	}
	public String getAnimation() {
		return this.currentAnimation;
	}
	public String getSpeech() {
		return this.speech;
	}
	public SpriteTile getSprite() {
		return sprite;
	}
}
