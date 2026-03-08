package com.menkaix.backlogs.models.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.menkaix.backlogs.models.transients.Comment;
import com.menkaix.backlogs.models.transients.Link;

public abstract class AbstractEntity {

	public String name;

	@CreatedDate
	public Date creationDate;

	@LastModifiedDate
	public Date lastUpdateDate;

	private List<Comment> comments = new ArrayList<Comment>();
	private List<Link> links = new ArrayList<Link>();

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public AbstractEntity() {

	}

	public AbstractEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}
