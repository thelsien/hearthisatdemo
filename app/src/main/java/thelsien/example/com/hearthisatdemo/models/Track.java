package thelsien.example.com.hearthisatdemo.models;

public class Track {
	private String id;

	private String created_at;

	private String user_id;

	private String duration;

	private String description;

	private String genre;

	private String genre_slush;

	private String title;

	private String artwork_url;

	private String waveform_data;

	private String waveform_url;

	private Artist user;

	private String stream_url;

	public Track() {

	}

	public String getId() {
		return id;
	}

	public String getCreatedAt() {
		return created_at;
	}

	public String getUserId() {
		return user_id;
	}

	public String getDuration() {
		return duration;
	}

	public String getDescription() {
		return description;
	}

	public String getGenre() {
		return genre;
	}

	public String getGenreSlush() {
		return genre_slush;
	}

	public String getTitle() {
		return title;
	}

	public String getArtworkUrl() {
		return artwork_url;
	}

	public String getWaveformData() {
		return waveform_data;
	}

	public String getWaveformUrl() {
		return waveform_url;
	}

	public Artist getUser() {
		return user;
	}

	public String getStreamUrl() {
		return stream_url;
	}
}
