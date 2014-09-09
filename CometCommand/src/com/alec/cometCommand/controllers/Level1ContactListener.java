package com.alec.cometCommand.controllers;

import com.alec.cometCommand.models.Asteroid;
import com.alec.cometCommand.models.Comet;
import com.alec.cometCommand.models.Earth;
import com.alec.cometCommand.models.Laser;
import com.alec.cometCommand.models.Moon;
import com.alec.cometCommand.views.Level1;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Level1ContactListener implements ContactListener {
	public static final String TAG = Level1ContactListener.class.getName();
	private Level1 game;

	public Level1ContactListener(Level1 game) {
		this.game = game;
	}

	@Override
	public void beginContact(Contact contact) {

	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// if a comet hits a laser
		if (contact.getFixtureA().getBody().getUserData() instanceof Comet
				&& contact.getFixtureB().getBody().getUserData() instanceof Laser) {
			game.collisionLaserComet((Comet) contact.getFixtureA().getBody()
					.getUserData());
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Laser
				&& contact.getFixtureB().getBody().getUserData() instanceof Comet) {
			game.collisionLaserComet((Comet) contact.getFixtureB().getBody()
					.getUserData());
		} else
		// if an asteroid hits a laser
		if (contact.getFixtureA().getBody().getUserData() instanceof Asteroid
				&& contact.getFixtureB().getBody().getUserData() instanceof Laser) {
			game.collisionLaserAsteroid((Asteroid) contact.getFixtureA()
					.getBody().getUserData());
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Laser
				&& contact.getFixtureB().getBody().getUserData() instanceof Asteroid) {
			game.collisionLaserAsteroid((Asteroid) contact.getFixtureB()
					.getBody().getUserData());
		} else
		// if an asteroid hits the moon
		if (contact.getFixtureA().getBody().getUserData() instanceof Asteroid
				&& contact.getFixtureB().getBody().getUserData() instanceof Moon) {
			game.destroyAsteroid((Asteroid) contact.getFixtureA().getBody()
					.getUserData());
		} else if (contact.getFixtureB().getBody().getUserData() instanceof Asteroid
				&& contact.getFixtureA().getBody().getUserData() instanceof Moon) {
			game.destroyAsteroid((Asteroid) contact.getFixtureB().getBody()
					.getUserData());
			// if an asteroid hits the earth
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Earth
				&& contact.getFixtureB().getBody().getUserData() instanceof Asteroid) {
			game.collisionAsteroidEarth((Asteroid) contact.getFixtureB()
					.getBody().getUserData());
			// if a comet hits the earth
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Earth
				&& contact.getFixtureB().getBody().getUserData() instanceof Comet) {
			game.collisionCometEarth((Comet) contact.getFixtureB()
					.getBody().getUserData());
			// if a comet hits the moon
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Moon
				&& contact.getFixtureB().getBody().getUserData() instanceof Comet) {
			game.collisionCometMoon((Comet) contact.getFixtureB()
					.getBody().getUserData());
		} else if (contact.getFixtureA().getBody().getUserData() instanceof Comet
				&& contact.getFixtureB().getBody().getUserData() instanceof Moon) {
			game.collisionCometMoon((Comet) contact.getFixtureB()
					.getBody().getUserData());
		}
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

	}

}
