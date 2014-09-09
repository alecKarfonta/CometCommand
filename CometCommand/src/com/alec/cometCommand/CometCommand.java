package com.alec.cometCommand;

import com.alec.cometCommand.controllers.AudioManager;
import com.alec.cometCommand.models.Assets;
import com.alec.cometCommand.views.DirectedGame;
import com.alec.cometCommand.views.MainMenu;
import com.alec.cometCommand.views.transitions.ScreenTransition;
import com.alec.cometCommand.views.transitions.ScreenTransitionFade;
import com.badlogic.gdx.assets.AssetManager;

public class CometCommand extends DirectedGame {
		
		private GoogleInterface platformInterface;
			
		public CometCommand () {
			
		}
		
		public CometCommand (GoogleInterface aInterface) {
			platformInterface = aInterface;
			platformInterface.Login();
			platformInterface.showAds(true);
		}
	
		@Override
		public void create() {
			// Load assets
			Assets.instance.init(new AssetManager());
			GamePreferences.instance.load();
			AudioManager.instance.play(Assets.instance.music.intro);
			ScreenTransition transition = 
					ScreenTransitionFade.init(1.25f);

			setScreen(new MainMenu(this), transition);
		}
	}