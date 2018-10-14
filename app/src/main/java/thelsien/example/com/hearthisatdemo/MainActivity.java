package thelsien.example.com.hearthisatdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerController;
import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerProvider;
import thelsien.example.com.hearthisatdemo.models.Artist;

public class MainActivity extends AppCompatActivity implements MediaPlayerController {

	private static final String MEDIA_CONTROLLER_ACTIVITY_KEY = "activity_media_controller_key";

	private static final String BACK_STACK_DETAILS_FRAGMENT_KEY = "back_stack_details_fragment_key";

	private View mediaPlayerWrapperView;

	private TextView playerTrackNameView;

	private ImageView playerPlayPauseView;

	private ImageView playerStopView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mediaPlayerWrapperView = findViewById(R.id.cl_media_player_wrapper);
		playerTrackNameView = findViewById(R.id.tv_track_name);
		playerPlayPauseView = findViewById(R.id.iv_play_pause);
		playerStopView = findViewById(R.id.iv_stop);

		playerStopView.setOnClickListener(clickedView -> {
			MediaPlayerProvider.getInstance().stopPlayingTrack();
		});

		MediaPlayerProvider.getInstance().addMediaPlayerManager(MEDIA_CONTROLLER_ACTIVITY_KEY, this);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content, new PopularArtistsListFragment())
				.commit();
	}

	@Override
	protected void onStop() {
		MediaPlayerProvider.getInstance().removeMediaPlayerManager(MEDIA_CONTROLLER_ACTIVITY_KEY);
		super.onStop();
	}

	public void changeFragmentToArtistDetails(final Artist artist) {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
						android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(R.id.content, ArtistDetailsFragment.createInstance(artist))
				.addToBackStack(BACK_STACK_DETAILS_FRAGMENT_KEY)
				.commit();
	}

	@Override
	public void onStartPressed() {
		final String trackTitle = MediaPlayerProvider.getInstance().getCurrentlyPlayingTrackTitle();
		if (trackTitle != null) {
			playerTrackNameView.setText(trackTitle);
		}

		playerPlayPauseView.setImageResource(R.drawable.ic_action_pause);
		playerPlayPauseView.setOnClickListener(getPauseClickListener());

		mediaPlayerWrapperView.setVisibility(View.VISIBLE);
	}

	@NonNull
	private View.OnClickListener getPauseClickListener() {
		return clickedView -> {
			MediaPlayerProvider.getInstance().pausePlayingTrack();
			playerPlayPauseView.setOnClickListener(getResumePlayingClickListener());
		};
	}

	private View.OnClickListener getResumePlayingClickListener() {
		return clickedView -> {
			MediaPlayerProvider.getInstance().resumePlayingTrack();
			playerPlayPauseView.setOnClickListener(getPauseClickListener());
		};
	}

	@Override
	public void onPausePressed() {
		playerPlayPauseView.setImageResource(R.drawable.ic_action_play);
	}

	@Override
	public void onStopPressed() {
		mediaPlayerWrapperView.setVisibility(View.GONE);
	}
}
