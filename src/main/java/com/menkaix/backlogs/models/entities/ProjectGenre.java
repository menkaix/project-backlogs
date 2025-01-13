package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;

public class ProjectGenre {

	@Id
	private String id;

	private String genreName;
	private String description;

	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName.toUpperCase();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
