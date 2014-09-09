package com.alec.cometCommand;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AndroidApplication implements GoogleInterface {

	protected AdView adView;

	private final int SHOW_ADS = 1;
	private final int HIDE_ADS = 0;

	private boolean isFree = true;
	
	private long lastAdTime;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ADS: {
				adView.setVisibility(View.VISIBLE);
				break;
			}
			case HIDE_ADS: {
				adView.setVisibility(View.GONE);
				break;
			}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the layout
		RelativeLayout layout = new RelativeLayout(this);

		// Do the stuff that initialize() would do for you
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		// Create the libgdx View
		View gameView = initializeForView(new CometCommand(this), true);

		// Create and setup the AdMob view
		adView = new AdView(this); // Put in your secret key here
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-3051564686435793/3178786062");
		adView.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				lastAdTime = System.currentTimeMillis();
			}
		});
		adView.setBackgroundColor(Color.TRANSPARENT);
		if (isFree) {
			AdRequest adRequest = new AdRequest.Builder().addTestDevice(
					AdRequest.DEVICE_ID_EMULATOR) // Emulator
					// .addTestDevice("MY DEVICE ID") // My Nexus test
					.build();

			adView.loadAd(adRequest);
			// Add the libgdx view
			layout.addView(gameView);
		}

		// Add the AdMob view
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		layout.addView(adView, adParams);

		// Hook it all up
		setContentView(layout);

		/**
		 * / AndroidApplicationConfiguration cfg = new
		 * AndroidApplicationConfiguration(); cfg.useGL20 = true; initialize(new
		 * AsteroidCommand(), cfg); /
		 **/
	}

	@Override
	public void showAds(boolean show) {
		handler.sendEmptyMessage(show ? SHOW_ADS : HIDE_ADS);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
		// aHelper.onStop();
	}

	@Override
	public void onActivityResult(int request, int response, Intent data) {
		super.onActivityResult(request, response, data);
	}

	/**/// google interface methods
	@Override
	public void Login() {
		// TODO Auto-generated method stub

	}

	@Override
	public void LogOut() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getSignedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void submitScore(int score) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getScores() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getScoresData() {
		// TODO Auto-generated method stub

	}
}
