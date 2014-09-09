package com.alec.cometCommand.models;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;

public class Gauge {

	AtlasRegion border, infill;
	float width, height;
	Vector2 displayHealth;
	float x, y;
	boolean isDebug = false;

	public Gauge(float health, float x, float y, AtlasRegion infill) {
		displayHealth = new Vector2(health,0);
		this.x = x;
		this.y = y;
		border = Assets.instance.ui.healthGaugeBorder;
		this.infill = infill;
		width = 300;
		height = 20;
	}

	public void render(SpriteBatch batch, float health) {
		if (displayHealth.x != health) {
			displayHealth.lerp(new Vector2(health,0),.1f);
		}
		batch.draw(border, x, y - height, 0, 0, width, height, 1, 1, 0);
		batch.draw(infill, x + 7, y - height + 2, 0, 0, width - 7, height - 8,
				displayHealth.x / 100, 1, 0);
	}
}
