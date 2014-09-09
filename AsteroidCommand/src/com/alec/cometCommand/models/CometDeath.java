package com.alec.cometCommand.models;

import com.alec.cometCommand.controllers.AudioManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class CometDeath {

	public ParticleEffect particles = new ParticleEffect();
	float lifeTime;
	float duration = 1.0f;

	public CometDeath(Vector2 initPos) {
		lifeTime = 0.0f;
		particles.load(Gdx.files.internal("particles/cometDeath.pfx"),
				Gdx.files.internal("particles"));
		particles.setPosition(initPos.x, initPos.y);
		particles.start();
		AudioManager.instance.play(Assets.instance.sounds.asteroidDeath);
	}

	public CometDeath(Vector2 initPos, Color color) {
		lifeTime = 0.0f;
		particles.load(Gdx.files.internal("particles/cometDeath.pfx"),
				Gdx.files.internal("particles"));
		particles.setPosition(initPos.x, initPos.y);
		particles.start();
		// tint
		if (color != null) {
			particles.getEmitters().get(0).getTint()
					.setColors(new float [] { color.r, color.g, color.b });
		}
		// sound
		AudioManager.instance.play(Assets.instance.sounds.asteroidDeath);
	}

	public boolean shouldDestroy() {
		return lifeTime > duration;
	}

	public void render(SpriteBatch batch, float deltaTime) {
		lifeTime += deltaTime;
		particles.draw(batch, deltaTime);
	}

}
