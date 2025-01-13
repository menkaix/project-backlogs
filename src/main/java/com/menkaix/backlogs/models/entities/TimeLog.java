package com.menkaix.backlogs.models.entities;

import java.util.Date;

public class TimeLog {


	private Integer id;
	

	private Task task ;
	

	private People owner ;
	
	private String mailSubject ;
	
	public People getOwner() {
		return owner;
	}
	public void setOwner(People owner) {
		this.owner = owner;
	}
	private Float seconds ;
	
	private Date creationDate ;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	
	public Float getSeconds() {
		return seconds;
	}
	public void setSeconds(Float seconds) {
		this.seconds = seconds;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
}
