package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.menkaix.backlogs.models.values.ProjectPhase;
import com.menkaix.backlogs.models.values.ProjectState;

public class Project extends AbstractEntity {

	@Id
	public String id;

	public String code;
	public String clientName;
	public String group;

	public String description;

	public ProjectPhase phase = ProjectPhase.INCONNUE;

	@Transient
	public ProjectState status;

	public Project() {

	}

	public Project(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProjectPhase getPhase() {
		return phase;
	}

	public void setPhase(ProjectPhase phase) {
		this.phase = phase;
	}

	public ProjectState getStatus() {
		return status;
	}

	public void setStatus(ProjectState status) {
		this.status = status;
	}
}
