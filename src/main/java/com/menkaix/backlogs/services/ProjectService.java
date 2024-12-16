package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.entities.Task;
import com.menkaix.backlogs.models.*;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.repositories.TaskRepository;

import com.menkaix.backlogs.services.applicatif.DataAccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.utilities.exceptions.DataConflictException;
import com.menkaix.backlogs.utilities.exceptions.DataDefinitionException;

@Service
public class ProjectService {

	private static Logger logger = LoggerFactory.getLogger(ProjectService.class);
	private final ProjectRepository repo;
	private final ActorRepository actorRepisitory;
	private final TaskRepository taskRepository;
	private final StoryRepository storyRepository;
	private final FeatureRepository featureRepository;
	private final FeatureService featureService;
	private final DataAccessService accessService;

	@Autowired
	public ProjectService(ProjectRepository repo, ActorRepository actorRepisitory, TaskRepository taskRepository,
			StoryRepository storyRepository, FeatureRepository featureRepository, FeatureService featureService,
			DataAccessService accessService) {
		this.repo = repo;
		this.actorRepisitory = actorRepisitory;
		this.taskRepository = taskRepository;
		this.storyRepository = storyRepository;
		this.featureRepository = featureRepository;
		this.featureService = featureService;
		this.accessService = accessService;
	}

	private FullProjectDTO objectTree(String projectRef) {
		Project p = accessService.findProject(projectRef);
		if (p == null) {
			return null;
		}

		FullProjectDTO projectDTO = mapProjectToDTO(p);
		List<Actor> actors = actorRepisitory.findByProjectName(p.name);
		for (Actor a : actors) {
			FullActorDTO actorDTO = mapActorToDTO(a, projectDTO.code);
			projectDTO.actors.add(actorDTO);
		}
		return projectDTO;
	}

	private FullProjectDTO mapProjectToDTO(Project p) {
		FullProjectDTO projectDTO = new FullProjectDTO();
		projectDTO.id = p.id;
		projectDTO.name = p.name;
		projectDTO.description = p.description;
		projectDTO.clientName = p.clientName;
		projectDTO.creationDate = p.creationDate;
		projectDTO.code = p.code;
		return projectDTO;
	}

	private FullActorDTO mapActorToDTO(Actor a, String projectCode) {
		FullActorDTO actorDTO = new FullActorDTO();
		actorDTO.id = a.id;
		actorDTO.name = a.name;
		actorDTO.description = a.description;
		actorDTO.type = a.type;

		List<Story> stories = storyRepository.findByActorId(a.id);
		for (Story s : stories) {
			FullStoryDTO storyDTO = mapStoryToDTO(s, projectCode, actorDTO.name);
			actorDTO.stories.add(storyDTO);
		}
		return actorDTO;
	}

	private FullStoryDTO mapStoryToDTO(Story s, String projectCode, String actorName) {
		FullStoryDTO storyDTO = new FullStoryDTO();
		storyDTO.id = s.id;
		storyDTO.projectCode = projectCode;
		storyDTO.actorName = actorName;
		storyDTO.action = s.action;
		storyDTO.objective = s.objective;
		storyDTO.scenario = s.scenario;

		List<Feature> features = featureRepository.findByStoryId(s.id);
		for (Feature f : features) {
			FullFeatureDTO fullFeatureDTO = mapFeatureToDTO(f);
			storyDTO.features.add(fullFeatureDTO);
		}
		return storyDTO;
	}

	private FullFeatureDTO mapFeatureToDTO(Feature f) {
		FullFeatureDTO fullFeatureDTO = new FullFeatureDTO();
		fullFeatureDTO.id = f.id;
		fullFeatureDTO.name = f.name;
		fullFeatureDTO.description = f.description;
		fullFeatureDTO.type = f.type;
		fullFeatureDTO.parentID = f.parentID;

		List<Task> tasks = taskRepository.findByIdReference("feature/" + fullFeatureDTO.id);
		for (Task task : tasks) {
			FullTaskDTO taskDTO = mapTaskToDTO(task);
			fullFeatureDTO.tasks.add(taskDTO);
		}
		return fullFeatureDTO;
	}

	private FullTaskDTO mapTaskToDTO(Task task) {
		FullTaskDTO taskDTO = new FullTaskDTO();
		taskDTO.id = task.id;
		taskDTO.projectId = task.projectId;
		taskDTO.reference = task.reference;
		taskDTO.title = task.title;
		taskDTO.description = task.description;
		taskDTO.dueDate = task.dueDate;
		taskDTO.doneDate = task.doneDate;
		taskDTO.idReference = task.idReference;
		taskDTO.name = task.name;
		taskDTO.creationDate = task.creationDate;
		taskDTO.lastUpdateDate = task.lastUpdateDate;
		return taskDTO;
	}

