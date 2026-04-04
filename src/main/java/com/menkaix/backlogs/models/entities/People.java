package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.menkaix.backlogs.models.transients.PersonSkill;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "person")
public class People extends AbstractEntity {

	@Id
	private String id;

	private String firstName;
	private String lastName;
	private String email;
	private String description;
	private boolean isActive = true;

	private Date lastAccess ;

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	private List<PersonSkill> skills = new ArrayList<>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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
}
