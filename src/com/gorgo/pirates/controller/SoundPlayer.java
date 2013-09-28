package com.gorgo.pirates.controller;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.gorgo.pirates.R;

/**
 * @author Gorgo
 * 
 * SoundPlayer si occupa di istanziare in memoria i suoni e li riproduce
 * 
 */

public class SoundPlayer {
	public static final int SWORD_COLPO = 1;
	public static final int SWORD_COLPO_2 = 2;
	public static final int GUYBRUSH_PERDE = 3;
	public static final int PIRATA_PERDE = 4;

	private SoundPool soundPool;
	private HashMap<Integer, Integer> soundPoolMap;

	private Context context;

	public void initSounds(Context context) {
		this.context = context;
	     soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	     soundPoolMap = new HashMap<Integer, Integer>();
	     soundPoolMap.put(SWORD_COLPO, soundPool.load(context, R.raw.swordcolpo, 1));
	     soundPoolMap.put(SWORD_COLPO_2, soundPool.load(context, R.raw.swordcolpo2, 2));
	     soundPoolMap.put(GUYBRUSH_PERDE, soundPool.load(context, R.raw.perde, 3));
	     soundPoolMap.put(PIRATA_PERDE, soundPool.load(context, R.raw.perde_pirata, 4));
	}
	          
	public void playSound(int sound) {
	    AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
	    
	    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);     
	}

	public void SuonoColpo() {
	    playSound(SWORD_COLPO);
	} 
	
	public void SuonoColpo2() {
	    playSound(SWORD_COLPO_2);
	} 
	
	public void SuonoGuybrushPerde() {
	    playSound(GUYBRUSH_PERDE);
	} 
	
	public void SuonoPirataPerde() {
	    playSound(PIRATA_PERDE);
	} 
}
