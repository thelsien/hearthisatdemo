package thelsien.example.com.hearthisatdemo.adapters;

import com.bumptech.glide.Glide;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thelsien.example.com.hearthisatdemo.R;
import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerProvider;
import thelsien.example.com.hearthisatdemo.models.Track;
import thelsien.example.com.hearthisatdemo.retrofit.RetrofitServiceProvider;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder> {

	private static final String ARTIST_TRACKS_TYPE = "tracks";

	private static final int ARTIST_TRACKS_COUNT = 20;

	@NonNull
	private final AdapterItemsLoadedListener loaderListener;

	@NonNull
	private List<Track> items = new ArrayList<>();

	@NonNull
	private String userPermalink;

	private int page = 0;

	private int currentlyPlayingAdapterPosition = -1;

	public TrackListAdapter(@NonNull final String artistPermalink,
			@NonNull final AdapterItemsLoadedListener loaderListener) {
		this.userPermalink = artistPermalink;
		this.loaderListener = loaderListener;

		loadTracksForUser();
	}

	private void loadTracksForUser() {
		page++;

		RetrofitServiceProvider.getInstance().getHearThisAtService()
				.getArtistTracks(userPermalink, ARTIST_TRACKS_TYPE, page, ARTIST_TRACKS_COUNT).enqueue(
				new Callback<List<Track>>() {
					@Override
					public void onResponse(@NonNull final Call<List<Track>> call,
							@NonNull final Response<List<Track>> response) {
						final int statusCode = response.code();

						if (statusCode >= 200 && statusCode < 300) {
							final List<Track> tracks = response.body();

							if (tracks == null) {
								loaderListener.onListItemsLoaded(getItemCount());
								return;
							}

							items.addAll(tracks);

							notifyDataSetChanged();

							loaderListener.onListItemsLoaded(getItemCount());

						} else {
							final String message = response.message();
							loaderListener.onListLoadError("Error during loading tracks - " + message);
						}
					}

					@Override
					public void onFailure(@NonNull final Call<List<Track>> call, @NonNull final Throwable throwable) {
						loaderListener.onListLoadError("Error during loading the tracks " + throwable.getMessage());
					}
				});
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int position) {
		final View itemView =
				LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_track_item, viewGroup, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
		final Track track = items.get(position);

		if (track == null) {
			return;
		}

		viewHolder.trackNameView.setText(track.getTitle());
		Glide.with(viewHolder.trackImageView)
				.load(track.getArtworkUrl())
				.into(viewHolder.trackImageView);

		final String currentlyPlayingTrack = MediaPlayerProvider.getInstance().getCurrentlyPlayingTrackId();

		if (currentlyPlayingTrack != null && currentlyPlayingTrack.equals(track.getId())) {
			viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_stop);
			viewHolder.mediaPlayerIconView.setOnClickListener(clickedView -> {
				MediaPlayerProvider.getInstance().stopPlayingTrack();
				viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_play);
			});
		} else {
			viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_play);
			viewHolder.mediaPlayerIconView.setOnClickListener(getStartPlayingListener(track, viewHolder));
		}
	}

	public View.OnClickListener getStopListener(@NonNull final Track track, @NonNull final ViewHolder viewHolder) {
		return clickedView -> {
			MediaPlayerProvider.getInstance().stopPlayingTrack();
			viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_play);
			viewHolder.mediaPlayerIconView.setOnClickListener(getStartPlayingListener(track, viewHolder));
		};
	}

	public View.OnClickListener getStartPlayingListener(@NonNull final Track track,
			@NonNull final ViewHolder viewHolder) {
		return clickedView -> {
			MediaPlayerProvider.getInstance().startPreparePlayingUrl(track.getId(), track.getStreamUrl(),
					mp -> {
						mp.start();
						viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_stop);
						viewHolder.mediaPlayerIconView.setOnClickListener(getStopListener(track, viewHolder));
					},
					mp -> {
						MediaPlayerProvider.getInstance().stopPlayingTrack();
						viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_play);
						viewHolder.mediaPlayerIconView.setOnClickListener(getStartPlayingListener(track, viewHolder));
					});
		};
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MediaPlayer.OnPreparedListener {

		TextView trackNameView;

		ImageView trackImageView;

		ImageView mediaPlayerIconView;

		public ViewHolder(@NonNull final View itemView) {
			super(itemView);

			trackNameView = itemView.findViewById(R.id.tv_track_name);
			trackImageView = itemView.findViewById(R.id.iv_track_image);
			mediaPlayerIconView = itemView.findViewById(R.id.iv_media_player_icon);

			mediaPlayerIconView.setOnClickListener(this);
		}

		@Override
		public void onPrepared(final MediaPlayer mp) {

		}

		@Override
		public void onClick(final View clickedView) {
			final String currentTrackId = MediaPlayerProvider.getInstance().getCurrentlyPlayingTrackId();
			final Track track = items.get(getAdapterPosition());

			if (currentlyPlayingAdapterPosition < 0) {
				notifyItemChanged(currentlyPlayingAdapterPosition);
			}

			currentlyPlayingAdapterPosition = getAdapterPosition();
		}
	}
}
