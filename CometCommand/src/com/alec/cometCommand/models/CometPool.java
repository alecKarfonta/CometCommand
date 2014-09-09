package com.alec.cometCommand.models;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class CometPool extends Pool<Comet> implements Disposable {
	private CometFactory factory;
	
	public CometPool (CometFactory factory) {
		this.factory = factory;
	}
	
	@Override
	protected Comet newObject() {
		return factory.makeComet();
	}

	@Override
	public void dispose() {
		clear();
	}
	
}
