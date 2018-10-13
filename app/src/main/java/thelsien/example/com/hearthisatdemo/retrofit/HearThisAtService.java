package thelsien.example.com.hearthisatdemo.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import thelsien.example.com.hearthisatdemo.models.Track;

public interface HearThisAtService {

	@GET("feed")
	Call<List<Track>> getTracks(@Query("page") int page, @Query("count") int count);

	@GET("{artistname}")
	Call<List<Track>> getArtistTracks(@Path("artistname") String artistName, @Query("type") String type,
			@Query("page") int page,
			@Query("count") int count);
}
