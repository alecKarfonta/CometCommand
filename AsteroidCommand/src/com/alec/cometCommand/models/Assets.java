package com.alec.cometCommand.models;

import com.alec.cometCommand.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();	// singleton
	private AssetManager assetManager;
	private TextureAtlas atlas;
	
	private Assets() {}
	
	public Skin skin = new Skin(Gdx.files.internal(Constants.SKIN_MENU_UI),
			new TextureAtlas(Constants.TEXTURE_ATLAS_MENU_UI));

	public AssetFonts fonts;
	public AssetEarth earth;
	public AssetMoon moon;
	public AssetAsteroids asteroids;
	public AssetLasers lasers;
	public AssetAliens aliens;
	public AssetSounds sounds;
	public AssetMusic music;
	public AssetUI ui;
	public AssetLevelDecorations levelDecorations;
	
	public void init(AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.setErrorListener(this);		
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
		assetManager.load("sounds/laser.wav", Sound.class);
		assetManager.load("sounds/asteroidDeath.wav", Sound.class);
		assetManager.load("music/intro.ogg", Music.class);
		assetManager.finishLoading();
		
		// load the sounds
		
		// log all the assets that were loaded
		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String asset : assetManager.getAssetNames()) {
			Gdx.app.debug(TAG, "asset: " + asset);
		}
		
		// load the texture atlas
		atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
		
		// enable texture filtering
		for (Texture texture : atlas.getTextures()) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		// create the game resources (inner Asset~ classes)
		fonts = new AssetFonts();
		earth = new AssetEarth(atlas);
		moon = new AssetMoon(atlas);
		asteroids = new AssetAsteroids(atlas);
		levelDecorations = new AssetLevelDecorations(atlas);
		lasers = new AssetLasers(atlas);
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);
		ui = new AssetUI(atlas);
		//aliens = new AssetAliens(atlas);
	}
	
	public class AssetLevelDecorations {
		public AtlasRegion background;
		
		public  AssetLevelDecorations (TextureAtlas atlas) {
			background = atlas.findRegion("itsFullOfStars");
		}
	}

	public class AssetUI {
		public AtlasRegion healthGaugeBorder, healthGaugeInfill, laserGaugeInfill;
		public AtlasRegion slider, sliderBar, sliderVert, sliderBarVert;
		public AssetUI (TextureAtlas atlas) {
			healthGaugeBorder = atlas.findRegion("healthGaugeBorder");
			healthGaugeInfill= atlas.findRegion("healthGaugeInfill");
			laserGaugeInfill = atlas.findRegion("laserGaugeInfill");			
		}
	}
	
	public class AssetSounds {
		public final Sound laser;
		public final Sound asteroidDeath;

		public AssetSounds (AssetManager am) {
			laser = am.get("sounds/laser.wav", Sound.class);
			laser.setLooping(0, true);
			asteroidDeath = am.get("sounds/asteroidDeath.wav", Sound.class);
		}
	}
	
	public class AssetMusic {
		public final Music intro;
		
		public AssetMusic (AssetManager am) {
			intro = am.get("music/intro.ogg", Music.class);
			intro.setLooping(true);
			
		}
	}
	
	public class AssetEarth {
		public final AtlasRegion currFrame;
		public final AtlasRegion shield;
		
		public AssetEarth (TextureAtlas atlas) {
			currFrame = atlas.findRegion("earth");
			shield = atlas.findRegion("earthShield");
			/** /		// animation
			Array<AtlasRegion> regions = null;
			regions = atlas.findRegions("earth/earth");
			fullHealth = new Animation(1.0f / 15.0f, regions, Animation.LOOP);
			regions = atlas.findRegions("earth/damagePhase3/earthDamagePhase3");
			damagePhase3 = new Animation(1.0f / 15.0f, regions, Animation.LOOP);
			currFrame = regions.first();
			/**/
			//currFrame = atlas.findRegion("earth/earth");
		}
	}
	
	public class AssetMoon {
		public final AtlasRegion moon;
		public AssetMoon(TextureAtlas atlas) {
			moon = atlas.findRegion("moon");
		}
	}
	
	public class AssetAsteroids {
		public final AtlasRegion asteroidVesta;
		
		public AssetAsteroids (TextureAtlas atlas) {
			asteroidVesta = atlas.findRegion("asteroidVesta");	// an actual image of an asteroid lol
		}
	}
	
	public class AssetLasers {
		public final AtlasRegion laser1Blue;
		public final AtlasRegion laser1Red;
		public final AtlasRegion laser1Yellow;
		public final AtlasRegion laser1Purple;
		public final AtlasRegion laser1Teal;
		public final AtlasRegion laser1Green;
		
		public AssetLasers (TextureAtlas atlas) {
			laser1Blue = atlas.findRegion("laser1Blue");
			laser1Red = atlas.findRegion("laser1Red");
			laser1Yellow = atlas.findRegion("laser1Yellow");
			laser1Purple = atlas.findRegion("laser1Purple");
			laser1Teal = atlas.findRegion("laser1Teal");
			laser1Green = atlas.findRegion("laser1Green");
		}
	}
	
	public class AssetAliens {
		public final AtlasRegion smallRedShip;
		
		public AssetAliens (TextureAtlas atlas) {
			smallRedShip = atlas.findRegion("aliens/smallRedShip");
		}
	}
	
	public class AssetFonts {
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		
		public AssetFonts () {
			defaultSmall = new BitmapFont(Gdx.files.internal("fonts/white16.fnt"), false);
			defaultNormal = new BitmapFont(Gdx.files.internal("fonts/white32.fnt"), false);
			
			defaultSmall.setScale(1f);
			defaultNormal.setScale(.5f);
			
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}
	
	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset: '" + asset.fileName + "' " + (Exception)throwable);
	}

	@Override
	public void dispose() {
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
	}
	
	
}
