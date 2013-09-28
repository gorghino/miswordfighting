package com.gorgo.pirates;

import java.util.ArrayList;


import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;

/**
 * @author Gorgo
 * 
 * Classe ha il compito di inizializzare tutte le scritte che compaiono durante il gioco, dagli insulti/ControInsulti ai dialoghi
 * 
 */

public class Dialogs {
	
	//Livelli
		private static final int
			FACILE = 1,
			MEDIO = 2,
			DIFFICILE = 3;
	
	private Context context;
	
	// Dialoghi Intro (2 pirati e Guybrush fuoco)
	private String[] introGuybrushFuoco, introPirate1Fuoco, introPirate2Fuoco;
	
	// Dialoghi Guybrush/Pirata
	private String[] introPirate, guybrush_start, pirate_start;
	
	// Dialoghi Guybrush/Carla
	private String[] guybrush_answer_carla, guybrush_start_carla,
				carla_start_0, carla_start_1, carla_answer, carla_lose;

	// TUTTI gli insulti e ControInsulti
	private String[] insulti, controInsulti;
	
	// TUTTI gli insulti 'speciali' di Carla
	private String[] insulti_carla;
	
	// SparseArray che contengono gli insulti/controInsulti SAPUTI del round (current) e dell'intera Partita
	// Ogni insulto/controInsulto ha una KEY che viene valutata per identificare la risposta corretta/sbagliata
	private SparseArray<String> guybrush_insults_known,
			guybrush_controInsults_know, guybrush_insults_current,
			guybrush_controInsults__current;
	
	// Insulti e controInsulti per arrendersi, ripetere o perdere il round
	private String[] insulti_plus;
	private String[] controInsulti_plus;

	public Dialogs(Context context) {
		this.context = context;
		Resources res = context.getResources();

		// Carico Dialogo intro Guybrush pirati fuoco
		introGuybrushFuoco = res.getStringArray(R.array.guybrush_intro);
		introPirate1Fuoco = res.getStringArray(R.array.pirate1_intro);
		introPirate2Fuoco = res.getStringArray(R.array.pirate2_intro);

		// Carico dialoghi iniziali Guybrush/Pirati
		introPirate = res.getStringArray(R.array.pirate_intro);
		guybrush_start = res.getStringArray(R.array.guybrush_start_pirate);
		pirate_start = res.getStringArray(R.array.pirate_start);

		// Carico dialoghi iniziali Guybrush/Carla
		guybrush_answer_carla = res
				.getStringArray(R.array.guybrush_answer_carla);
		guybrush_start_carla = res.getStringArray(R.array.guybrush_start_carla);
		carla_start_0 = res.getStringArray(R.array.carla_start_0);
		carla_start_1 = res.getStringArray(R.array.carla_start_1);
		carla_answer = res.getStringArray(R.array.carla_answer);
		carla_lose = res.getStringArray(R.array.carla_lose);

		// SparseArray con insulti/controInsulti SAPUTI da Guybrush
		guybrush_insults_known = new SparseArray<String>();
		guybrush_controInsults_know = new SparseArray<String>();

		// Insulti e controInsulti per arrendersi, ripetere o perdere il round
		insulti_plus = res.getStringArray(R.array.insulti_plus);
		controInsulti_plus = res.getStringArray(R.array.ControInsulti_plus);
	}

	// Impara insulto con chiave i, viene inserito nello SparseArray
	public void imparaInsulto(int i, SparseArray<String> current) {
		current.put(i, insulti[i]);
		guybrush_insults_known.put(i, insulti[i]);
	}
	
	// Impara controInsulto con chiave i, viene inserito nello SparseArray
	public void imparaControInsulto(int i, SparseArray<String> current) {
		current.put(i, controInsulti[i]);
		guybrush_controInsults_know.put(i, controInsulti[i]);
	}

	// Ogni ROUND viene ripristinato lo SparseArray contenente gli insulti saputi
	// Durante il round questo SparseArray viene modificato per non far ripetere gli stessi insulti,
	// o vengono aggiunti gli insulti imparati
	public SparseArray<String> initInsulti() {
		guybrush_insults_current = new SparseArray<String>();
		
		for (int i = 0; i < guybrush_insults_known.size(); i++)
			guybrush_insults_current.put(guybrush_insults_known.keyAt(i),
					guybrush_insults_known.valueAt(i));
		
		return guybrush_insults_current;
	}
	