	private Actor createActor(Project p, String actorName) {
		Actor actor = new Actor();
		actor.projectName = p.name;
		actor.name = actorName.toLowerCase();
		return actorRepisitory.save(actor);
	}

	private String actorToCsv(FullActorDTO actor) {
		StringBuilder ans = new StringBuilder();
		if (actor.stories.size() > 0) {
			for (FullStoryDTO story : actor.stories) {
				String tStoryPart = "\"" + story.actorName + "\", \"" + story.action + "\", \"" + story.scenario + "\"";
				if (story.features.size() > 0) {
					for (FullFeatureDTO feature : story.features) {
						String featurePart = tStoryPart + ", \"[" + feature.type + "] " + feature.name + "\"";
						if (feature.tasks.size() > 0) {
							for (FullTaskDTO task : feature.tasks) {
								ans.append(featurePart).append(", \"").append(task.title).append("\"\n");
							}
						} else {
							ans.append(featurePart).append("\n");
						}
					}
				} else {
					ans.append(tStoryPart).append("\n");
				}
			}
		} else {
			ans.append("\"").append(actor.name).append("\" \n");
		}
		return ans.toString();
	}

	public List<Project> getAll() {
		return repo.findAll();
	}

	public void safeCreateProject(Project projectCanditate) throws DataConflictException, DataDefinitionException {
		List<Project> prjs = null;
		if (projectCanditate.name != null) {
			prjs = repo.findByName(projectCanditate.name);
		} else if (projectCanditate.code != null) {
			prjs = repo.findByCode(projectCanditate.code);
			projectCanditate.name = projectCanditate.code;
		} else {
			throw new DataDefinitionException("Missing project name and code");
		}

		if (prjs.size() != 0) {
			throw new DataConflictException("Project already exists");
		} else {
			repo.save(projectCanditate);
		}
	}

	public String tree(String projectRef) {
		FullProjectDTO tAns = objectTree(projectRef);
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
				.create();
		return gson.toJson(tAns);
	}

	public String createStory(Project project, UserStoryDTO storyDTO) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Story newStory = new Story();
		List<Actor> actors = actorRepisitory.findByProjectName(project.name);
		boolean actorFound = false;

		for (Actor tActor : actors) {
			if (tActor.name.equalsIgnoreCase(storyDTO.actor)) {
				newStory.actorId = tActor.id;
				actorFound = true;
				break;
			}
		}

		if (!actorFound) {
			Actor newActor = createActor(project, storyDTO.actor);
			newStory.actorId = newActor.id;
		}

		newStory.action = storyDTO.action;
		newStory.scenario = storyDTO.scenario;
		newStory.objective = storyDTO.objective;
		Story ans = storyRepository.save(newStory);
		return gson.toJson(ans);
	}

	public String csv(String projectRef) {
		FullProjectDTO project = objectTree(projectRef);
		StringBuilder ans = new StringBuilder();
		for (FullActorDTO actor : project.actors) {
			ans.append(actorToCsv(actor)).append("\n");
		}
		return ans.toString();
	}

	public String csvTasks(String projectRef) {
		Project prj = accessService.findProject(projectRef);
		List<Task> tasks = taskRepository.findByProjectId(prj.id);
		StringBuilder ans = new StringBuilder();

		for (Task task : tasks) {
			ans.append(task.title).append(", \"").append(task.description).append("\"\n");
		}

		List<Actor> actors = actorRepisitory.findByProjectName(prj.name);
		for (Actor actor : actors) {
			List<Story> stories = storyRepository.findByActorId(actor.id);
			for (Story story : stories) {
				List<Feature> features = featureRepository.findByStoryId(story.id);
				for (Feature feature : features) {
					List<Task> tasksOfFeature = taskRepository.findByIdReference("feature/" + feature.id);
					for (Task taskOfFeature : tasksOfFeature) {
						ans.append(taskOfFeature.title).append(", \"").append(taskOfFeature.description).append("\"\n");
					}
				}
			}
		}
		return ans.toString();
	}

	public List<FeatureTreeDTO> featureTree(String projectRef) {
		ArrayList<FeatureTreeDTO> ans = new ArrayList<>();
		Project prj = accessService.findProject(projectRef);
		List<Feature> features = featureService.getFeatures(prj);
		List<FeatureTreeDTO> allDtos = new ArrayList<>();

		for (Feature feature : features) {
			FeatureTreeDTO tmpDTO = new FeatureTreeDTO();
			tmpDTO.id = feature.id;
			tmpDTO.name = feature.name;
			tmpDTO.description = feature.description;
			tmpDTO.parentID = feature.parentID;
			tmpDTO.type = feature.type;
			allDtos.add(tmpDTO);
		}
		return order(allDtos);
	}

	private List<FeatureTreeDTO> order(List<FeatureTreeDTO> in) {
		return new ArrayList<>();
	}
}
