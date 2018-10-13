package thelsien.example.com.hearthisatdemo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import thelsien.example.com.hearthisatdemo.adapters.AdapterItemClickedListener;
import thelsien.example.com.hearthisatdemo.adapters.AdapterItemsLoadedListener;
import thelsien.example.com.hearthisatdemo.adapters.PopularArtistsAdapter;
import thelsien.example.com.hearthisatdemo.models.Artist;

public class PopularArtistsListFragment extends BaseListFragment<Artist> implements AdapterItemsLoadedListener,
		AdapterItemClickedListener<Artist> {

	@Nullable
	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
			@Nullable final Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_base_list, container, false);
	}

	@Override
	public void setupList() {
		super.setupList();

		final PopularArtistsAdapter adapter = new PopularArtistsAdapter(this);
		adapter.setOnArtistClickedListener(this);

		listView.setAdapter(adapter);
	}

	@Override
	public void onListItemClicked(@NonNull final Artist artist) {
		final Activity activity = getActivity();

		if (activity instanceof MainActivity) {
			((MainActivity) activity).changeFragmentToArtistDetails(artist);
		}
	}
}
