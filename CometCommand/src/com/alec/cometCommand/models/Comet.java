package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.MyMath;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Comet {
	public Body body;
	public boolean isDead = false;
	private Color color;

	public ParticleEffect particles = new ParticleEffect();

	public Comet(World world, Color color) {
		this.color = color;
		// define body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(300, 0); // initially place it off screen
		bodyDef.bullet = true;

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 2;
		fixtureDef.friction = .01f;
		fixtureDef.restitution = 0;
		fixtureDef.filter.categoryBits = Constants.FILTER_COMET;
		fixtureDef.filter.maskBits = Constants.FILTER_EARTH
				| Constants.FILTER_LASER | Constants.FILTER_COMET;

		CircleShape shape = new CircleShape();
		shape.setRadius(Constants.SMALL_COMET_RADIUS);
		fixtureDef.shape = shape;

		// create body
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);

		// model data
		body.setUserData(this);

		particles.load(Gdx.files.internal("particles/comet.pfx"),
				Gdx.files.internal("particles"));
		particles.setPosition(body.getPosition().x, body.getPosition().y);
		particles.start();

		particles.getEmitters().get(0).getTint()
				.setColors(new float[] { color.r, color.g, color.b });
	}

	public void render(SpriteBatch batch, float deltaTime) {
		particles.draw(batch, deltaTime);
	}

	public void update(float delatTime) {
		// apply gravity
		Vector2 forceVectorPolar = new Vector2();
		forceVectorPolar.x = (body.getMass() * 10000)
				/ body.getPosition().dst(0,0);
		forceVectorPolar.y = MyMath.getAngleBetween(body.getPosition(),
				new Vector2(0, 0));
		body.applyForceToCenter(MyMath.getRectCoords(forceVectorPolar), false);
		particles.setPosition(body.getPosition().x, body.getPosition().y);
	}

	public Color getColor() {
		return color;
	}

	public void reset() {
		Filter filter = this.body.getFixtureList().get(0).getFilterData();
		filter.categoryBits = Constants.FILTER_NONE;
		body.getFixtureList().get(0).setFilterData(filter);
		// body.setTransform(new Vector2(100,0), 0);
	}

}
