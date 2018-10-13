package thelsien.example.com.hearthisatdemo.adapters;

import android.support.annotation.NonNull;

/**
 * Handles the cases when the artist list items are loaded or an error happened.
 */
public interface AdapterItemsLoadedListener {

	void onListItemsLoaded(int count);

	void onListLoadError(@NonNull String message);
}