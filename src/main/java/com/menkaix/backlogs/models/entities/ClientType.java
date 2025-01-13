package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;

public class ClientType {

	@Id
	private String id;

	private String clientTypeName;

	public String getClientTypeName() {
		return clientTypeName;
	}

	public void setClientTypeName(String clientTypeName) {
		this.clientTypeName = clientTypeName;
	}

}
