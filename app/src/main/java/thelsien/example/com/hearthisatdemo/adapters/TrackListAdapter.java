package thelsien.example.com.hearthisatdemo.adapters;

import com.bumptech.glide.Glide;

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

		//TODO change media player icon based on the selected track id.
		//TODO add an on click event for media player icon.
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	class ViewHolder extends RecyclerView.ViewHolder {

		TextView trackNameView;

		ImageView trackImageView;

		ImageView mediaPlayerIconView;

		public ViewHolder(@NonNull final View itemView) {
			super(itemView);

			trackNameView = itemView.findViewById(R.id.tv_track_name);
			trackImageView = itemView.findViewById(R.id.iv_track_image);
			mediaPlayerIconView = itemView.findViewById(R.id.iv_media_player_icon);
		}
	}
}
