package com.alec.cometCommand;

public class Constants {
	public static final String name = "AsteroidCommand";
	public static final float TIMESTEP = 1 / 60f;	// 60 fps
	public static final int VELOCITYITERATIONS = 8;
	public static final int POSITIONITERATONS = 3;
	// paths
	public static final String TEXTURE_ATLAS_OBJECTS = "images/" + name + ".pack";
	public static final String PREFERENCES = "default.prefs";
	public static final String TEXTURE_ATLAS_MENU_UI = "ui/uiskin.atlas";
	public static final String SKIN_MENU_UI = "ui/uiskin.json";
	public static final float VIEWPORT_WIDTH = 128f;
	public static final float VIEWPORT_HEIGHT = 72f;
	public static final float VIEWPORT_GUI_WIDTH = 1280.0f;
	public static final float VIEWPORT_GUI_HEIGHT = 720.0f;

	// collision filter bits
	public final static short FILTER_NONE = 0x0000;
	public final static short FILTER_EARTH = 0x0001;
	public final static short FILTER_ASTEROID = 0x0002;
	public final static short FILTER_LASER = 0x0003;
	public final static short FILTER_EXPLOSION = 0x0004;
	public final static short FILTER_COMET = 0x0005;
	public final static short FILTER_ALIENSHIP = 0x0006;
	
	// object properties
	public static final float EARTH_RADIUS = 10.0f;
	public static final float SMALL_ASTEROID_RADIUS = 1.0f;
	public static final float SMALL_COMET_RADIUS = 1f;
	public static float LASERDEFENSE_BASEWIDTH = 1.0f;
	public static float LASERDEFENSE_BASEHEIGHT = .5f;
	public static float LASERDEFENSE_TURRET_RADIUS = 1.0f;
	
	// settings
	public static String INPUTMODES [] = {"Click", "Accelerometer"};
}
