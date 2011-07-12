package com.axelby.podax;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayerActivity {
	public static void injectPlayerFooter(final Activity activity) {
		injectView(activity, R.layout.player);

		final TextView _podcastTitle = (TextView) activity.findViewById(R.id.podcasttitle);
		final TextView _position = (TextView) activity.findViewById(R.id.position);
		final ImageButton _pausebtn = (ImageButton) activity.findViewById(R.id.pausebtn);
		final ImageButton _showplayerbtn = (ImageButton) activity.findViewById(R.id.showplayer);
		
		_pausebtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				PodaxApp app = PodaxApp.getApp();
				if (app.isPlaying()) {
					app.pause();
					_pausebtn.setImageResource(android.R.drawable.ic_media_play);
				} else {
					app.play();
					_pausebtn.setImageResource(android.R.drawable.ic_media_pause);
				}
			}
		});
		
		_showplayerbtn.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(activity, PlayerControlActivity.class);
				activity.startActivity(intent);
			}
		});
		
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				PodaxApp app = PodaxApp.getApp();
				Podcast _podcast = app.getActivePodcast();
				
				if (_podcast == null) {
					_podcastTitle.setText("");
					_position.setText("");
					_showplayerbtn.setEnabled(false);
				} else {
					_podcastTitle.setText(_podcast.getTitle());
					_pausebtn.setImageResource(app.isPlaying() ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);
					_showplayerbtn.setEnabled(true);
					_position.setText(PlayerService.getPositionString(app.getDuration(), app.getPosition()));
				}

				handler.postDelayed(this, 400);
			}
		}, 200);
	}

	private static View injectView(Activity activity, int resource) {
		View mainView = activity.findViewById(android.R.id.content);
		ViewGroup mainParent = (ViewGroup)mainView.getParent();
		int mainIndex = mainParent.indexOfChild(mainView);
		mainParent.removeView(mainView);
		
		LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View outerView = inflater.inflate(resource, null);
		
		// inject old view into new view
		View container = outerView.findViewById(R.id.view);
		ViewGroup containerGroup = (ViewGroup) container.getParent();
		int index = containerGroup.indexOfChild(container);
		containerGroup.removeView(container);
		containerGroup.addView(mainView, index);
		mainParent.addView(outerView, mainIndex);

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		lp.addRule(RelativeLayout.ABOVE, R.id.footer);
		mainView.setLayoutParams(lp);
		
		return mainParent;
	}

	static String getTimeString(int milliseconds) {
		int seconds = milliseconds / 1000;
		final int SECONDSPERHOUR = 60 * 60;
		final int SECONDSPERMINUTE = 60;
		int hours = seconds / SECONDSPERHOUR;
		int minutes = seconds % SECONDSPERHOUR / SECONDSPERMINUTE;
		seconds = seconds % SECONDSPERMINUTE;
		
		StringBuilder builder = new StringBuilder();
		if (hours > 0) {
			builder.append(hours);
			builder.append(":");
			if (minutes < 10)
				builder.append("0");
		}
		builder.append(minutes);
		builder.append(":");
		if (seconds < 10)
			builder.append("0");
		builder.append(seconds);
		return builder.toString();
	}

}