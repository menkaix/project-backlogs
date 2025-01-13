package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.ProjectGenre;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProjectGenreRepository extends MongoRepository<ProjectGenre, String> {
}