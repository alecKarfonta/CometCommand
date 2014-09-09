package com.alec.cometCommand.models;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class AsteroidPool extends Pool<Asteroid> implements Disposable {
	private AsteroidFactory factory;

	public AsteroidPool(AsteroidFactory factory) {
		this.factory = factory;
	}

	@Override
	protected Asteroid newObject() {
		return factory.makeAsteroid();
	}

	@Override
	public void dispose() {
		clear();
	}
}
