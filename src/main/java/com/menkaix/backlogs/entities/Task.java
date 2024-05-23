package com.menkaix.backlogs.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class Task  extends AbstractEntity {
	
	@Id
    public String id;
	
	public String projectId ;
	
	public String reference ;
	
	public String title ;
	
	public String description ;
	
	public Date dueDate ;
	
	public Date doneDate ;

	public String idReference;

}
