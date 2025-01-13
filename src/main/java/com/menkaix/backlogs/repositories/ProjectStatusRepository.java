package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.ProjectStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectStatusRepository extends MongoRepository<ProjectStatus, String> {
}