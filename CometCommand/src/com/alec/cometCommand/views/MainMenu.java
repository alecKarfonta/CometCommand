package com.alec.cometCommand.views;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.touchable;

import com.alec.cometCommand.Constants;
import com.alec.cometCommand.GamePreferences;
import com.alec.cometCommand.controllers.AudioManager;
import com.alec.cometCommand.models.Assets;
import com.alec.cometCommand.views.transitions.ScreenTransition;
import com.alec.cometCommand.views.transitions.ScreenTransitionFade;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class MainMenu extends AbstractGameScreen {
	private Stage stage;
	private Table mainTable;
	private float backgroundRotation;
	private Sprite background;
	private SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private boolean isDebug = false;

	// options pop up
	private Window winOptions;
	private TextButton btnWinOptSave, btnWinOptCancel;
	private CheckBox chkSound, chkMusic, chkShowFpsCounter,
			chkUseMonochromeShader, chkUseAccelerometer;
	private Slider sldSound, sldMusic;

	// credits pop up
	private Window winCredits;
	private ScrollPane spCredits;
	
	public MainMenu(DirectedGame game) {
		super(game);
	}

	@Override
	public void render(float delta) {
		// clear the screen with black
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		stage.act(delta);
		stage.draw();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();

		// background
		backgroundRotation += (delta / 5);
		background.setRotation(backgroundRotation);
		background.draw(spriteBatch);

		spriteBatch.end();
	}

	@Override
	public void show() {
		background = new Sprite(Assets.instance.levelDecorations.background);
		float backgroundWidth = 2 * -Constants.VIEWPORT_WIDTH;
		float backgroundHeight = 2 * -Constants.VIEWPORT_HEIGHT;
		background.setBounds(-backgroundWidth / 2, -backgroundHeight / 2,
				backgroundWidth, backgroundHeight);
		background.setOrigin(backgroundWidth / 2 + 100,
				backgroundHeight / 2 - 50);

		// create a new stage object to hold all of the other objects
		stage = new Stage();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,
				Constants.VIEWPORT_HEIGHT);
		spriteBatch = new SpriteBatch();

		// create a new table the size of the window
		mainTable = new Table(Assets.instance.skin);
		mainTable.setFillParent(true);
		Table layerOptionsWindow = buildOptionsWindowLayer();
		//Table layerCreditsWindow = buildCreditsWindowLayer();
		// assemble stage for menu screen
		stage.clear();
		stage.addActor(mainTable);
		stage.addActor(layerOptionsWindow);
		//stage.addActor(layerCreditsWindow);
		

		showOptionsWindow(false, false); // initially hide the options pane

		// create a heading
		Label heading = new Label("Comet Command", Assets.instance.skin, "big");
		heading.scale(3);

		/*
		 * create some buttons
		 */
		TextButton tbPlay = new TextButton("Play", Assets.instance.skin, "big");
		// use an anonymous inner class for then click event listener
		tbPlay.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onPlayClicked();
			}
		});
		TextButton tbSettings = new TextButton("Settings", Assets.instance.skin, "big");
		tbSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onOptionsClicked();
			}
		});
		TextButton tbExit = new TextButton("Exit", Assets.instance.skin);

		tbExit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		tbExit.padLeft(90);
		tbExit.padRight(90);
		tbPlay.padLeft(90);
		tbPlay.padRight(90);
		// add everything to the table
		mainTable.add(heading).spaceBottom(100);
		mainTable.row(); // increments the row so all new actors are placed in
							// the
							// new row
		mainTable.add(tbPlay).padBottom(15);
		mainTable.row();
		mainTable.add(tbSettings).padBottom(15);
		mainTable.row();
		mainTable.add(tbExit);

		// add the table to the stage
		stage.addActor(mainTable);

	}

	private void showOptionsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled),
				alpha(alphaTo, duration)));
	}

	private void showCreditsWindow(boolean visible, boolean animated) {
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled
				: Touchable.disabled;
		winCredits.addAction(sequence(touchable(touchEnabled),
				alpha(alphaTo, duration)));
	}
	
	private Table buildOptionsWindowLayer() {
		winOptions = new Window("Options", Assets.instance.skin);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();
		// + Character Assets.instance.skin: Selection Box (White, Gray, Brown)
		winOptions.add(buildOptWinInputSelection()).row();
		// + Debug: Show FPS Counter
		if (isDebug) {
			winOptions.add(buildOptWinDebug()).row();
		}
		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);
		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showOptionsWindow(false, false);
		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();
		// Move options window to bottom right corner
		winOptions.setPosition(
				Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50, 50);
		return winOptions;
	}

	private Table buildOptWinAudioSettings() {
		Table tbl = new Table();
		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", Assets.instance.skin, "fontWhite16", Color.ORANGE)).colspan(
				3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", Assets.instance.skin);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", Assets.instance.skin));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, Assets.instance.skin);
		tbl.add(sldSound);
		tbl.row();
		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", Assets.instance.skin);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", Assets.instance.skin));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, Assets.instance.skin);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;
	}

	private Table buildOptWinInputSelection() {
		Table table = new Table();
		// + Title: "Character Assets.instance.skin"
		table.pad(10, 10, 0, 10);
		table.add(new Label("Input Mode", Assets.instance.skin, "fontWhite16", Color.ORANGE))
				.colspan(2);

		table.columnDefaults(0).padRight(10);
		table.columnDefaults(1).padRight(10);
		table.row();
		table.add(new Label("Use Accelerometer", Assets.instance.skin, "default"));
		chkUseAccelerometer = new CheckBox("", Assets.instance.skin);
		chkUseAccelerometer.getCells().get(0).size(20);
		table.add(chkUseAccelerometer);

		return table;
	}

	// debug options
	private Table buildOptWinDebug() {
		Table tbl = new Table();
		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", Assets.instance.skin, "fontWhite16", Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);
		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", Assets.instance.skin);
		tbl.add(new Label("Show FPS Counter", Assets.instance.skin));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		// + Checkbox, "Use Monochrome Shader" label chkUseMonochromeShader =
		chkUseMonochromeShader = new CheckBox("", Assets.instance.skin);
		tbl.add(new Label("Use Monochrome Shader", Assets.instance.skin));
		tbl.add(chkUseMonochromeShader);
		tbl.row();
		return tbl;
	}

	private Table buildOptWinButtons() {
		Table tbl = new Table();
		// + Separator
		Label lbl = null;
		lbl = new Label("", Assets.instance.skin);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = Assets.instance.skin.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", Assets.instance.skin);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = Assets.instance.skin.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();
		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", Assets.instance.skin);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onSaveClicked();
			}
		});
		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", Assets.instance.skin);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onCancelClicked();
			}
		});
		return tbl;
	}

	private Table buildCreditsWindowLayer() {
		winCredits = new Window("Credits", Assets.instance.skin);

		Table table = new Table();
		table.pad(10, 10, 10, 10);
		
		StringBuilder credits = new StringBuilder();
		credits.append("Thanks to:");
		credits.append("everybody at the Libgdx forum badlogicgame.com/forum/ \n\n");
		credits.append("everybody at the Bodx2d forum http://www.box2d.org/forum/ \n\n ");
		credits.append("iForce2d for his wonderful Box2d tutorials http://www.iforce2d.net/b2dtut/ \n\n ");
		credits.append("Ove Melaa for the amazing background music, despite the title we did not cordinate the: \n");
		credits.append("'Earth Is All We Have' written and produced by Ove Melaa (Omsofware@hotmail.com) -2013 Ove Melaa \n\n");
		credits.append("dklon for the explosion sound effect great pack: \n");
		credits.append("http://opengameart.org/content/boom-pack-1");
		
		final Label lblCredits = new Label(credits.toString(), Assets.instance.skin);
		lblCredits.setAlignment(Align.center);
		lblCredits.setWrap(true);
		
		table.setFillParent(true);
		table.add(lblCredits);

		ScrollPane spCredits = new ScrollPane(table);
		winCredits.add(spCredits);
		
		// Make options window slightly transparent
		winCredits.setColor(1, 1, 1, 0.8f);
		// Hide options window by default
		showCreditsWindow(false, false);
		// Let TableLayout recalculate widget sizes and positions
		winCredits.pack();
		// Move options window to bottom right corner
		winCredits.setPosition(
				-Constants.VIEWPORT_GUI_WIDTH + winOptions.getWidth() - 50, 50);
		return winCredits;
	}
	
	// user input convenience methods
	private void onPlayClicked() {
		// fade to the play screen in .75 seconds
		ScreenTransition transition = ScreenTransitionFade.init(0.75f);
		game.setScreen(new Level1(game), transition);
	}

	private void onOptionsClicked() {
		loadSettings();
		// showMenuButtons(false);
		showOptionsWindow(true, true);
	}

	private void onSaveClicked() {
		saveSettings();
		onCancelClicked();
		AudioManager.instance.onSettingsUpdated();
	}

	private void onCancelClicked() {
		// showMenuButtons(true);
		showOptionsWindow(false, true);
		AudioManager.instance.onSettingsUpdated();
	}

	private void loadSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		chkUseAccelerometer.setChecked(prefs.useAccelerometer);

		// debug
		if (isDebug) {
			chkUseMonochromeShader.setChecked(prefs.useMonochromeShader);
			chkShowFpsCounter.setChecked(prefs.showFpsCounter);
		}
	}

	private void saveSettings() {
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.useAccelerometer = chkUseAccelerometer.isChecked();

		// debug
		if (isDebug) {
			prefs.showFpsCounter = chkShowFpsCounter.isChecked();
			prefs.useMonochromeShader = chkUseMonochromeShader.isChecked();
		}
		prefs.save();
	}

	@Override
	public void resize(int width, int height) {
		// set the view to the new window width and height
		stage.setViewport(width, height, true);
		// invalidate the table hierarchy for it to reposition elements
		mainTable.invalidateHierarchy();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		stage.dispose();
		spriteBatch.dispose();
	}

	@Override
	public InputProcessor getInputProcessor() {
		return stage;
	}

}
