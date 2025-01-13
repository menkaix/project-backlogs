package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelRepository extends MongoRepository<Channel, String> {
}