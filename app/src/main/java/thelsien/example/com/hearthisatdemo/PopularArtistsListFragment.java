package thelsien.example.com.hearthisatdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import thelsien.example.com.hearthisatdemo.adapters.PopularArtistsAdapter;
import thelsien.example.com.hearthisatdemo.models.Artist;

public class PopularArtistsListFragment extends Fragment implements PopularArtistsAdapter.LoaderListener,
		PopularArtistsAdapter.ArtistClickedListener {

	private RecyclerView listView;

	private ProgressBar loadingBar;

	private TextView messageView;

	private PopularArtistsAdapter adapter;

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
			@Nullable final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_popular_artists_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listView = view.findViewById(R.id.rv_list);
		loadingBar = view.findViewById(R.id.pb_loading);
		messageView = view.findViewById(R.id.tv_message);

		setupList();

	}

	private void setupList() {
		adapter = new PopularArtistsAdapter(this);
		adapter.setOnArtistClickedListener(this);

		final Context context = getContext();
		if (context != null) {
			listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
			listView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
			listView.setAdapter(adapter);
		}
	}

	public void showList() {
		messageView.setText("");

		loadingBar.setVisibility(View.GONE);
		messageView.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
	}

	public void showMessage(final String stringRes) {
		listView.setVisibility(View.GONE);
		loadingBar.setVisibility(View.GONE);

		messageView.setText(stringRes);
		messageView.setVisibility(View.VISIBLE);
	}

	@Override
	public void onListItemsLoaded(final int count) {
		if (count > 0) {
			showList();
		} else {
			showMessage(getString(R.string.list_empty_message));
		}
	}

	@Override
	public void onListLoadError(@NonNull final String message) {
		showMessage(String.format(getString(R.string.list_error_message), message));
	}

	@Override
	public void onArtistClicked(@NonNull final Artist artist) {
		Toast.makeText(getContext(), artist.getUsername(), Toast.LENGTH_SHORT).show();
	}
}
