package com.gorgo.pirates.model;

import android.util.SparseArray;

import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Classe che definisce lo sfidante (Pirata o Carla)
 * 
 */

public class Enemy {

	private final int NO_DRAW = 1;
	
	// Tipo
	private int type; // 1 Biondo, 2 Cappello, 3 Bandana, 4 Pelato, 5 Carla

	// Animazioni
	private String currentAnimation;
	private SpriteTile sprite;
	private int lock = NO_DRAW;

	// Azioni
	private boolean isSpeaking = false;
	private boolean isMoving = false;
	private boolean idle = true; //In attesa di parlare

	// Testo
	private Text textEnemy;
	private String speech;
	private int color;

	// Punteggio
	private int points; //Punteggio round
	
	//Insulti
	private SparseArray<String> insulti;
	private int answerKey; // Chiave della risposta da dare a Guybrush
	private int insultKey; // Chiave dell'insulto da rispondere a Guybrush
	
	
	// SETTER
	
	public void setPoints(int points) {
		this.points = points;
	}
	public void setAnimation(String string) {
		this.currentAnimation = string;
	}
	public void setSprite(SpriteTile sprite) {
		this.sprite = sprite;
	}
	public void setSpeech(String speech) {
		this.speech = speech;
	}
	public void setIdle(boolean idle) {
		this.idle = idle;
	}
	public void setMoving(boolean moving) {
		this.isMoving = moving;
	}
	public void setSpeaking(boolean speaking) {
		this.isSpeaking = speaking;
	}
	public void setInsulti(SparseArray<String> insulti_temp) {
		this.insulti = insulti_temp;
	}
	public void setInsultKey(int insulto_scelto) {
		this.insultKey = insulto_scelto;
	}	
	public void setType(int type) {
		this.type = type;
	}
	public void setLock(int lock) {
		this.lock = lock;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public void setText(Text textEnemy2) {
		this.textEnemy = textEnemy2;
	}
	public void setAnswerKey(int answerKey) {
		this.answerKey = answerKey;
	}
	
	
	// GETTER
	
	public String getAnimation() {
		return currentAnimation;
	}
	public String getSpeech() {
		return this.speech;
	}
	public SpriteTile getSprite() {
		return this.sprite;
	}
	public boolean getIdle() {
		return this.idle;
	}
	public boolean isMoving() {
		return this.isMoving;
	}
	public boolean isSpeaking() {
		return this.isSpeaking;
	}
	public SparseArray<String> getInsulti() {
		return this.insulti;
	}
	public int getInsultKey() {
		return this.insultKey;
	}
	public int getType() {
		return this.type;
	}
	public int getLock() {
		return this.lock;
	}
	public int getColor() {
		return this.color;
	}
	public Text getText() {
		return this.textEnemy;
	}
	public int getAnswerKey() {
		return this.answerKey;
	}
	public int getPoints() {
		return this.points;
	}
}