	// Ogni ROUND viene ripristinato lo SparseArray contenente i controInsulti saputi
	// Durante il round questo SparseArray viene modificato; vengono aggiunti i controInsulti imparati
	public SparseArray<String> initControInsulti() {
		guybrush_controInsults__current = new SparseArray<String>();
		
		for (int i = 0; i < guybrush_controInsults_know.size(); i++)
			guybrush_controInsults__current.put(
					guybrush_controInsults_know.keyAt(i),
					guybrush_controInsults_know.valueAt(i));
		
		return guybrush_controInsults__current;
	}
	
	// Metodo che inizializza lo SparseArray degli insulti di Carla
	public SparseArray<String> initInsultBoss() {
		SparseArray<String> insulti_temp = new SparseArray<String>();
		
		for (int i = 0; i < insulti_carla.length; i++) {
			insulti_temp.put(i, insulti_carla[i]);
		}
		return insulti_temp;
	}

	// Viene rimosso l'insulto con chiave i, perchè viene detto da Guybrush o dal Pirata
	public void rimuoviInsulto(int i, SparseArray<String> current) {
		current.remove(current.keyAt(i));
	}

	// Verifica se un insulto sentito è già conosciuto
	public boolean conosceInsulto(int i, SparseArray<String> current) {
		if (current.get(i) != null)
			return true;
		else
			return false;
	}

	// Verifica se un controInsulto sentito è già conosciuto
	public boolean conosceControInsulto(int i, SparseArray<String> current) {
		if (current.get(i) != null)
			return true;
		else
			return false;
	}

	// Metodo che carica lo SparseArray degli insulti del nemico
	public SparseArray<String> loadInsultEnemy() {
		SparseArray<String> insulti_temp = new SparseArray<String>();
		
		for (int i = 0; i < insulti.length; i++) {
			insulti_temp.put(i, insulti[i]);
		}
		return insulti_temp;
	}
	
	// Metodo che carica gli insulti di Carla in base al LIVELLO della partita
	public void loadInsultBoss(int level) {
		Resources res = context.getResources();

		if (level == FACILE)
			insulti_carla = res.getStringArray(R.array.insultiCarla);
		else if (level == MEDIO)
			insulti_carla = res.getStringArray(R.array.insultiCarla_mi3);
		else if (level == DIFFICILE) {
			// Vengono caricati entrambi i livelli (facile+medio)
			String insulti_carla1[] = res.getStringArray(R.array.insultiCarla);
			String insulti_carla3[] = res
					.getStringArray(R.array.insultiCarla_mi3);

			List<String> list = new ArrayList<String>(
					Arrays.<String> asList(insulti_carla1));
			list.addAll(Arrays.<String> asList(insulti_carla3));

			insulti_carla = list.toArray(new String[list.size()]);
		}
	}
	
	// Metodo che carica gli insulti in base al LIVELLO della partita
	public void loadInsulti(int level) {
		Resources res = context.getResources();

		if (level == FACILE)
			insulti = res.getStringArray(R.array.insulti);
		else if (level == MEDIO)
			insulti = res.getStringArray(R.array.insulti_mi3);
		else if (level == DIFFICILE) {
			String insulti_1[] = res.getStringArray(R.array.insulti);
			String insulti_3[] = res.getStringArray(R.array.insulti_mi3);

			List<String> list = new ArrayList<String>(
					Arrays.<String> asList(insulti_1));
			list.addAll(Arrays.<String> asList(insulti_3));

			insulti = list.toArray(new String[list.size()]);
		}
	}
	
	// Metodo che carica gli insulti di Guybrush a inizio PARTITA
	public void loadInitGuybrushInsulti(int level) {
		
		loadInsulti(level);

		// Aggiungo insulti Iniziali
		//DEBUG MODE: Cambiare 2 con insulti.length per avere già tutti gli insulti
		for (int i = 0; i < 2; i++) {
			guybrush_insults_known.put(i, insulti[i]);
			guybrush_controInsults_know.put(i, controInsulti[i]);
		}
		// Aggiungo finti insulti e addio
		for (int i = 0; i < 3; i++)
			guybrush_insults_known.put(i + 33, insulti_plus[i]);
	}
	
	// Metodo che carica i controInsulti in base al LIVELLO della partita
	public void loadControInsult(int level){
		Resources res = context.getResources();

		if (level == FACILE)
			controInsulti = res.getStringArray(R.array.controInsulti);
		else if (level == MEDIO)
			controInsulti = res.getStringArray(R.array.controInsulti_mi3);
		else if (level == DIFFICILE) {
			String controInsulti_1[] = res
					.getStringArray(R.array.controInsulti);
			String controInsulti_3[] = res
					.getStringArray(R.array.controInsulti_mi3);

			List<String> list = new ArrayList<String>(
					Arrays.<String> asList(controInsulti_1));
			list.addAll(Arrays.<String> asList(controInsulti_3));

			controInsulti = list.toArray(new String[list.size()]);
		}
	}

