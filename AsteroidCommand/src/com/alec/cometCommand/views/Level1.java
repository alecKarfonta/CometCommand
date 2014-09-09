package com.alec.cometCommand.views;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;
import java.util.ArrayList;
import com.alec.cometCommand.Constants;
import com.alec.cometCommand.GamePreferences;
import com.alec.cometCommand.MyMath;
import com.alec.cometCommand.controllers.CameraHelper;
import com.alec.cometCommand.controllers.Level1ContactListener;
import com.alec.cometCommand.models.Assets;
import com.alec.cometCommand.models.Asteroid;
import com.alec.cometCommand.models.AsteroidDeath;
import com.alec.cometCommand.models.AsteroidFactory;
import com.alec.cometCommand.models.AsteroidPool;
import com.alec.cometCommand.models.Comet;
import com.alec.cometCommand.models.CometDeath;
import com.alec.cometCommand.models.CometFactory;
import com.alec.cometCommand.models.CometPool;
import com.alec.cometCommand.models.Earth;
import com.alec.cometCommand.models.Gauge;
import com.alec.cometCommand.models.LaserDefense;
import com.alec.cometCommand.models.Moon;
import com.alec.cometCommand.views.transitions.ScreenTransition;
import com.alec.cometCommand.views.transitions.ScreenTransitionFade;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class Level1 extends AbstractGameScreen {
	private static final String TAG = Level1.class.getName();

	private World world;
	private InputMultiplexer inputPlexer;
	private Stage stage;
	private Table mainTable;
	private Skin skin;
	private Sprite background;
	private Gauge healthGauge, laserGauge;
	private Earth earth;
	private Moon moon;
	private ArrayList<Body> destroyQueue;
	private LaserDefense[] laserDefense;
	private Array<Asteroid> asteroids;
	private Array<AsteroidDeath> asteroidDeaths;
	private AsteroidFactory asteroidFactory;
	private AsteroidPool asteroidPool;
	private Array<Comet> comets;
	private Array<CometDeath> cometDeaths;
	private CometFactory cometFactory;
	private CometPool cometPool;
	
	// world variables
	private OrthographicCamera gameCamera;
	private OrthographicCamera guiCamera;
	private CameraHelper cameraHelper;
	private SpriteBatch spriteBatch;

	// stats window
	private Window winStats;
	private Label lblScore, lblBonus;

	// ** Debug
	private Box2DDebugRenderer debugRenderer;

	private boolean isDebug = false, isPaused = false;
	private boolean isFiringLaser = false;
	private float heavyTimer = 0.0f, cometTimer = 0.0f, cometInterval = 3.0f,
			asteroidTimer = 0.0f, asteroidInterval = 2.0f;
	private float width, height;
	private int score = 0, asteroidsDestroyed = 0, cometsDestroyed = 0,
			lasersActive = 4, playTime = 0;
	private int maxComets = 10;
	private int maxAsteroids = 25;
	private float earthHealth = 100.0f, laserHealth = 100.0f;
	private float backgroundRotation;
	private boolean isGameOver = false;

	// bonus
	private float bonusTimer = 0.0f; // how many things were destroyed in the
	private int bonusCount = 0;
	private float bonusAnimationLength = .2f;
	private boolean isBonusAnimationPlaying = false;
	private float bonusAnimationStateTime = 0.0f;
	private float waveTimer = 0.0f;
	private float waveInterval = 5.0f;
	private int level = 1;

	// accelerometer
	private boolean isUsingAccelerometer = GamePreferences.instance.useAccelerometer
			&& Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
	private Vector2 laserTargetPolar = new Vector2(75, 90);

	public Level1(DirectedGame game) {
		super(game);
		this.skin = Assets.instance.skin;
		Gdx.input.setCatchBackKey(true);
		if (isDebug) {
			Gdx.app.setLogLevel(Application.LOG_DEBUG);
		}
	}

	// initialize
	@Override
	public void show() {
		Gdx.app.debug(TAG, "show()");
		createWorld();
		createLevel();
		createGameListener();
		createUI();
	}

	public void createWorld() {
		Gdx.app.debug(TAG, "   createWorld()");
		// create the world with no gravity
		world = new World(new Vector2(0, 0), true);
		world.setContactListener(new Level1ContactListener(this));
		debugRenderer = new Box2DDebugRenderer(); // only used for drawing hit
													// boxes

		// setup a camera for game and gui
		gameCamera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		guiCamera = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraHelper = new CameraHelper();

		spriteBatch = new SpriteBatch();
		destroyQueue = new ArrayList<Body>();

		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
	}

	public void createGameListener() {
		// input listener
		InputAdapter input = new InputAdapter() {
			@Override
			public boolean keyUp(int keycode) {
				switch (keycode) {
				case Keys.P:
					isPaused = (isPaused) ? false : true;
					break;
				case Keys.BACK:
					onBackClicked();
				default:
					break;
				}
				return false;
			}

			@Override
			public boolean keyDown(int keycode) {
				switch (keycode) {
				case Keys.G:
					gravityWave();
					break;
				case Keys.M:
					moonPull();
					break;

				default:
					break;
				}
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				if (!isUsingAccelerometer) {
					Vector3 testPoint = new Vector3();
					// convert from vector2 to vector3
					testPoint.set(screenX, screenY, 0);
					// convert meters to pixel cords
					gameCamera.unproject(testPoint);

					if (!isPaused && !isGameOver) {
						for (int index = 0; index < lasersActive; index++) {
							laserDefense[index].fireLaser(world, new Vector2(
									testPoint.x, testPoint.y));
						}
					}
				}
				return false; // let other input adapters also respond
			}

			// click
			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				if (!isUsingAccelerometer) {
					isFiringLaser = true;
					Vector3 testPoint = new Vector3();
					// convert from vector2 to vector3
					// convert meters to pixel cords
					gameCamera.unproject(testPoint);

					if (!isPaused && !isGameOver) {
						for (int index = 0; index < lasersActive; index++) {
							laserDefense[index].fireLaser(world, new Vector2(
									testPoint.x, testPoint.y));
						}
					}
				}
				return false; // let other input adapters also respond
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				isFiringLaser = false || isUsingAccelerometer;
				stopLasers();
				// handled input = true
				return false;
			}

			// zoom
			@Override
			public boolean scrolled(int amount) {
				if (amount == 1) {
					cameraHelper.addZoom(gameCamera.zoom * .25f);
				} else if (amount == -1) {
					cameraHelper.addZoom(-gameCamera.zoom * .25f);
				}
				return false;
			}

		};

		inputPlexer = new InputMultiplexer();
		inputPlexer.addProcessor(input);
	}

	public void createUI() {
		stage = new Stage();
		mainTable = new Table(skin);

		mainTable.debugTable();

		mainTable.setFillParent(true);

		lblScore = new Label("Score: " + score, skin, "medium");
		mainTable.add(lblScore).expand().align(Align.top + Align.right).pad(10);

		lblBonus = new Label("Bonus: " + bonusCount, skin);
		mainTable.add(lblBonus).expand().align(Align.top + Align.left).padTop(22).padLeft(25);
		
		TextButton tbBack = new TextButton("Back", skin, "small");
		tbBack.pad(20);
		// use an anonymous inner class for the click event listener
		tbBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onBackClicked();
			}
		});
		mainTable.add(tbBack).align(Align.top + Align.right);
		mainTable.row();

		//createPowerUpButtons();
		if (isDebug) {
			showStatsWindow(true);
		}

		stage.addActor(mainTable);

		inputPlexer.addProcessor(stage);
	}

	public void createLevel() {
		Gdx.app.debug(TAG, "createLevel()");
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		isFiringLaser = isUsingAccelerometer; // no regen in this mode

		background = new Sprite(Assets.instance.levelDecorations.background);
		float backgroundWidth = 5 * -Constants.VIEWPORT_WIDTH;
		float backgroundHeight = 5 * -Constants.VIEWPORT_HEIGHT;
		background.setBounds(-backgroundWidth / 2, -backgroundHeight / 2 - 50,
				backgroundWidth, backgroundHeight);
		background.setOrigin(backgroundWidth / 2 - 50,
				backgroundHeight / 2 - 50);

		asteroids = new Array<Asteroid>();
		asteroidFactory = new AsteroidFactory(world);
		asteroidPool = new AsteroidPool(asteroidFactory);
		asteroidDeaths = new Array<AsteroidDeath>();
		comets = new Array<Comet>();
		cometDeaths = new Array<CometDeath>();
		cometFactory = new CometFactory(world);
		cometPool = new CometPool(cometFactory);
		earth = new Earth(world);
		healthGauge = new Gauge(earthHealth, -width / 2, height / 2 - 15,
				Assets.instance.ui.healthGaugeInfill);
		// laserGauge = new Gauge(laserHealth, -width / 2, height / 2 - 20,
		// Assets.instance.ui.laserGaugeInfill);
		laserDefense = new LaserDefense[4];

		moon = new Moon(world);
		createLaserDefence();
	}

	private void createLaserDefence() {
		if (laserDefense[2] == null) {
			laserDefense[2] = new LaserDefense(world,
					Constants.EARTH_RADIUS, 0, 
					90.0f);
		}
		if (laserDefense[0] == null) {
			laserDefense[0] = new LaserDefense(world, 
					0, Constants.EARTH_RADIUS - 1, 
					180);
		}
		if (laserDefense[3] == null) {
			laserDefense[3] = new LaserDefense(world,
					-Constants.EARTH_RADIUS, 0, 
					270f);
		}
		if (laserDefense[1] == null) {
			laserDefense[1] = new LaserDefense(world, 
					0, -Constants.EARTH_RADIUS - .5f, 
					0);
		}
		if (isUsingAccelerometer) {
			for (int index = 0; index < lasersActive; index++) {
				laserDefense[index].fireLaser(world, new Vector2(0, 100));
			}
		}
	}

	private void createWave(int level) {
		
		Vector2 initialPosition;
		Vector2 initialVelocity;
		initialPosition = asteroidFactory.generateInitPosisition();
		for (int index = 0; index < level * 5; index++) {
			initialVelocity = new Vector2();
			initialVelocity.x = (float) (1000 + Math.random() * 1000);
			initialVelocity.y = MyMath.getAngleBetween(initialPosition,
					new Vector2(0, 0)); // direction
			initialVelocity.y += 45 * MyMath.randomSignChange();
			initialVelocity = MyMath.getRectCoords(initialVelocity);

			Asteroid asteroid = asteroidFactory.makeAsteroid();
			asteroid.body.setTransform(initialPosition, 0);
			asteroid.body.setLinearVelocity(initialVelocity);
			/**
			 * / // for pool Filter filter =
			 * asteroid.body.getFixtureList().get(0).getFilterData();
			 * filter.categoryBits = Constants.FILTER_ASTEROID;
			 * asteroid.body.getFixtureList().get(0).setFilterData(filter); /
			 **/
			asteroids.add(asteroid);
		}
		
		for (int index = 0; index < level; index++) {
			createComet();
		}
	}
	
	private void createAsteroid(float degrees) {
		if (asteroids.size < maxAsteroids) {
			Vector2 initialPosition = asteroidFactory.generateInitPosisition();
			Vector2 initialVelocity = new Vector2();
			initialVelocity.x = (float) (1000 + Math.random() * 1000);
			initialVelocity.y = MyMath.getAngleBetween(initialPosition,
					new Vector2(0, 0)); // direction
			initialVelocity.y += degrees * MyMath.randomSignChange();
			initialVelocity = MyMath.getRectCoords(initialVelocity);

			Asteroid asteroid = asteroidFactory.makeAsteroid();
			asteroid.body.setTransform(initialPosition, 0);
			asteroid.body.setLinearVelocity(initialVelocity);
			/**
			 * / // for pool Filter filter =
			 * asteroid.body.getFixtureList().get(0).getFilterData();
			 * filter.categoryBits = Constants.FILTER_ASTEROID;
			 * asteroid.body.getFixtureList().get(0).setFilterData(filter); /
			 **/
			asteroids.add(asteroid);
		}
	}

	private void createComet() {
		if (comets.size < maxComets) {
			Comet comet = cometFactory.makeComet();
			Vector2 initPos = cometFactory.generateInitPosisition();
			comet.body.setTransform(initPos, 0);
			comet.body.setLinearVelocity(cometFactory
					.generateInitVelocity(initPos));
			/**
			 * / // for pool Filter filter =
			 * comet.body.getFixtureList().get(0).getFilterData();
			 * filter.categoryBits = Constants.FILTER_COMET;
			 * comet.body.getFixtureList().get(0).setFilterData(filter); /
			 **/
			comets.add(comet);
		}
	}

	private void createStatsWindow() {
		winStats = new Window("GameOver", skin, "big");
		Table table = new Table(skin);
		table.row();
		table.pad(10, 10, 0, 10);
		table.add(new Label("Comets destroyed: ", skin)).colspan(3);
		table.add(new Label("" + cometsDestroyed, skin));

		table.row();
		table.pad(10, 10, 0, 10);
		table.add(new Label("Asteroids destroyed: ", skin)).colspan(3);
		table.add(new Label("" + asteroidsDestroyed, skin));

		table.row();
		table.pad(10, 10, 0, 10);
		table.add(new Label("Playtime: ", skin)).colspan(3);
		table.add(new Label("" + playTime, skin));

		// mainTable.addActor(table);
		winStats.add(table);
		int winWidth = 400,
				winHeight = 100;
		
		winStats.setWidth(winWidth);
		winStats.setHeight(winHeight);
		winStats.setPosition(
				width / 2 - winWidth / 2,
				winHeight );
		stage.addActor(winStats);
		showStatsWindow(true);

	}

	private void createPowerUpButtons() {
		TextButton tbGravityWave = new TextButton("Gravity Wave", skin, "small");
		tbGravityWave.pad(10);
		// use an anonymous inner class for then click event listener
		tbGravityWave.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gravityWave();
			}
		});

		TextButton tbMoonPull = new TextButton("Moon Pull", skin, "small");
		tbMoonPull.pad(10);
		// use an anonymous inner class for then click event listener
		tbMoonPull.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				moonPull();
			}
		});

		/**
		 * TextButton tbShield = new TextButton("Shield", skin, "small");
		 * tbMoonPull.pad(10); // use an anonymous inner class for then click
		 * event listener tbMoonPull.addListener(new ClickListener() {
		 * 
		 * @Override public void clicked(InputEvent event, float x, float y) {
		 *           shield(); } });
		 **/

		tbGravityWave.setX(30);
		tbGravityWave.setY(125);

		tbMoonPull.setX(30);
		tbMoonPull.setY(100);

		/**
		 * tbShield.setX(30); tbShield.setY(150);
		 **/

		Table table = new Table(skin);
		table.add(tbGravityWave);
		table.add(tbMoonPull);
		mainTable.addActor(table);

	}

	private void showStatsWindow(boolean visible) {
		float alphaTo = visible ? 0.8f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		winStats.addAction(sequence(touchable(touchEnabled),
				alpha(alphaTo, .5f)));
	}
	
	// update
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// increment the world
		if (!isPaused) {
			world.step(Constants.TIMESTEP, Constants.VELOCITYITERATIONS,
					Constants.POSITIONITERATONS);

			updateTimers(delta);
			
			/**/// accelerometer control
			if (!isGameOver && isUsingAccelerometer) {
				// accelerometer
				for (int index = 0; index < lasersActive; index++) {
					laserTargetPolar.lerp(new Vector2(75, 90 + Gdx.input.getPitch() * 6), .1f);
					laserDefense[index].fireLaser(world, MyMath.getRectCoords(laserTargetPolar));
				}
			}
			
			
		}

		cameraHelper.update(delta);
		cameraHelper.applyTo(gameCamera);
		renderUI(delta);
		renderGame(delta);

		if (isDebug) {
			debugRenderer.render(world, gameCamera.combined);
		}

		destroyQueue();
	}
	
	private void updateTimers(float delta) {
		playTime++;
		
		// make wave
		waveTimer += delta;
		if (waveTimer > waveInterval) {
			waveTimer = 0;
			if (score > 1000) {
				level = 4 + score / 1000;
			} else if (score > 500) {
				level = 3;
			} else if (score > 100) {
				level = 2;
			} 
			createWave(level);
		}
		// bonus
		lblBonus.setText("Bonus: " + bonusCount);
		bonusTimer += delta;
		// end the bonus count 1 second after nothing is destroyed
		if (bonusTimer > 1.0f) {
			if (bonusCount >= 10 && !isBonusAnimationPlaying) {
				isBonusAnimationPlaying = true;
				lblBonus.setColor(Color.YELLOW);
				earthHealth += bonusCount;
				if (earthHealth > 100) {
					earthHealth = 100;
				}
			} else {
				bonusCount = 0;
			}
			if (isBonusAnimationPlaying) {
				animateBonus(delta);
			} 
		}
		
		/** /
		cometTimer += delta;
		if (cometTimer > cometInterval) {
			cometTimer = 0;
			createComet();
		}

		asteroidTimer += delta;
		if (asteroidTimer > asteroidInterval) {
			asteroidTimer = 0;
			createAsteroid(15);
		}
		/**/
	}

	private void renderGame(float delta) {
		// ** render game elements
		spriteBatch.setProjectionMatrix(gameCamera.combined);
		spriteBatch.begin();

		backgroundRotation += (delta / 4);
		background.setRotation(backgroundRotation);
		background.draw(spriteBatch);

		earth.render(spriteBatch, delta);
		moon.update(delta);
		moon.render(spriteBatch);

		// update and draw asteroid
		for (Asteroid asteroid : asteroids) {
			if (!isPaused) {
				asteroid.update(delta);
			}
			asteroid.render(spriteBatch);
		}

		for (AsteroidDeath asteroidDeath : asteroidDeaths) {
			asteroidDeath.render(spriteBatch, delta);
			if (asteroidDeath.shouldDestroy()) {
				asteroidDeaths.removeValue(asteroidDeath, true);
			}
		}

		for (Comet comet : comets) {
			if (!isPaused)
				comet.update(delta);
			comet.render(spriteBatch, delta);

		}

		for (CometDeath cometDeath : cometDeaths) {
			cometDeath.render(spriteBatch, delta);
			if (cometDeath.shouldDestroy()) {
				cometDeaths.removeValue(cometDeath, true);
			}
		}

		laserDefense[0].render(spriteBatch);
		laserDefense[1].render(spriteBatch);
		laserDefense[2].render(spriteBatch);
		laserDefense[3].render(spriteBatch);

		spriteBatch.end();
	}

	private void renderUI(float delta) {
		stage.act(delta);
		stage.draw();
		if (isDebug) {
			Table.drawDebug(stage);
		}
		spriteBatch.setProjectionMatrix(guiCamera.combined);
		spriteBatch.begin();

		lblScore.setText("Score: " + score);

		healthGauge.render(spriteBatch, earthHealth);
		// laserGauge.render(spriteBatch, laserHealth);


		if (GamePreferences.instance.showFpsCounter) {
			int fps = Gdx.graphics.getFramesPerSecond();
			float x = -width / 2;
			float y = -height / 2;
			BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
			if (fps >= 45) {
				fpsFont.setColor(Color.GREEN);
			} else if (fps >= 30) {
				fpsFont.setColor(Color.YELLOW);
			} else {
				fpsFont.setColor(Color.RED);
			}
			fpsFont.draw(spriteBatch, "FPS: " + fps, x + 50, y + 50);
			fpsFont.setColor(Color.WHITE);
		}

		if (isDebug) {
			renderDebugStats();
		}

		spriteBatch.end();
	}

	private void renderDebugStats() {
		if (isDebug) {
			// draw number of asteroids in play, at bottom right
			Assets.instance.fonts.defaultNormal.draw(spriteBatch,
					"Asteroids In Play: " + asteroids.size,
					-(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 15);
			// draw number of comets in play
			Assets.instance.fonts.defaultNormal.draw(spriteBatch,
					"Comets In Play: " + comets.size,
					-(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 30);
			// draw play time
			Assets.instance.fonts.defaultNormal.draw(spriteBatch, "Playtime: "
					+ (int) playTime, -(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 45);

			// accelerometer
			Assets.instance.fonts.defaultNormal.draw(spriteBatch, "Roll: "
					+ Gdx.input.getRoll(),
					-(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 60);
			Assets.instance.fonts.defaultNormal.draw(spriteBatch, "Rotation: "
					+ Gdx.input.getRotation(),
					-(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 75);
			Assets.instance.fonts.defaultNormal.draw(spriteBatch, "Pitch: "
					+ Gdx.input.getPitch(),
					-(Constants.VIEWPORT_GUI_WIDTH / 2) + 10,
					-(Constants.VIEWPORT_GUI_HEIGHT / 2) + 90);
		}
	}

	// events
	private void onGameOver() {
		isGameOver = true;
		earth.activateShield(-1);		// disable shield
		createStatsWindow();
		stopLasers();
	}

	private void onBackClicked() {
		ScreenTransition transition = ScreenTransitionFade.init(.75f);
		// (duration, direction, slideOut, easing)
		game.setScreen(new MainMenu(game), transition);
	}

	// collisions
	public void collisionLaserAsteroid(Asteroid asteroid) {
		if (!asteroid.isDead) {
			asteroid.isDead = true;
			score++;
			asteroidsDestroyed++;
			laserHealth += asteroid.radius;
			if (laserHealth > 100)
				laserHealth = 100;
			
			bonusCount++;
			bonusTimer = 0.0f;

			destroyAsteroid(asteroid);
		}
	}

	public void collisionAsteroidEarth(Asteroid asteroid) {
		if (!asteroid.isDead) {
			asteroid.isDead = true;
			if (!isGameOver) {
				earthDamage(asteroid.radius * 5);
				earth.activateShield(.75f);
			}
			destroyAsteroid(asteroid);
		}
	}

	public void collisionLaserComet(Comet comet) {
		if (!comet.isDead) {
			comet.isDead = true;
			score += 10;
			cometsDestroyed++;
			laserHealth += 10;
			if (laserHealth > 100)
				laserHealth = 100;
			
			bonusCount += 3;
			bonusTimer = 0.0f;
			
			destroyComet(comet);
		}
	}

	public void collisionCometEarth(Comet comet) {
		if (!comet.isDead) {
			comet.isDead = true;
			if (!isGameOver) {
				earthDamage(33);
				earth.activateShield(.75f);
			}
			destroyComet(comet);
		}
	}

	public void collisionCometMoon(Comet comet) {
		if (!comet.isDead) {
			comets.removeValue(comet, true);
			cometDeaths.add(new CometDeath(comet.body.getPosition(), comet
					.getColor()));
			// cometPool.free(comet);
			destroyComet(comet);
		}
	}

	public void earthDamage(float amount) {
		earthHealth -= amount;
		if (earthHealth < 0) {
			earthHealth = 0;
			onGameOver();
		}
		// Gdx.input.vibrate(250);
	}

	// earth powers
	public void gravityWave() {
		Gdx.app.debug(TAG, "gravityWave()");
		// force vector polar
		Vector2 forceVector = new Vector2();

		for (Asteroid asteroid : asteroids) {
			forceVector.x = 500000
					* asteroid.body.getMass()
					/ MyMath.getDistanceBetween(asteroid.body.getPosition(),
							new Vector2(0, 0));
			forceVector.y = 180 + MyMath.getAngleBetween(
					asteroid.body.getPosition(), new Vector2(0, 0));

			asteroid.body.applyForceToCenter(MyMath.getRectCoords(forceVector),
					false);
		}
		for (Comet comet : comets) {
			forceVector.x = 500000
					* comet.body.getMass()
					/ MyMath.getDistanceBetween(comet.body.getPosition(),
							new Vector2(0, 0));
			forceVector.y = 180 + MyMath.getAngleBetween(
					comet.body.getPosition(), new Vector2(0, 0));
			comet.body.applyForceToCenter(MyMath.getRectCoords(forceVector),
					false);
		}
	}

	public void moonPull() {
		Gdx.app.debug(TAG, "moonPull()");
		// force vector polar
		Vector2 forceVector = new Vector2();

		int range = 200;

		for (Asteroid asteroid : asteroids) {
			float distance = MyMath.getDistanceBetween(
					asteroid.body.getPosition(), moon.body.getPosition());
			if (distance < range) {
				forceVector.x = 500000 / distance;
				forceVector.y = MyMath.getAngleBetween(
						asteroid.body.getPosition(), moon.body.getPosition());
				asteroid.body.setLinearVelocity(MyMath
						.getRectCoords(forceVector));
			}
		}
		for (Comet comet : comets) {
			float distance = MyMath.getDistanceBetween(
					comet.body.getPosition(), moon.body.getPosition());
			if (distance < range) {
				forceVector.x = 500000 / distance;
				forceVector.y = MyMath.getAngleBetween(
						comet.body.getPosition(), moon.body.getPosition());
				comet.body.setLinearVelocity(MyMath.getRectCoords(forceVector));
			}
		}
	}

	public void stopLasers() {
		for (int index = 0; index < lasersActive; index++) {
			if (laserDefense[index].laser != null) {
				destroyBody(laserDefense[index].laser.body);
				laserDefense[index].stopLaser();
			}
		}
	}

	// animate
	public void animateBonus(float delta) {
		bonusAnimationStateTime += delta;
		if (bonusAnimationStateTime < bonusAnimationLength) {
			lblBonus.setFontScale(1 + bonusAnimationStateTime);
		} else {
			bonusAnimationStateTime = 0.0f;
			lblBonus.setColor(Color.WHITE);
			lblBonus.setFontScale(1);
			isBonusAnimationPlaying = false;
			bonusTimer = 0.0f;
			bonusCount = 0;
		}
	}
	
	// destroy stuff
	public void destroyBody(Body body) {
		// be sure the body you are trying to destroy is not already in the
		// queue
		if (!destroyQueue.contains(body))
			destroyQueue.add(body);
	}

	private void destroyQueue() {
		if (!destroyQueue.isEmpty()) {
			Gdx.app.debug(TAG, "destroy(): destroying queue");
			for (Body body : destroyQueue) {
				world.destroyBody(body);
			}
			destroyQueue.clear();
		}
	}

	public void destroyAsteroid(Asteroid asteroid) {
		Gdx.app.debug(TAG, "destroyAsteroid()");
		try {
			// asteroid.body.setTransform(new Vector2(100, 0), 0);
			asteroidDeaths.add(new AsteroidDeath(asteroid.getBody()
					.getPosition()));
			asteroids.removeValue(asteroid, true);
			destroyBody(asteroid.body);
			// asteroidPool.free(asteroid);
		} catch (IndexOutOfBoundsException ex) {
			Gdx.app.error(TAG, "destroyAsteroid() : not in list");
		}

	}

	public void destroyComet(Comet comet) {
		Gdx.app.debug(TAG, "destroyComet()");
		try {
			cometDeaths.add(new CometDeath(comet.body.getPosition(), comet
					.getColor()));
			comets.removeValue(comet, true);
			destroyBody(comet.body);
			// cometPool.free(comet);
		} catch (IndexOutOfBoundsException ex) {
			Gdx.app.error(TAG, "destroyComet() : not in list");
		}

	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.debug(TAG, "resize() - " + width + " " + height);
		this.width = width;
		this.height = height;
		stage.setViewport(width, height, true);
		mainTable.invalidateHierarchy();
	}

	@Override
	public void hide() {
		Gdx.app.debug(TAG, "hide()");
		// pause();
	}

	@Override
	public void pause() {
		Gdx.app.debug(TAG, "pause()");
		isPaused = true;
	}

	@Override
	public void resume() {
		Gdx.app.debug(TAG, "resume()");
		isPaused = false;
		//Assets.instance.init(new AssetManager());
	}

	@Override
	public void dispose() {
		Gdx.app.debug(TAG, "dispose()");
		world.dispose();
		debugRenderer.dispose();
		spriteBatch.dispose();
		stage.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return inputPlexer;
	}

}
