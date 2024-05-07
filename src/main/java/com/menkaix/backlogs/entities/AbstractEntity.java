package com.menkaix.backlogs.entities;

import java.util.Date;

public abstract class AbstractEntity {
	
	public String name;
	public Date creationDate=new Date() ;
	public Date lastUpdateDate ;

}
