package com.alec.cometCommand.models;

import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class CometFactory {
	// private Color defaultColor = new Color(176, 230, 255);
	private World world;

	public CometFactory(World world) {
		this.world = world;
	}

	public Comet makeComet() {
		return new Comet(world, generateColor());
	}
	
	public Comet makeComet(Color color) {
		return new Comet(world, color);
	}


	public Color generateColor() {
		return new Color( (float)(.5f * Math.random()), ((float)(.25f + .75f * Math.random())), ((float)(.25f + .75f * Math.random())), 1 );
	}

	public Vector2 generateInitPosisition() {
		return new Vector2((float) (100 + Math.random() * 100)
				* MyMath.randomSignChange(),
				((float) (100 + Math.random() * 100) * MyMath
						.randomSignChange()));
	}

	public Vector2 generateInitVelocity(Vector2 initPosition) {
		Vector2 initialVelocity = new Vector2(); // polar

		// rho = (G * m1 * m2) / d^2
		initialVelocity.x = (float) (2500 + Math.random() * 5000); // the initial
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
