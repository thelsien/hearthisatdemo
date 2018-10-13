package thelsien.example.com.hearthisatdemo.adapters;

import android.support.annotation.NonNull;

/**
 * Handles the item clicks in the artist list.
 */
public interface AdapterItemClickedListener<T> {

	void onListItemClicked(@NonNull final T item);
}