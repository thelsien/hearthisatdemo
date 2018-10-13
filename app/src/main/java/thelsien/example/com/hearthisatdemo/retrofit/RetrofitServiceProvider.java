package thelsien.example.com.hearthisatdemo.retrofit;

import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitServiceProvider {

	private static final String BASE_URL = "https://api-v2.hearthis.at/";

	private static RetrofitServiceProvider instance;

	private Retrofit retrofit;

	@NonNull
	private HearThisAtService hearThisAtService;

	private RetrofitServiceProvider() {
		retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();

		hearThisAtService = retrofit.create(HearThisAtService.class);
	}

	@NonNull
	public static RetrofitServiceProvider getInstance() {
		if (instance == null) {
			instance = new RetrofitServiceProvider();
		}

		return instance;
	}

	@NonNull
	public HearThisAtService getHearThisAtService() {
		return hearThisAtService;
	}
}