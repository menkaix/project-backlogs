package com.menkaix.backlogs.models.entities;

import org.springframework.data.annotation.Id;

public class Channel {

	@Id
	private String id;

	private String channelName;

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channerName) {
		this.channelName = channerName;
	}

}
