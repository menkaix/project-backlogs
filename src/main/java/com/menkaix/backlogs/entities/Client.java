package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;


public class Client  extends AbstractEntity{

	@Id
	public String id;

	public Client() {}

	public Client(String name) {
		this.name = name;
	}

}

