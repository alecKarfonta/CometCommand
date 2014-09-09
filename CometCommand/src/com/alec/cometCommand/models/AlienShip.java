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

public class AlienShip {
	public Body body;
	public float radius  = 2;
	public boolean isDead = false;
	private Sprite sprite ;
	private Vector2 target;

	public AlienShip(World world) {
		target = new Vector2(0,0);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody; 
		bodyDef.position.set(-200, 70); 

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 2;
		fixtureDef.restitution = 0;
		fixtureDef.friction = .32f;
		fixtureDef.filter.categoryBits = Constants.FILTER_ALIENSHIP;
		fixtureDef.filter.maskBits = Constants.FILTER_ALIENSHIP
				| Constants.FILTER_EARTH | Constants.FILTER_LASER | Constants.FILTER_COMET | Constants.FILTER_ASTEROID;

		CircleShape shape = new CircleShape();
		shape.setRadius(radius);
		fixtureDef.shape = shape;

		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		body.setAngularDamping(.01f);

		sprite = new Sprite(Assets.instance.aliens.smallRedShip);
		sprite.setSize((float) ((radius * 2) + (radius * Math.random())),
				radius * 2);
		sprite.setOrigin(radius, radius);
		body.setUserData(this);
		body.setAngularVelocity((float) (1 + (5 * Math.random()) * ((Math.random() > .5f) ? 1 : -1)));
	}


	public void render(SpriteBatch batch) {
		sprite.setPosition(body.getWorldCenter().x - radius, 
				body.getWorldCenter().y - radius);
		sprite.setRotation((float) Math.toDegrees(body.getAngle()));
		sprite.draw(batch);
	}

	public void update(float delta) {
		body.setTransform(body.getPosition().lerp(target, .01f), 
				(float) Math.toRadians(MyMath.getAngleBetween( target, body.getPosition())) );
	}
	
	public void setTartget (Vector2 target) {
		this.target = target;
	}
	
	public void reset() {
		Filter filter = this.body.getFixtureList().get(0).getFilterData();
		filter.categoryBits = Constants.FILTER_NONE;
		body.getFixtureList().get(0).setFilterData(filter);
	}
}