package thelsien.example.com.hearthisatdemo.mediaplayer;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

import thelsien.example.com.hearthisatdemo.models.Track;

public class MediaPlayerProvider {

	private static MediaPlayerProvider instance;

	@Nullable
	private MediaPlayer mediaPlayer;

	@Nullable
	private Track currentlyPlayingTrack;

	@Nullable
	private MediaPlayerManager mediaPlayerManager;

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

		mediaPlayer.setScreenOnWhilePlaying(true);
		mediaPlayer.setOnCompletionListener(completionListener);
		mediaPlayer.setOnPreparedListener(preparedListener);
		mediaPlayer.prepareAsync();
		if (mediaPlayerManager != null) {
			mediaPlayerManager.onStartPressed();
		}
	}

	public void pausePlayingTrack() {
		if (mediaPlayer == null) {
			return;
		}

		mediaPlayer.pause();

		if (mediaPlayerManager != null) {
			mediaPlayerManager.onPausePressed();
		}
	}

	public void resumePlayingTrack() {
		if (mediaPlayer == null) {
			return;
		}

		mediaPlayer.start();

		if (mediaPlayerManager != null) {
			mediaPlayerManager.onStartPressed();
		}
	}

	@Nullable
	public String getCurrentlyPlayingTrackId() {
		if (currentlyPlayingTrack == null) {
			return null;
		}

		return currentlyPlayingTrack.getId();
	}

	public void stopPlayingTrack() {
		currentlyPlayingTrack = null;
		if (mediaPlayer == null) {
			return;
		}
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;

		if (mediaPlayerManager != null) {
			mediaPlayerManager.onStopPressed();
		}
	}

	public void setMediaPlayerManager(@NonNull final MediaPlayerManager mediaPlayerManager) {
		this.mediaPlayerManager = mediaPlayerManager;
	}

	@Nullable
	public String getCurrentlyPlayingTrackTitle() {
		if (currentlyPlayingTrack == null) {
			return null;
		}

		return currentlyPlayingTrack.getTitle();
	}
}
