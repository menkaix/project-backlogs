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

}
