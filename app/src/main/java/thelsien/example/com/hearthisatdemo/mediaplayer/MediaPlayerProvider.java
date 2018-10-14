package thelsien.example.com.hearthisatdemo.mediaplayer;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thelsien.example.com.hearthisatdemo.models.Track;

public class MediaPlayerProvider {

	private static MediaPlayerProvider instance;

	@Nullable
	private MediaPlayer mediaPlayer;

	@Nullable
	private Track currentlyPlayingTrack;

	@NonNull
	private Map<String, MediaPlayerController> mediaPlayerControllers = new HashMap<>();

	private MediaPlayerProvider() {
	}

	public static MediaPlayerProvider getInstance() {
		if (instance == null) {
			instance = new MediaPlayerProvider();
		}

		return instance;
	}

	public void startPreparePlayingUrl(@NonNull final Track track,
			@NonNull final MediaPlayer.OnPreparedListener preparedListener,
			@NonNull final MediaPlayer.OnCompletionListener completionListener) {
		this.currentlyPlayingTrack = track;

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}

		mediaPlayer.reset();

		try {
			mediaPlayer.setDataSource(currentlyPlayingTrack.getStreamUrl());
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
			return;
		}

		mediaPlayer.setOnErrorListener((mp, what, extra) -> {
			mediaPlayer.release();
			mediaPlayer = null;
			return true;
		});
		mediaPlayer.setScreenOnWhilePlaying(true);
		mediaPlayer.setOnCompletionListener(completionListener);
		mediaPlayer.setOnPreparedListener(preparedListener);
		mediaPlayer.prepareAsync();
		for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
			entry.getValue().onStartPressed();
		}
	}

	public void pausePlayingTrack() {
		if (mediaPlayer == null) {
			return;
		}

		mediaPlayer.pause();

		for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
			entry.getValue().onPausePressed();
		}
	}

	public void resumePlayingTrack() {
		if (mediaPlayer == null) {
			return;
		}

		mediaPlayer.start();

		for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
			entry.getValue().onStartPressed();
		}
	}

	public void stopPlayingTrack() {
		currentlyPlayingTrack = null;
		if (mediaPlayer == null) {
			return;
		}

		mediaPlayer.release();
		mediaPlayer = null;

		for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
			entry.getValue().onStopPressed();
		}
	}

	public void addMediaPlayerManager(@NonNull final String key,
			@NonNull final MediaPlayerController mediaPlayerController) {
		mediaPlayerControllers.put(key, mediaPlayerController);
	}

	public void removeMediaPlayerManager(@NonNull final String key) {
		mediaPlayerControllers.remove(key);
	}

	@Nullable
	public String getCurrentlyPlayingTrackTitle() {
		if (currentlyPlayingTrack == null) {
			return null;
		}

		return currentlyPlayingTrack.getTitle();
	}
}
