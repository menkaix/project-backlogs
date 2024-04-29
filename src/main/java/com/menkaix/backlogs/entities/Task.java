package com.menkaix.backlogs.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Task {
	
	@Id
    public String id;
	
	public String projectId ;
	
	public String title ;
	
	public String description ;
	
	public Date creationDate = new Date() ;
	
	public Date dueDate ;
	
	public Date doneDate ;

}
