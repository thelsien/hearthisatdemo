package thelsien.example.com.hearthisatdemo.mediaplayer;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class MediaPlayerProvider {

	private static MediaPlayerProvider instance;

	@Nullable
	private MediaPlayer mediaPlayer;

	@Nullable
	private String currentlyPlayingTrackId;

	private MediaPlayerProvider() {
	}

	public static MediaPlayerProvider getInstance() {
		if (instance == null) {
			instance = new MediaPlayerProvider();
		}

		return instance;
	}

	public void startPreparePlayingUrl(final String trackId, @NonNull final String url,
			@NonNull final MediaPlayer.OnPreparedListener preparedListener,
			@NonNull final MediaPlayer.OnCompletionListener completionListener) {
		currentlyPlayingTrackId = trackId;

		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
		}

		mediaPlayer.reset();

		try {
			mediaPlayer.setDataSource(url);
		} catch (IllegalArgumentException | IOException e) {
			e.printStackTrace();
			return;
		}

		mediaPlayer.setScreenOnWhilePlaying(true);
		mediaPlayer.setOnCompletionListener(completionListener);
		mediaPlayer.setOnPreparedListener(preparedListener);
		mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
			@Override
			public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
				switch (what) {
					case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING:
						break;
					case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_END:
						break;
					case MediaPlayer.MEDIA_INFO_BUFFERING_START:
						break;
					case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
						break;
					default:
						break;
				}

				Log.d(MediaPlayer.class.getSimpleName(), "info - " + what);

				return false;
			}
		});
		mediaPlayer.prepareAsync();
	}

	@Nullable
	public String getCurrentlyPlayingTrackId() {
		return currentlyPlayingTrackId;
	}

	public void stopPlayingTrack() {
		currentlyPlayingTrackId = null;
		if (mediaPlayer == null) {
			return;
		}
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer = null;
	}
}
