package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Earth {

	private final String TAG = Earth.class.getName();
	private Animation animation;
	private float stateTime = 0.0f;
	private float shieldTime = 0.0f;
	private ParticleEffect shieldParticles = new ParticleEffect();
	private Sprite sprite;
	private Sprite shieldSprite;
	

	public Earth(World world) {
		init(world);
	}

	public void init(World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.KinematicBody;
		bodyDef.position.set(0, 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 5.5f;
		fixtureDef.friction = .32f;
		fixtureDef.restitution = 0;
		fixtureDef.filter.categoryBits = Constants.FILTER_EARTH;
		fixtureDef.filter.maskBits = Constants.FILTER_ASTEROID
				| Constants.FILTER_COMET;

		CircleShape shape = new CircleShape();
		shape.setRadius(Constants.EARTH_RADIUS);
		fixtureDef.shape = shape;

		Body body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		// animation
		//animation = Assets.instance.earth.fullHealth;
		
		/**/
		sprite = new Sprite(Assets.instance.earth.currFrame);
		sprite.setSize(Constants.EARTH_RADIUS * 2, Constants.EARTH_RADIUS * 2);
		sprite.setOrigin(Constants.EARTH_RADIUS, Constants.EARTH_RADIUS);
		sprite.setPosition(0- Constants.EARTH_RADIUS, 0- Constants.EARTH_RADIUS);

		shieldSprite = new Sprite(Assets.instance.earth.shield);
		shieldSprite.setSize(Constants.EARTH_RADIUS * 2, Constants.EARTH_RADIUS * 2);
		shieldSprite.setOrigin(Constants.EARTH_RADIUS, Constants.EARTH_RADIUS);
		shieldSprite.setPosition(0- Constants.EARTH_RADIUS, 0- Constants.EARTH_RADIUS);
		/**/

		body.setUserData(this);

		// shield particles
		shieldParticles.load(Gdx.files.internal("particles/shield.pfx"),
				Gdx.files.internal("particles"));
		shieldParticles.setPosition(0, 0);
		shieldParticles.start();
	}

	public void activateShield(float time) {
		shieldTime = time;
	}

	public void render(SpriteBatch batch, float delta) {
		sprite.draw(batch);
		/** /		// animation
		stateTime += delta;
		batch.draw(animation.getKeyFrame(stateTime, true),
				body.getWorldCenter().x - Constants.EARTH_RADIUS,
				body.getWorldCenter().y - Constants.EARTH_RADIUS,
				Constants.EARTH_RADIUS, Constants.EARTH_RADIUS,
				Constants.EARTH_RADIUS * 2, Constants.EARTH_RADIUS * 2, 1, 1, 0);
		/**/
		
		// render shield
		if (shieldTime > 0) {
			shieldTime -= delta;
			shieldParticles.draw(batch, delta);
			shieldSprite.draw(batch);
		}
	}
}
