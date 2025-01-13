package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;

public class Client extends AbstractEntity {

	@Id
	public String id;

	public Client() {
	}

	public Client(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
