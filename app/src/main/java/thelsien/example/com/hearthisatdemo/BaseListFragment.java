package thelsien.example.com.hearthisatdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import thelsien.example.com.hearthisatdemo.adapters.AdapterItemClickedListener;
import thelsien.example.com.hearthisatdemo.adapters.AdapterItemsLoadedListener;

public abstract class BaseListFragment<T> extends Fragment implements AdapterItemsLoadedListener,
		AdapterItemClickedListener<T> {

	protected RecyclerView listView;

	protected ProgressBar loadingBar;

	protected TextView messageView;

	@Override
	public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		listView = view.findViewById(R.id.rv_list);
		loadingBar = view.findViewById(R.id.pb_loading);
		messageView = view.findViewById(R.id.tv_message);

		setupList();

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

	public void setupList() {
		final Context context = getContext();
		if (context != null) {
			listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
			listView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
		}
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
	public void onListLoadError(
			@NonNull final String message) {
		showMessage(String.format(getString(R.string.list_error_message), message));
	}
}