	public void loadInitGuybrushControInsulti(int level) {
		
		loadControInsult(level);
		
		// Aggiungo controInsulti perdenti e ripetere
		for (int i = 0; i < 5; i++)
			guybrush_controInsults_know.put(i + 33, controInsulti_plus[i]);
	}
	
	
	// Utils
	
	// Riempie current con i chiave/valori di total
	public SparseArray<String> fill(SparseArray<String> current,
			SparseArray<String> total) {

		if (current.size() != 0)
			current.clear();
		for (int i = 0; i < total.size(); i++)
			current.put(total.keyAt(i), total.valueAt(i));
		return current;

	}

	// Getter insulti/Controinsulti
	public SparseArray<String> getControInsultKnow() {
		return this.guybrush_controInsults_know;
	}
	public SparseArray<String> getInsultKnow() {
		return this.guybrush_insults_known;
	}
	
	// Metodi getter dei pirati.
	// Viene invocato per cercare l'array giusto in caso di risposta sbagliata
	public String getInsulto(int i) {
		if (i >= 33)
			return insulti_plus[i - 33];
		else
			return insulti[i];
	}

	public String getControInsulto(int i) {
		if (i >= 33)
			return controInsulti_plus[i - 33];
		else
			return controInsulti[i];
	}
	
	
	// STORE / RESUME
	
	// Metodo che scompone lo SparseArray degli insulti SAPUTI e ritorna solo int[] delle chiavi
	// Metodo chiamato in onSaveInstanceState()
	public int[] DisassembleInsultKeyArray() {
		int insultKeyArray[] = new int[guybrush_insults_known.size()];
		for (int i = 0; i < guybrush_insults_known.size()-3; i++)
			insultKeyArray[i] = guybrush_insults_known.keyAt(i);
		return insultKeyArray;
	}

	// Metodo che scompone lo SparseArray dei controInsulti SAPUTI e ritorna solo int[] delle chiavi
	// Metodo chiamato in onSaveInstanceState()
	public int[] DisassembleControInsultKeyArray() {
		int controInsultKeyArray[] = new int[guybrush_controInsults_know.size()];
		for (int i = 0; i < guybrush_controInsults_know.size()-5; i++)
			controInsultKeyArray[i] = guybrush_controInsults_know.keyAt(i);
		return controInsultKeyArray;
	}

	// Metodo ricostruisce lo SparseArray degli insulti SAPUTI
	// Metodo chiamato in onRestoreInstanceState()
	public void buildInsultArray(int insultKeyArray[], int level){	
		loadInsulti(level);
				
		for(int i=0;i<insultKeyArray.length;i++)
			guybrush_insults_known.put(insultKeyArray[i], insulti[insultKeyArray[i]]);
		
		// Aggiungo finti insulti e addio
				for (int i = 0; i < 3; i++)
					guybrush_insults_known.put(i + 33, insulti_plus[i]);
	}
	
	// Metodo ricostruisce lo SparseArray dei controInsulti SAPUTI
	// Metodo chiamato in onRestoreInstanceState()
	public void buildControInsultArray(int controInsultKeyArray[], int level){	
		loadControInsult(level);
				
		for(int i=0;i<controInsultKeyArray.length;i++)
			guybrush_controInsults_know.put(controInsultKeyArray[i], controInsulti[controInsultKeyArray[i]]);
		
		for (int i = 0; i < 5; i++)
			guybrush_controInsults_know.put(i + 33, controInsulti_plus[i]);
	}
	
	// Getter	
	
	public String getIntroPirate() {
		Random ran = new Random();
		return introPirate[ran.nextInt(5)];
	}

	public String getIntroGuybrushCarla(int i) {
		return guybrush_answer_carla[i];
	}

	public String[] getGuybrushStartCarla() {
		return guybrush_start_carla;
	}

	public String getCarlaAnswer(int i) {
		return carla_answer[i];
	}

	public String getCarlaAnswer0(int i) {
		return carla_start_0[i];
	}

	public String getCarlaAnswer1(int i) {
		return carla_start_1[i];
	}

	public String getCarlaLose(int i) {
		return carla_lose[i];
	}

	public String getPirateAnswer(int i) {
		return pirate_start[i];
	}

	public String[] getGuybrushIntro() {
		return guybrush_start;
	}

	public String getIntroGuybrushFuoco(int i) {
		return introGuybrushFuoco[i];
	}

	public String getIntroPirate1Fuoco(int i) {
		return introPirate1Fuoco[i];
	}

	public String getIntroPirate2Fuoco(int i) {
		return introPirate2Fuoco[i];
	}
	
}