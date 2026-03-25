package com.menkaix.backlogs.models.dto;

import com.menkaix.backlogs.models.transients.PersonSkill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FullPersonDTO {

	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String description;
	private boolean isActive;
	private List<PersonSkill> skills = new ArrayList<>();
	private Date creationDate;
	private Date lastUpdateDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<PersonSkill> getSkills() {
		return skills;
	}

	public void setSkills(List<PersonSkill> skills) {
		this.skills = skills;
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
