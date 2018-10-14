package thelsien.example.com.hearthisatdemo.adapters;

import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import thelsien.example.com.hearthisatdemo.R;
import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerController;
import thelsien.example.com.hearthisatdemo.mediaplayer.MediaPlayerProvider;
import thelsien.example.com.hearthisatdemo.models.Track;
import thelsien.example.com.hearthisatdemo.retrofit.RetrofitServiceProvider;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder>
        implements MediaPlayerController {

    private static final String MEDIA_PLAYING_TAG = "playing";

    private static final String MEDIA_STOPPED_TAG = "stopped";

    private static final String ARTIST_TRACKS_TYPE = "tracks";

    private static final int ARTIST_TRACKS_COUNT = 20;

    @NonNull
    private final AdapterItemsLoadedListener loaderListener;

    @NonNull
    private List<Track> items = new ArrayList<>();

    @NonNull
    private String userPermalink;

    private boolean isPrepared = false;

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

        if (currentlyPlayingAdapterPosition != position) {
            viewHolder.mediaPlayerIconView.setVisibility(View.VISIBLE);
            viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_play);
            viewHolder.mediaPlayerIconView.setTag(MEDIA_STOPPED_TAG);
        } else {
            viewHolder.mediaPlayerIconView.setVisibility(isPrepared ? View.GONE : View.VISIBLE);
            viewHolder.mediaPlayerIconView.setImageResource(R.drawable.ic_action_stop);
            viewHolder.mediaPlayerIconView.setTag(MEDIA_PLAYING_TAG);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onResumePressed() {

    }

    @Override
    public void onStartPressed() {

    }

    @Override
    public void onPausePressed() {

    }

    @Override
    public void onStopPressed() {
        final int positionToNotify = currentlyPlayingAdapterPosition;
        currentlyPlayingAdapterPosition = -1;
        notifyItemChanged(positionToNotify);
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
            mp.start();
            isPrepared = true;
            notifyItemChanged(currentlyPlayingAdapterPosition);
        }

        @Override
        public void onClick(final View clickedView) {
            if (currentlyPlayingAdapterPosition < 0 || currentlyPlayingAdapterPosition != getAdapterPosition()) {
                notifyItemChanged(currentlyPlayingAdapterPosition);
            }

            if (mediaPlayerIconView.getTag().equals(MEDIA_PLAYING_TAG)) {
                MediaPlayerProvider.getInstance().stopPlayingTrack();
                mediaPlayerIconView.setTag(MEDIA_STOPPED_TAG);

                notifyItemChanged(currentlyPlayingAdapterPosition);

                currentlyPlayingAdapterPosition = -1;
                return;
            }

            currentlyPlayingAdapterPosition = getAdapterPosition();
            final Track track = items.get(getAdapterPosition());
            isPrepared = false;
            MediaPlayerProvider.getInstance().startPreparePlayingUrl(track, this, MediaPlayer::release);

            mediaPlayerIconView.setTag(MEDIA_PLAYING_TAG);
            notifyItemChanged(currentlyPlayingAdapterPosition);
        }
    }
}
