package com.gorgo.pirates.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author maximo guerrero
 * 
 * Classe che si occupa di Parsare XML delle coordinate dei frame, e di disegnare a video le Sprite Animation
 * 
 */

public class SpriteTile extends Drawable {

	private static final String TAG = SpriteTile.class.getSimpleName();
	
	private Bitmap tileSheet; // sprite tile sheet for all animations.
								// rectangles are used to slip and only show
								// parts of one bitmap
	private Hashtable<String, AnimationSequece> animations; // all animation
															// sequences for
															// this sprite
	private String currentAnimation; // current animation
														// sequence
	private int currentFrame = 0; // current frame being played
	private int waitDelay = 0; // delay before the next frame

	private Rect destRect;

	// Class contains Information about one frame
	private class FrameInfo {
		public Rect rect = new Rect();
		public int nextFrameDelay = 0;
	}

	// Class encapsulates all the data for an animations sequence. List for
	// frames, animcation name, if the sequence will loop
	private class AnimationSequece {
		public ArrayList<FrameInfo> sequence;
		public boolean canLoop = false;
	}

	// takes resource ids for bitmaps and xmlfiles
	public SpriteTile(int BitmapResourceId, int XmlAnimationResourceId,
			Context context) {
		loadSprite(BitmapResourceId, XmlAnimationResourceId, context);
	}

	// load bitmap and xml data
	public void loadSprite(int spriteid, int xmlid, Context context) {
		
		// Chiamare la BitmapFactory.decodeResource() con Sprite così grosse fa esplodere l'Heap, meglio decodeStream() 
		// Inoltre openRawResource visualizza la Bitmap nella giusta dimensione su qualsiasi display
		InputStream is = context.getResources().openRawResource(spriteid);
		tileSheet = BitmapFactory.decodeStream(is);
		
		try {
			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//PARSER XML
		
		// load the xml will all the frame animations into a hashtable
		XmlResourceParser xpp = context.getResources().getXml(xmlid);

		animations = new Hashtable<String, AnimationSequece>();

		try {
			int eventType = xpp.getEventType();
			String animationname = "";
			AnimationSequece animationsequence = new AnimationSequece();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_DOCUMENT) {
					System.out.println("Start document");

				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					System.out.println("End document");

				} else if (eventType == XmlPullParser.START_TAG) {
					System.out.println("Start tag " + xpp.getName());
					if (xpp.getName().toLowerCase().equals("animation")) {
						animationname = xpp.getAttributeValue(null, "name");
						animationsequence = new AnimationSequece();
						animationsequence.sequence = new ArrayList<FrameInfo>();
						animationsequence.canLoop = xpp
								.getAttributeBooleanValue(null, "canLoop",
										false);
					} else if (xpp.getName().toLowerCase().equals("framerect")) {
						FrameInfo frameinfo = new FrameInfo();
						Rect frame = new Rect();
						frame.top = xpp.getAttributeIntValue(null, "top", 0);
						frame.bottom = xpp.getAttributeIntValue(null, "bottom",
								0);
						frame.left = xpp.getAttributeIntValue(null, "left", 0);
						frame.right = xpp
								.getAttributeIntValue(null, "right", 0);
						frameinfo.rect = frame;
						frameinfo.nextFrameDelay = xpp.getAttributeIntValue(
								null, "delayNextFrame", 0);
						animationsequence.sequence.add(frameinfo);
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					if (xpp.getName().toLowerCase().equals("animation")) {
						animations.put(animationname, animationsequence);
					}
				} else if (eventType == XmlPullParser.TEXT) {
					System.out.println("Text " + xpp.getText());

				}
				eventType = xpp.next();
			}
		} catch (Exception e) {
			/*Log.e("ERROR", "LOAD ERROR IN SPRITE TILE  CODE:" + e.toString());*/
		}
		Log.d(TAG, "Sprite Loaded ");
	}

	// Draw sprite onto screen
	@Override
	public void draw(Canvas canvas) {
		try {
			FrameInfo frameinfo = animations.get(currentAnimation).sequence
					.get(currentFrame);
			Rect rclip = frameinfo.rect;
			canvas.drawBitmap(tileSheet, rclip, destRect, null);
			update();
		} catch (Exception e) {
			/*Log.e("ERROR", "DRAW ERROR IN SPRITE TILE  CODE:" + e.toString()
					+ e.getStackTrace().toString());*/
		}
	}

	@Override
	public int getOpacity() {
		return 100;
	}

	@Override
	public void setAlpha(int alpha) {}

	@Override
	public void setColorFilter(ColorFilter cf) {}

	// updates the frame counter to the next frame
	public void update() {
		if (waitDelay == 0)// if done waiting
		{
			// set current frame back to the first because looping is possible
			if (animations.get(currentAnimation).canLoop
					&& currentFrame == animations.get(currentAnimation).sequence.size() - 1)
				currentFrame = 0;
			
			//Se sono arrivato alla fine dell'animazione non cambiare il currentFrame
			else if (currentFrame == animations.get(currentAnimation).sequence.size() - 1) {} 
			else {
				currentFrame++; // go to next frame
				

				FrameInfo frameinfo = animations.get(currentAnimation).sequence
						.get(currentFrame);
				waitDelay = frameinfo.nextFrameDelay; // set delaytime for the
														// next frame
			}
		} else {
			waitDelay--; // wait for delay to expire
		}

	}

	// has animation finished playing, returns true on a animaiton that can loop
	// for ever
	public boolean hasAnimationFinished() {
		AnimationSequece as = animations.get(currentAnimation);
		if (currentFrame == as.sequence.size() - 1 && !as.canLoop)
			return true;

		return false;
	}
	
	// Funzione che "forza" il GC a pulire la Sprite. Non è detto che avvenga subito!!
	public void recycle(){
		this.tileSheet.recycle();
	}
	
	// Imposta l'animazione currentAnimation alla Sprite
	public void setCurrentAnimation(String currentAnimation, boolean resetloop) {
		this.currentAnimation = currentAnimation;
		if (!resetloop)
			currentFrame = 0;
	}
	
	// Imposta un eventuale Loop dell'animazione
	public void setCanLoop(){
		this.animations.get(currentAnimation).canLoop = true;
	}
	
	// Imposta idle l'animazione
	public void setAnimationIdle(){
		AnimationSequece as = animations.get(currentAnimation);
		this.animations.get(currentAnimation).canLoop = false;
		currentFrame = as.sequence.size()-1;
	}

	// Viene istanziato il rettangolo di disegno proporzionato alla canvas
	public void setDestRect(Rect destRect){
		this.destRect = destRect;
	}
	
	public Rect getDestRect(){
		return this.destRect;
	}
	public String getCurrentAnimation() {
		return currentAnimation;
	}
}
