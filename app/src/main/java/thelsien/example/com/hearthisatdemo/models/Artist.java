package thelsien.example.com.hearthisatdemo.models;

import java.io.Serializable;

public class Artist implements Serializable {
	private String id;

	private String username;

	private String permalink;

	private String avatar_url;

	public Artist() {

	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getAvatarUrl() {
		return avatar_url;
	}

	public String getPermalink() {
		return permalink;
	}
}
