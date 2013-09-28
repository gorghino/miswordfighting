package com.gorgo.pirates.controller;

import android.util.SparseArray;

import com.gorgo.pirates.model.Guybrush;
import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * Controller di Giuybrush. Contiene metodi get/set per modificare valori del Model Guybrush
 * 
 */

public class GuybrushController implements Controller{
	
	private static final int 
		LEN_LIVELLO_FACILE = 16,
		LEN_LIVELLO_MEDIO = 16,
		LEN_LIVELLO_DIFFICILE = 33;
	
	private Guybrush guybrush;

	public GuybrushController(Guybrush guybrush) {
		this.guybrush = guybrush;
	}

	public void update() {
		if (guybrush.isSpeaking()) {
			guybrush.setMoving(true);
			guybrush.setSpeaking(true);
		}
		if (guybrush.isMoving()) {
			guybrush.setMoving(true);
		}
	}

	//Guybrush inizia a parlare
	public void startSpeaking() {
		// Metodo automatico con scelta dalla ListView
		guybrush.setMoving(true);
		guybrush.setIdle(false);
		guybrush.setSpeaking(true);
		guybrush.getSprite().setCanLoop();
	}

	public void startSpeaking(String speech) {
		// Metodo forced dal controller
		guybrush.setMoving(true);
		guybrush.setSpeaking(true);
		guybrush.getText().setString(speech);
		guybrush.setSpeech(speech);
		guybrush.getSprite().setCanLoop();
	}

	//Guybrush smette di parlare
	public void stopSpeaking() {
		guybrush.setMoving(false);
		guybrush.getSprite().setAnimationIdle();
	}
	
	//Guybrush si muove
	public void startMoving() {
		guybrush.setIdle(false);
		guybrush.setMoving(true);
	}
	
	//Imposta Guybrush idle: Animazione caricata ma rimane fermo
	public void setIdle() {
		guybrush.setIdle(true);
		guybrush.setMoving(false);
		guybrush.getSprite().setAnimationIdle();
	}
	
	//Aggiorna l'animazione da visualizzare, per default No Loop
	public void setAnimation(String animation) {
		guybrush.setAnimation(animation);
		guybrush.getSprite()
				.setCurrentAnimation(guybrush.getAnimation(), false);
	}

	//Ritorna la percentuale di insulti imparati da Guybrush
	public double getXpPerc(int level, SparseArray<String> insultiCurrentGuybrush) {
		int lenInsulti;
		switch (level) {
		case 0:
			lenInsulti = LEN_LIVELLO_FACILE;
		case 1:
			lenInsulti = LEN_LIVELLO_MEDIO;
		case 2:
			lenInsulti = LEN_LIVELLO_DIFFICILE;
		default:
			lenInsulti = -1;
		}

		// Conosciuti : Totale = x : 100
		float perc = insultiCurrentGuybrush.size() - 3 / lenInsulti;
		return perc;
	}

	//Setter base
	//Richiamano i metodi del Model
	
	//Imposta SpriteSheet corrente
	public void setSprite(SpriteTile spriteTile) {
		guybrush.setSprite(spriteTile);
	}
	
	//Imposta se Guybrush sta aspettando input utente
	public void setInputWaiting(boolean input) {
		guybrush.setInputWaiting(input);
	}
	
	//Imposta punteggio del round di Guybrush
	public void setPoints(int i) {
		guybrush.setPoints(i);
	}
	
	//Imposta il SparseArray contenente gli insulti di Guybrush
	public void setInsults(SparseArray<String> insulti) {
		guybrush.setInsulti(insulti);
	}
	
	//Imposta il SparseArray contenente i controInsulti di Guybrush
	public void setControInsulti(SparseArray<String> controInsulti_temp) {
		guybrush.setControInsulti(controInsulti_temp);
	}
	
	//Imposta il Lock per il Render Video
	//Se Lock == 1 Pirata visibile
	public void setLock(int i) {
		guybrush.setLock(i);
	}
	
	//Imposta il numero di Vittorie di Guybrush contro i pirati
	public void setVittorie(int i) {
		guybrush.setVittorie(i);
	}
	
	//Imposta frase e posizione dell'input selezionato dall'utente
	public void setSpeech(String selectedFromList, int position) {
		guybrush.setSpeech(selectedFromList, position);
	}
	
	// Associa un'istanza Text al model Guybrush
	public void setText(Text textGuy) {
		guybrush.setText(textGuy);	
	}
	
	// Imposta se Guybrush sta parlando
	public void setSpeaking(boolean bool) {
		guybrush.setSpeaking(bool);
	}
	
	// Imposta se Guybrush si sta muovendo
	public void setMoving(boolean bool) {
		guybrush.setMoving(bool);
	}
	
	//Getter base
	//Richiamano i metodi del Model

	public Text getText(){
		return guybrush.getText();
	}
	public boolean getInputWaiting() {
		return guybrush.getInputWaiting();
	}
	public boolean isMoving() {
		return guybrush.isMoving();
	}
	public SparseArray<String> getInsults() {
		return guybrush.getInsulti();
	}
	public int getPoints() {
		return guybrush.getPoints();
	}
	public SparseArray<String> getControInsulti() {
		return guybrush.getControInsulti();
	}
	public int getVittorie() {
		return guybrush.getVittorie();
	}
	public boolean getSpeaking() {
		return guybrush.isSpeaking();
	}
	public SpriteTile getSprite() {
		return guybrush.getSprite();
	}
	public String getAnimation() {
		return guybrush.getAnimation();
	}
	public int getLock() {
		return guybrush.getLock();
	}
	public String getSpeech() {
		return guybrush.getSpeech();
	}
	public int getSpeechIndex() {
		return guybrush.getSpeechIndex();
	}
}