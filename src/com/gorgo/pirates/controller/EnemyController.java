package com.gorgo.pirates.controller;

import android.util.SparseArray;

import com.gorgo.pirates.model.Enemy;
import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Controller dei Pirati/Carla. Contiene metodi get/set per modificare valori del Model Enemy
 * 
 */

public class EnemyController implements Controller{

	private Enemy enemy;

	public EnemyController(Enemy enemy) {
		this.enemy = enemy;
	}

	public void update() {
		if (enemy.isSpeaking()) {
			enemy.setMoving(true);
			enemy.setSpeaking(true);
		}
		if (enemy.isMoving()) {
			enemy.setMoving(true);
		}		
	}

	//Enemy inizia a parlare
	public void startSpeaking(String speech) {
		enemy.setMoving(true);	
		enemy.setSpeaking(true);
		enemy.setSpeech(speech);	
		enemy.getSprite().setCanLoop();
	}
	
	//Enemy smette di parlare
	public void stopSpeaking(){
		enemy.setMoving(false);
		enemy.getSprite().setAnimationIdle();
	}
	
	//Enemy si muove
	public void startMoving(){
		enemy.setIdle(false);
		enemy.setMoving(true);
	}
	
	//Imposta l'enemy idle: Animazione caricata ma rimane fermo
	public void setIdle(){
		enemy.setIdle(true);
		enemy.setMoving(false);
		enemy.getSprite().setAnimationIdle();
	}
	
	//Aggiorna l'animazione da visualizzare, per default No Loop
	public void setAnimation(String animation){
		enemy.setAnimation(animation);
		enemy.getSprite().setCurrentAnimation(enemy.getAnimation(), false);	
	}	
	
	//Setter base
	//Richiamano i metodi del Model
	
	//Imposta SpriteSheet corrente
	public void setSprite(SpriteTile spriteTile){
		enemy.setSprite(spriteTile);
	}
	
	//Imposta punteggio del round dell'enemy
	public void setPoints(int i) {
		enemy.setPoints(i);
	}
	
	//Imposta il SparseArray contenente gli insulti del pirata
	public void setInsults(SparseArray<String> insulti) {
		enemy.setInsulti(insulti);	
	}
	
	//Imposta il Lock per il Render Video
	//Se Lock == 1 Pirata visibile
	public void setLock(int i) {
		enemy.setLock(i);		
	}
	
	//Imposta il colore del testo
	public void setColor(int color) {
		enemy.setColor(color);			
	}
	
	//Imposta la chiave della risposta da dare a Guybrush
	public void setAnswerKey(int answerKey) {
		enemy.setAnswerKey(answerKey);
	}
	
	// Associa la Text all'enemy
	public void setText(Text textEnemy) {
		enemy.setText(textEnemy);	
	}	
	
	// Imposta se il pirata sta parlando
	public void setSpeaking(boolean bool) {
		enemy.setSpeaking(bool);	
	}
	
	// Imposta se il pirata si sta muovendo
	public void setMoving(boolean bool) {
		enemy.setMoving(bool);	
	}
	
	// Imposta la chiave dell'insulto da proporre
	public void setInsultKey(int keyAt) {
		enemy.setInsultKey(keyAt);
	}
	
	// Imposta il tipo di pirata scelto
	public void setType(int i) {
		enemy.setType(i);
	}
	
	//Getter base
	//Richiamano i metodi del Model
	
	public Text getText(){
		return enemy.getText();
	}	
	public boolean getSpeaking() {
		return enemy.isSpeaking();
	}
	public int getPoints() {
		return enemy.getPoints();
	}
	public SparseArray<String> getInsults() {
		return enemy.getInsulti();	
	}
	public boolean isMoving(){
		return enemy.isMoving();
	}	
	public int getAnswerKey() {
		return enemy.getAnswerKey();
	}
	public SpriteTile getSprite() {
		return enemy.getSprite();
	}
	public String getAnimation() {
		return enemy.getAnimation();
	}
	public int getLock() {	
		return enemy.getLock();
	}
	public String getSpeech() {
		return enemy.getSpeech();
	}
	public int getColor() {
		return enemy.getColor();
	}

}