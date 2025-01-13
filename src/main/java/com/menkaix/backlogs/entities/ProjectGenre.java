package com.menkaix.backlogs.entities;


public class ProjectGenre {

	
	private Integer id;
	
	private String genreName ;
	private String description ;

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
