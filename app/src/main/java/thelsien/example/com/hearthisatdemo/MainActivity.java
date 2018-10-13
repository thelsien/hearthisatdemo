package thelsien.example.com.hearthisatdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import thelsien.example.com.hearthisatdemo.models.Artist;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	private static final String BACK_STACK_DETAILS_FRAGMENT_KEY = "back_stack_details_fragment_key";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, new PopularArtistsListFragment())
				.commit();
	}

	public void changeFragmentToArtistDetails(final Artist artist) {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
						android.R.anim.slide_in_left, android.R.anim.slide_out_right)
				.replace(android.R.id.content, ArtistDetailsFragment.createInstance(artist))
				.addToBackStack(BACK_STACK_DETAILS_FRAGMENT_KEY)
				.commit();
	}
}
