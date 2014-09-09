package com.alec.cometCommand.models;

import com.alec.cometCommand.controllers.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class AsteroidDeath {
	public ParticleEffect explosionParticles = new ParticleEffect();
	float lifeTime;
	float duration = 1.0f;
	
	public AsteroidDeath (Vector2 initPos) {
		lifeTime = 0.0f;
		explosionParticles.load(Gdx.files.internal("particles/asteroidDeath.pfx"),
				Gdx.files.internal("particles"));
		explosionParticles.setPosition(initPos.x, initPos.y);
		explosionParticles.start();
		AudioManager.instance.play(Assets.instance.sounds.asteroidDeath);
	}
		
	public boolean shouldDestroy() {
		return lifeTime > duration;
	}
	
	public void render(SpriteBatch batch, float deltaTime) {
		lifeTime += deltaTime;
		explosionParticles.draw(batch, deltaTime);
	}
}
