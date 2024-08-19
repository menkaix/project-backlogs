package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Project  extends AbstractEntity {
	
	@Id
	public String id;
	
	public String code ;
	public String clientName ;
	public String group ;

	public String description ;

	
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

	public Project() {
		
	}
	
	public Project(String name) {
		this.name = name ;
	}

}
