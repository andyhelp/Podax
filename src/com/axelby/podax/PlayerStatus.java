package com.axelby.podax;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class PlayerStatus {

	// internal event bus and state
	private static PlayerStatus _current;

	public static PlayerStatus getCurrentState() {
		return _current;
	}

	public static boolean isPlaying() {
		if (_current == null)
			return false;
		return _current.getState() == PlayerStates.PLAYING;
	}

	public static boolean isPaused() {
		if (_current == null)
			return false;
		return _current.getState() == PlayerStates.PAUSED;
	}

	public static boolean isStopped() {
		if (_current == null)
			return false;
		return _current.getState() == PlayerStates.STOPPED;
	}

	public static void updateState(PlayerStates state) {
		if (_current == null)
			return;
		_current._state = state;
	}

	public static void updatePosition(int position) {
		if (_current == null)
			return;
		_current._position = position;
	}

	public static void updateQueueEmpty() {
		_current = new PlayerStatus();
	}

	public static void refresh(Context context) {
		_current = null;
		initialize(context);
		Helper.updateWidgets(context);
	}

	public static void initialize(Context context) {
		if (_current != null)
			return;

		String[] projection = {
				PodcastProvider.COLUMN_ID,
				PodcastProvider.COLUMN_TITLE,
				PodcastProvider.COLUMN_SUBSCRIPTION_ID,
				PodcastProvider.COLUMN_SUBSCRIPTION_TITLE,
				PodcastProvider.COLUMN_LAST_POSITION,
				PodcastProvider.COLUMN_DURATION,
		};
		Uri activeUri = Uri.withAppendedPath(PodcastProvider.URI, "active");
		Cursor cursor = context.getContentResolver().query(activeUri, projection, null, null, null);
		_current = new PlayerStatus();
		if (cursor.moveToNext()) {
			PodcastCursor podcast = new PodcastCursor(cursor);
			_current._state = PlayerStates.STOPPED;
			_current._id = podcast.getId();
			_current._subscriptionId = podcast.getSubscriptionId();
			_current._title = podcast.getTitle();
			_current._subscriptionTitle = podcast.getSubscriptionTitle();
			_current._position = podcast.getLastPosition();
			_current._duration = podcast.getDuration();
		}
		cursor.close();
	}

	// instance variables
	public enum PlayerStates {
		QUEUEEMPTY, STOPPED, PAUSED, PLAYING
	}

	private PlayerStates _state;
	private long _id;
	private long _subscriptionId;
	private int _position;
	private int _duration;
	private String _title;
	private String _subscriptionTitle;

	private PlayerStatus() {
		_state = PlayerStates.QUEUEEMPTY;
	}

	public PlayerStates getState() {
		return _state;
	}

	public long getId() {
		return _id;
	}

	public long getSubscriptionId() {
		return _subscriptionId;
	}

	public int getPosition() {
		return _position;
	}

	public int getDuration() {
		return _duration;
	}

	public String getTitle() {
		return _title;
	}

	public String getSubscriptionTitle() {
		return _subscriptionTitle;
	}

	public boolean hasActivePodcast() {
		return getState() != PlayerStates.QUEUEEMPTY;
	}

}
