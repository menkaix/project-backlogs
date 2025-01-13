package com.menkaix.backlogs.entities;

import com.menkaix.backlogs.utilities.values.LifeCycle;

public class ProjectStatus {
	
	
	private Integer id;
	
	private String statusName ;
	private String description ;
	private LifeCycle cycle ;

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName.toUpperCase();
	}

	public LifeCycle getCycle() {
		return cycle;
	}

	public void setCycle(LifeCycle cycle) {
		this.cycle = cycle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
