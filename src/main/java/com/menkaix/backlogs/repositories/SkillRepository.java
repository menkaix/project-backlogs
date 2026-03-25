package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.Skill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;
import java.util.Optional;

@RepositoryRestController(path = "skills", value = "skills")
public interface SkillRepository extends MongoRepository<Skill, String> {

    Optional<Skill> findByName(String name);

    List<Skill> findByCategory(String category);

    List<Skill> findByTagsContaining(String tag);
}
