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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Asteroid  {
	public Body body;
	public float radius;
	public boolean isDead = false;
	private Sprite sprite ;
	private Vector2 target;

	public Asteroid(World world, float radius) {
		this.radius = radius;
		target = new Vector2(0,0);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody; 
		bodyDef.position.set(-200, 70); 

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 2;
		fixtureDef.restitution = 0;
		fixtureDef.friction = .32f;
		fixtureDef.filter.categoryBits = Constants.FILTER_ASTEROID;
		fixtureDef.filter.maskBits = Constants.FILTER_ASTEROID
				| Constants.FILTER_EARTH | Constants.FILTER_LASER | Constants.FILTER_COMET;

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		fixtureDef.shape = shape;

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setAngularDamping(.01f);

		sprite = new Sprite(Assets.instance.asteroids.asteroidVesta);
		sprite.setSize((float) ((radius * 2) + (radius * Math.random())),
				radius * 2);
		sprite.setOrigin(radius, radius);
		body.setUserData(this);
		body.setAngularVelocity((float) (1 + (5 * Math.random()) * ((Math.random() > .5f) ? 1 : -1)));
	}

	public Body getBody() {
		return body;
	}
	
	public void setTarget(Vector2 newTarget) {
		this.target = newTarget;
	}

	public void render(SpriteBatch batch) {
		sprite.setPosition(body.getWorldCenter().x - radius, 
				body.getWorldCenter().y - radius);
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.draw(batch);
	}

	public void update(float delta) {
		Vector2 forceVectorPolar = new Vector2();
		// rho = (G * m1 * m2) / d^2
		forceVectorPolar.x = (body.getMass() * 8000)		// earth is static so mass is infinite, cant use in equation
				/ body.getPosition().dst(target); // magnitude
		// theta = angle between body and earth
		forceVectorPolar.y = MyMath.getAngleBetween(body.getPosition(),
				target); // direction
		body.applyForceToCenter(MyMath.getRectCoords(forceVectorPolar), false);
	}
	/** /
	@Override
	public void reset() {
		Filter filter = this.body.getFixtureList().get(0).getFilterData();
		filter.categoryBits = Constants.FILTER_NONE;
		body.getFixtureList().get(0).setFilterData(filter);
	}
	/**/
}