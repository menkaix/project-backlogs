package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.TimeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimeLogRepository extends MongoRepository<TimeLog, String> {
}