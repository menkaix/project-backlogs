package com.menkaix.backlogs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.FeatureType;
import com.menkaix.backlogs.entities.Task;
import com.menkaix.backlogs.repositories.TaskRepository;

@Service
public class TaskService {

	private static Logger logger = LoggerFactory.getLogger(TaskService.class);
	private final TaskRepository repository;
	private final FeatureTypeService featureTypeService;

	@Autowired
	public TaskService(TaskRepository repository, FeatureTypeService featureTypeService) {
		this.repository = repository;
		this.featureTypeService = featureTypeService;
	}

	public void createUsualTasks(Feature feature) {

		FeatureType featureType = featureTypeService.getFeatureType(feature.type);

		if (featureType != null) {
			for (String taskKey : featureType.usualTask.keySet()) {

				Task task = new Task();
				task.idReference = "feature/" + feature.id;
				task.reference = "feature/" + feature.id + "/" + taskKey;
				task.title = String.format(featureType.usualTask.get(taskKey), feature.name);

				repository.save(task);

			}
		} else {
			logger.error("no feature type named " + feature.type);
		}
	}

}
