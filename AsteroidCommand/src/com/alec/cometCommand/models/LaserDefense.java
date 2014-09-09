package com.alec.cometCommand.models;

import com.alec.cometCommand.MyMath;
import com.alec.cometCommand.controllers.AudioManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class LaserDefense {
	private Vector2 position;
	public Laser laser;
	private float maxLaserLength = 75.0f;
	private Vector2 currAnglePolar;

	public LaserDefense(World world, float x, float y, float angle) {
		position = new Vector2(x, y);
		currAnglePolar = new Vector2(0,90);
	}

	public void fireLaser(World world, Vector2 target) {
		// if the laser isn't already active create one
		if (laser == null) {
			
			float length = MathUtils.clamp(position.dst(target), 50, maxLaserLength);
			
			laser = new Laser(world, position,
					length,
					MyMath.getAngleBetween(position, target));
			
			// pan audio to mouse click
			/** /	// TODO: figure out how to scale the click x to a value between 0 and 1
			float oldRange = Constants.VIEWPORT_WIDTH;
			float newValue = ((target.x - -Constants.VIEWPORT_WIDTH) / oldRange) - 1;
			
			.out.println("Audio Pan = " + newValue);
			/** /
			AudioManager.instance.loop(
					Assets.instance.sounds.laser, 
					1.0f,	// volume  
					1.0f, 		// pitch
					1.0f);		// pan
			/**/
		// else the laser is active, so just angle it
		} else {
			laser.setAngle(MyMath.getAngleBetween(position, target));
		}
	}
	
	public void setAngle(float targetAngle) {
		if (laser != null) {
			laser.setAngle(targetAngle);
		}
	}

	public void stopLaser() {
		if (laser != null) {
			laser = null;
			AudioManager.instance.stopSound(Assets.instance.sounds.laser);
		}
	}


	public void render(SpriteBatch batch) {
		if (laser != null) {
			laser.render(batch);
		}
	}
}
