package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Moon {
	private final String TAG = Moon.class.getName();
	public Body body;
	private float radius = Constants.EARTH_RADIUS * .5f;
	Vector2 posVectorPolar = new Vector2();
	private Sprite sprite;
	
	public Moon(World world) {
		init(world);
	}

	public void init(World world) {
		Vector2 initPosition = new Vector2();
		initPosition.x = 100;

		Vector2 initVelocityPolar = new Vector2();
		initVelocityPolar.x = 25;
		initVelocityPolar.y = 90;
		
		posVectorPolar.x = 100;
		posVectorPolar.y = 0;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(initPosition.x, initPosition.y);
		bodyDef.allowSleep = false;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 500.5f;
		fixtureDef.friction = .32f;
		fixtureDef.restitution = 0;

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		fixtureDef.shape = shape;

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		sprite = new Sprite(Assets.instance.moon.moon);
		sprite.setSize(radius * 2, radius * 2);
		sprite.setOrigin(radius, radius);
		sprite.setPosition(body.getWorldCenter().x - radius, body.getWorldCenter().y - radius);

		body.setUserData(this);
	}
	
	public void update(float delta) {

		posVectorPolar.y += delta * 10;
		//posVectorPolar.y %= 360;
		body.setTransform(MyMath.getRectCoords(posVectorPolar), 
				(float) Math.toRadians(MyMath.getAngleBetween(body.getPosition(), new Vector2(0,0))));
		
		/** / 	// gravity
		Vector2 forceVectorPolar = new Vector2();
		// rho = (G * m1 * m2) / d^2
		forceVectorPolar.x = (float) ((body.getMass() * 100000)		// earth is static so mass is infinite, cant use in equation
				/ Math.pow(MyMath.getDistanceBetween(body.getPosition(), center), 2)); // magnitude
		// theta = angle between body and earth
		forceVectorPolar.y = MyMath.getAngleBetween(body.getPosition(),
				center); // direction
		body.applyForceToCenter(MyMath.getRectCoords(forceVectorPolar), false);
		/**/
	}

	public void render(SpriteBatch batch) {
		sprite.setPosition(body.getWorldCenter().x - radius, 
				body.getWorldCenter().y - radius);
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.draw(batch);
	}

}
