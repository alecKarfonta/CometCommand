package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.GamePreferences;
import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Laser {
	public Body body;
	private Sprite sprite;
	public float width, length;
	private Vector2 initPos;

	public Laser(World world, Vector2 initPos, float length, float angle) {
		this.width = 1.0f;
		this.length = length;
		this.initPos = initPos;
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody; // cannot move
		bodyDef.position.set(initPos); // position at center of screen
		bodyDef.angle = (float) Math.toRadians(angle);
		bodyDef.allowSleep = false;
		bodyDef.bullet = true;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.1f;
		fixtureDef.restitution = 0;
		fixtureDef.friction = .32f;
		fixtureDef.filter.categoryBits = Constants.FILTER_LASER;
		fixtureDef.filter.maskBits = Constants.FILTER_ASTEROID
				| Constants.FILTER_COMET;

		PolygonShape shape = new PolygonShape();
		// make the rectangle with setAsBox(), and set the origin to the middle
		shape.setAsBox(length / 2, width / 2, new Vector2(length / 2
				+ Constants.LASERDEFENSE_TURRET_RADIUS, 0), 0);
		// (float) Math.toRadians(angle) );
		// make the rectangle with pixel coords, the origin is the first coord
		// shape.set(new float[] { 0, -width / 2, length, -width / 2, length,
		// width / 2, 0, width / 2 });

		fixtureDef.shape = shape;

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		sprite = new Sprite(Assets.instance.lasers.laser1Blue);
		// random Color
		/**
		 * / int random = (int) (Math.random() * 5); switch (random) { case 1:
		 * sprite = new Sprite(Assets.instance.lasers.laser1Red); break; case 2:
		 * sprite = new Sprite(Assets.instance.lasers.laser1Green); break; case
		 * 3: sprite = new Sprite(Assets.instance.lasers.laser1Yellow); break;
		 * case 4: sprite = new Sprite(Assets.instance.lasers.laser1Purple);
		 * break; case 5: sprite = new
		 * Sprite(Assets.instance.lasers.laser1Green); break; default: sprite =
		 * new Sprite(Assets.instance.lasers.laser1Blue); break; } /
		 **/

		sprite.setSize(length, width);
		sprite.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		sprite.setOrigin(0, width / 2);
		body.setUserData(this);
	}

	public void update(Vector2 target) {
		//setAngle(MyMath.getAngleBetween(initPos, target));
		// if not using accelerometer, allow lasers to change length
		/** /
		if (!GamePreferences.instance.useAccelerometer) {
			//Fixture fixture = body.getFixtureList().get(0);
			///PolygonShape shape = (PolygonShape) fixture.getShape();
			//float length = MyMath.getDistanceBetween(initPos, target);
			((PolygonShape)body.getFixtureList().get(0).getShape()).setAsBox(length / 2, 1 / 2, new Vector2(length / 2
					+ Constants.LASERDEFENSE_TURRET_RADIUS, 0), 0);
			this.length = length;
			sprite.setSize(length, width);
		}
		/**/
	}

	public void setAngle(float degrees) {
		body.setTransform(body.getPosition(), (float) Math.toRadians(degrees));
	}

	public Body getBody() {
		return body;
	}

	public void render(SpriteBatch batch) {
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.setPosition(body.getWorldCenter().x, body.getWorldCenter().y);
		sprite.draw(batch);
	}
}
