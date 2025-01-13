package com.menkaix.backlogs.models.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Note {

	@Id
	private String id;

	private String projectCode;

	private Date creationDate;

	private String title;

	private String content;

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
