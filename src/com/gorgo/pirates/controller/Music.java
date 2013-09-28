package com.gorgo.pirates.controller;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

/**
 * @author Gorgo
 * 
 * La classe si occupa di istanziare il MediaPlayer per la musica. Se è già stato inizializzato cambia la musica passata via ReferenceID
 * 
 */

public class Music{
	
	public static MediaPlayer changeMusic(Context context, MediaPlayer mPlayer, int id){
		AssetFileDescriptor afd = context.getResources()
				.openRawResourceFd(id);
		try {
			if(mPlayer == null){
				mPlayer = MediaPlayer.create(context, id);
				return mPlayer;
			}
			mPlayer.reset();
			mPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getDeclaredLength());
			mPlayer.prepare();
			mPlayer.start();
			afd.close();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mPlayer;
	}
}
