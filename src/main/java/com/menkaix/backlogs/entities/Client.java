package com.menkaix.backlogs.entities;

import org.springframework.data.annotation.Id;


public class Client {

	@Id
	public String id;

	public String name;

	public Client() {}

	public Client(String name) {
		this.name = name;
	}

}

