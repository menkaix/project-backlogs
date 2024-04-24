package com.example.accessingdatamongodb;

import org.springframework.data.annotation.Id;

public class Project {
	
	@Id
	public String id;
	
	public String name ;
	public String code ;
	public String clientName ;
	
	public Project() {
		
	}
	
	public Project(String name) {
		this.name = name ;
	}

}
