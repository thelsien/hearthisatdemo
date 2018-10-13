package thelsien.example.com.hearthisatdemo.adapters;

import com.bumptech.glide.Glide;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thelsien.example.com.hearthisatdemo.R;
import thelsien.example.com.hearthisatdemo.models.Artist;
import thelsien.example.com.hearthisatdemo.models.Track;
import thelsien.example.com.hearthisatdemo.retrofit.RetrofitServiceProvider;

public class PopularArtistsAdapter extends RecyclerView.Adapter<PopularArtistsAdapter.ViewHolder> {

	private static final String TAG = PopularArtistsAdapter.class.getSimpleName();

	private static final int PAGE_PER_COUNT = 20;

	@NonNull
	private final AdapterItemsLoadedListener loadListener;

	@Nullable
	private AdapterItemClickedListener<Artist> adapterItemClickedListener;

	@NonNull
	private List<Pair<Artist, List<Track>>> items = new ArrayList<>();

	/**
	 * Starts from 0. When a page load is called, it is increased.
	 */
	private int pageNumber = 0;

	public PopularArtistsAdapter(@NonNull final AdapterItemsLoadedListener loadListener) {
		this.loadListener = loadListener;

		loadPage();
	}

	/**
	 * Called when a new page load should happen.
	 */
	public synchronized void loadPage() {
		pageNumber++;

		RetrofitServiceProvider.getInstance().getHearThisAtService().getTracks(pageNumber, PAGE_PER_COUNT).enqueue(
				new Callback<List<Track>>() {
					@Override
					public void onResponse(@NonNull final Call<List<Track>> call,
							@NonNull final Response<List<Track>> response) {
						final int statusCode = response.code();
						if (statusCode >= 200 && statusCode < 300) {
							final List<Track> tracks = response.body();

							if (tracks == null) {
								Log.d(TAG, "Track getting returned with null.");
								loadListener.onListItemsLoaded(getItemCount());
								return;
							}

							populateItemsFromServerData(tracks);
							sortItems();
							notifyDataSetChanged();

							loadListener.onListItemsLoaded(getItemCount());
						} else {
							final String responseMessage = response.message();
							Log.e(TAG, "Error getting list of tracks. - " + statusCode + " - " + responseMessage);
							loadListener.onListLoadError(responseMessage);
						}
					}

					@Override
					public void onFailure(@NonNull final Call<List<Track>> call, @NonNull final Throwable throwable) {
						Log.e(TAG, "Error getting list of tracks.", throwable);
					}
				});
	}

	/**
	 * Populates the adapter items from the given parameter list.
	 *
	 * @param tracks a list of recently loaded tracks.
	 */
	private void populateItemsFromServerData(final List<Track> tracks) {
		for (final Track track : tracks) {
			final Artist trackArtist = track.getUser();
			boolean isFoundInMap = false;
			for (final Pair<Artist, List<Track>> pair : items) {
				if (pair.first.getId().equals(trackArtist.getId())) {
					pair.second.add(track);
					isFoundInMap = true;
					break;
				}
			}

			if (!isFoundInMap) {
				final List<Track> userTracks = new ArrayList<>();
				userTracks.add(track);
				items.add(new Pair<>(trackArtist, userTracks));
			}
		}
	}

	/**
	 * Sorts the items in the adapter.
	 */
	private void sortItems() {
		Collections.sort(items, (firstPair, secondPair) -> {
			final int firstSize = firstPair.second.size();
			final int secondSize = secondPair.second.size();

			return Integer.compare(secondSize, firstSize);
		});
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int position) {
		final View itemView = LayoutInflater.from(viewGroup.getContext())
				.inflate(R.layout.recyclerview_artist_item, viewGroup, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
		final Artist artist = items.get(position).first;

		if (artist == null) {
			return;
		}

		viewHolder.artistNameView.setText(artist.getUsername());

		final Context context = viewHolder.artistNameView.getContext();
		final int trackCount = items.get(position).second.size();
		String trackString;
		if (trackCount < 2) {
			trackString = String.format(context.getString(R.string.artist_track_single_text), trackCount);
		} else {
			trackString = String.format(context.getString(R.string.artist_track_multiple_text), trackCount);
		}
		viewHolder.artistTrackCountView.setText(trackString);

		Glide.with(viewHolder.artistImageView)
				.load(artist.getAvatarUrl())
				.into(viewHolder.artistImageView);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public void setOnArtistClickedListener(@NonNull final AdapterItemClickedListener<Artist> listener) {
		this.adapterItemClickedListener = listener;
	}

	class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		final TextView artistNameView;

		final ImageView artistImageView;

		final TextView artistTrackCountView;

		ViewHolder(@NonNull final View itemView) {
			super(itemView);

			artistNameView = itemView.findViewById(R.id.tv_artist_name);
			artistImageView = itemView.findViewById(R.id.iv_artist_image);
			artistTrackCountView = itemView.findViewById(R.id.tv_artist_track_count);

			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(final View v) {
			if (adapterItemClickedListener == null) {
				return;
			}

			final Artist artist = items.get(getAdapterPosition()).first;
			adapterItemClickedListener.onListItemClicked(artist);
		}
	}
}
