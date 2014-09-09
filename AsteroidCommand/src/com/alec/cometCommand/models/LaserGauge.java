package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LaserGauge {
	
	TextureRegion border, infill;
	float width, height;
	float displayHealth;
	float displayPositionFactorX;
	boolean isDebug = false;
	
	public LaserGauge (float health, float positionFactorX) {
		init(health, positionFactorX);
	}
	
	public void init (float health, float positionFactorX) {
		displayHealth = health;
		border = Assets.instance.ui.healthGaugeBorder;
		infill = Assets.instance.ui.laserGaugeInfill;
		displayPositionFactorX = positionFactorX;
		width = 300;
		height = 20;
	}

	public void render(SpriteBatch batch, float health) {
		if (displayHealth != health) {
			displayHealth = MyMath.lerp(displayHealth, health, .25f);
		}
		batch.draw(border,
				-Constants.VIEWPORT_GUI_WIDTH / 2  , Constants.VIEWPORT_GUI_HEIGHT / 2  - height + displayPositionFactorX, 
				0, 0, 
				width, height, 
				1, 1, 
				0);
		batch.draw(infill,
				(-Constants.VIEWPORT_GUI_WIDTH / 2) + 5 , Constants.VIEWPORT_GUI_HEIGHT / 2 - height + 2 + displayPositionFactorX, 
				0, 0, 
				width - 6, height - 8, 
				displayHealth / 100, 1, 
				0);
		
		if (isDebug) {
			Assets.instance.fonts.defaultNormal.draw(batch, ""+ health, 
					-Constants.VIEWPORT_GUI_WIDTH / 2 + width , Constants.VIEWPORT_GUI_HEIGHT / 2  - height);
		}

	}
}
