package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;

public class Project  extends AbstractEntity {
	
	@Id
	public String id;
	
	public String code ;
	public String clientName ;
	public String group ;

	public String description ;

	public Project() {
		
	}
	
	public Project(String name) {
		this.name = name ;
	}

}
