package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class AsteroidFactory {
	// private Color defaultColor = new Color(176, 230, 255);
	private World world;

	public AsteroidFactory(World world) {
		this.world = world;
	}

	public Asteroid makeAsteroid() {
		return new Asteroid(world, generateRadius());
	}
	
	public Asteroid makeAsteroid(float radius) {
		return new Asteroid(world, radius);
	}

	
	public float generateRadius() {
		return (float) (Constants.SMALL_ASTEROID_RADIUS + Math.random() * Constants.SMALL_ASTEROID_RADIUS * 4);
	}
	
	public Color generateColor() {
		return new Color( (float)(.5f * Math.random()), ((float)Math.random()), ((float)Math.random()), 1 );
	}

	public Vector2 generateInitPosisition() {
		return new Vector2((float) (150 + Math.random() * 100)
				* MyMath.randomSignChange(),
				((float) (150 + Math.random() * 100) * ((Math.random() < .9) ? 1 : -1) ) );
	}

	public Vector2 generateInitVelocity(Vector2 initPosition) {
		Vector2 initialVelocity = new Vector2(); // polar

		// rho = (G * m1 * m2) / d^2
		initialVelocity.x = (float) (1000 + Math.random() * 1000); // the initial
																	// asteroid
																	// magnitude
		// theta = angle between body and earth
		initialVelocity.y = MyMath.getAngleBetween(initPosition, new Vector2(0,
				0)); // direction
		// vary the initial angle so it doesn't point directly at earth
		initialVelocity.y += Math.random() * 45 * MyMath.randomSignChange();
		return MyMath.getRectCoords(initialVelocity);
	}

}
