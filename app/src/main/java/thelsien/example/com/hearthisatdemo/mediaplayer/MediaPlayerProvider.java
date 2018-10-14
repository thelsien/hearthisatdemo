package thelsien.example.com.hearthisatdemo.mediaplayer;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thelsien.example.com.hearthisatdemo.models.Track;

public class MediaPlayerProvider implements MediaPlayer.OnPreparedListener {

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
                                       @NonNull final MediaPlayer.OnCompletionListener completionListener) {
        this.currentlyPlayingTrack = track;

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
            entry.getValue().onStartPressed();
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
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.prepareAsync();
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
            entry.getValue().onResumePressed();
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

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        for (final Map.Entry<String, MediaPlayerController> entry : mediaPlayerControllers.entrySet()) {
            entry.getValue().onResumePressed();
        }
    }
}
