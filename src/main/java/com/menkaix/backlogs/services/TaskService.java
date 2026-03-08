package com.menkaix.backlogs.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.FeatureType;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.repositories.TaskRepository;

@Service
public class TaskService {

	private static Logger logger = LoggerFactory.getLogger(TaskService.class);
	private final TaskRepository repository;
	private final FeatureTypeService featureTypeService;
	private final MongoTemplate mongoTemplate;

	@Autowired
	public TaskService(TaskRepository repository, FeatureTypeService featureTypeService, MongoTemplate mongoTemplate) {
		this.repository = repository;
		this.featureTypeService = featureTypeService;
		this.mongoTemplate = mongoTemplate;
	}

	public void createUsualTasks(Feature feature) {
		FeatureType featureType = featureTypeService.getFeatureType(feature.getType());

		if (featureType != null) {
			for (String taskKey : featureType.getUsualTask().keySet()) {
				Task task = new Task();
				task.setIdReference("feature/" + feature.getId());
				task.setReference("feature/" + feature.getId() + "/" + taskKey);
				task.setTitle(String.format(featureType.getUsualTask().get(taskKey), feature.getName()));
				repository.save(task);
			}
		} else {
			logger.error("no feature type named " + feature.getType());
		}
	}

	public Task create(Task task) {
		return repository.save(task);
	}

	public Optional<Task> findById(String id) {
		return repository.findById(id);
	}

	public Optional<Task> findByTrackingReference(String trackingReference) {
		return repository.findByTrackingReference(trackingReference);
	}

	public Task update(Task task) {
		return repository.save(task);
	}

	public void delete(String id) {
		repository.deleteById(id);
	}

	public List<Task> findByProjectId(String projectId) {
		return repository.findByProjectId(projectId);
	}

	public List<Task> findByStatus(String status) {
		return repository.findByStatus(status);
	}

	public List<Task> findOverdueTasks() {
		Query query = new Query(Criteria.where("deadLine").lt(new Date()).and("doneDate").isNull());
		return mongoTemplate.find(query, Task.class);
	}

	public List<Task> findUpcomingTasks() {
		Date now = new Date();
		Date sevenDaysLater = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000);
		Query query = new Query(Criteria.where("deadLine").gt(now).lt(sevenDaysLater).and("doneDate").isNull());
		return mongoTemplate.find(query, Task.class);
	}

	public Page<Task> findAll(Pageable pageable, String search, String filter) {
		Query query = buildQuery(search, filter);
		Query countQuery = buildQuery(search, filter);
		List<Task> tasks = mongoTemplate.find(query.with(pageable), Task.class);
		return PageableExecutionUtils.getPage(tasks, pageable,
				() -> mongoTemplate.count(countQuery, Task.class));
	}

	private Query buildQuery(String search, String filter) {
		List<Criteria> criteria = new ArrayList<>();

		if (search != null && !search.isBlank()) {
			Criteria searchCriteria = new Criteria().orOperator(
					Criteria.where("title").regex(search, "i"),
					Criteria.where("description").regex(search, "i"),
					Criteria.where("reference").regex(search, "i")
			);
			criteria.add(searchCriteria);
		}

		if (filter != null && filter.contains(":")) {
			String[] parts = filter.split(":", 2);
			criteria.add(Criteria.where(parts[0]).is(parts[1]));
		}

		Query query = new Query();
		if (!criteria.isEmpty()) {
			query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
		}
		return query;
	}
}
