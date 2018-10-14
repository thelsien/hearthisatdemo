package thelsien.example.com.hearthisatdemo;

import com.bumptech.glide.Glide;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import thelsien.example.com.hearthisatdemo.adapters.AdapterItemsLoadedListener;
import thelsien.example.com.hearthisatdemo.adapters.TrackListAdapter;
import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerProvider;
import thelsien.example.com.hearthisatdemo.models.Artist;
import thelsien.example.com.hearthisatdemo.models.Track;

public class ArtistDetailsFragment extends BaseListFragment<Track> implements AdapterItemsLoadedListener {

	private static final String ARGUMENTS_ARTIST_KEY = "arguments_artist_key";

	private static final String MEDIA_CONTROLLER_ADAPTER_KEY = "adaper_key_media_controller";

	@Nullable
	private Artist artist;

	private TextView artistNameView;

	private ImageView artistImageView;

	private TrackListAdapter adapter;

	public static ArtistDetailsFragment createInstance(@NonNull final Artist artist) {
		final ArtistDetailsFragment fragment = new ArtistDetailsFragment();
		final Bundle args = new Bundle();

		args.putSerializable(ARGUMENTS_ARTIST_KEY, artist);
		fragment.setArguments(args);

		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
			@Nullable final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_artist_details, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {

		artistNameView = view.findViewById(R.id.tv_artist_name);
		artistImageView = view.findViewById(R.id.iv_artist_image);

		final Bundle args = getArguments();
		if (args != null) {
			artist = (Artist) args.getSerializable(ARGUMENTS_ARTIST_KEY);
		}

		if (artist != null) {
			artistNameView.setText(artist.getUsername());
			Glide.with(artistImageView)
					.load(artist.getAvatarUrl())
					.into(artistImageView);
		}

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void setupList() {
		if (artist == null) {
			return;
		}

		super.setupList();

		adapter = new TrackListAdapter(artist.getPermalink(), this);
		MediaPlayerProvider.getInstance().addMediaPlayerManager(MEDIA_CONTROLLER_ADAPTER_KEY, adapter);

		listView.setAdapter(adapter);
	}

	@Override
	public void onListItemClicked(@NonNull final Track item) {

	}

	@Override
	public void onDetach() {
		super.onDetach();

		MediaPlayerProvider.getInstance().removeMediaPlayerManager(MEDIA_CONTROLLER_ADAPTER_KEY);
	}
}
