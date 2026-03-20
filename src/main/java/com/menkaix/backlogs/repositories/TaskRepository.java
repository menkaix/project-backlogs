package com.menkaix.backlogs.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import com.menkaix.backlogs.models.entities.Task;

@RepositoryRestController(path = "tasks",value = "tasks")
public interface TaskRepository extends MongoRepository<Task, String>  {

    List<Task> findByProjectId(String project);

	List<Task> findByProjectIdOrderByLastUpdateDateDesc(String project);

	List<Task> findByIdReference(String id);

	List<Task> findByIdReferenceOrderByLastUpdateDateDesc(String id);

	List<Task> findByIdReferenceIn(Collection<String> idReferences);

	Optional<Task> findByTrackingReference(String trackingReference);

	List<Task> findByStatus(String status);

	List<Task> findByStatusOrderByLastUpdateDateDesc(String status);

	List<Task> findByAssigneesContainingOrderByLastUpdateDateDesc(String email);
}
