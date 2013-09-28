package com.gorgo.pirates.view;

import android.graphics.Canvas;
import com.gorgo.pirates.controller.GameEngine;


public interface Renderer {
	public void render(Canvas canvas, GameEngine gameEngine);
}
