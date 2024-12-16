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
	private final ActorRepository actorRepository;
	private final TaskRepository taskRepository;
	private final StoryRepository storyRepository;
	private final FeatureRepository featureRepository;
	private final FeatureService featureService;
	private final DataAccessService accessService;

	@Autowired
	public ProjectService(ProjectRepository repo, ActorRepository actorRepository, TaskRepository taskRepository,
			StoryRepository storyRepository, FeatureRepository featureRepository, FeatureService featureService,
			DataAccessService accessService) {
		this.repo = repo;
		this.actorRepository = actorRepository;
		this.taskRepository = taskRepository;
		this.storyRepository = storyRepository;
		this.featureRepository = featureRepository;
		this.featureService = featureService;
		this.accessService = accessService;
	}

	public ProjectRepository getRepo() {
		return repo;
	}

	public ActorRepository getActorRepository() {
		return actorRepository;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	public StoryRepository getStoryRepository() {
		return storyRepository;
	}

	public FeatureRepository getFeatureRepository() {
		return featureRepository;
	}

	public FeatureService getFeatureService() {
		return featureService;
	}

	public DataAccessService getAccessService() {
		return accessService;
	}

	private FullProjectDTO objectTree(String projectRef) {
		Project p = accessService.findProject(projectRef);
		if (p == null) {
			return null;
		}

		FullProjectDTO projectDTO = mapProjectToDTO(p);
		List<Actor> actors = actorRepository.findByProjectName(p.getName());
		for (Actor a : actors) {
			FullActorDTO actorDTO = mapActorToDTO(a, projectDTO.getCode());
			projectDTO.getActors().add(actorDTO);
		}
		return projectDTO;
	}

	private FullProjectDTO mapProjectToDTO(Project p) {
		FullProjectDTO projectDTO = new FullProjectDTO();
		projectDTO.setId(p.getId());
		projectDTO.setName(p.getName());
		projectDTO.setDescription(p.getDescription());
		projectDTO.setClientName(p.getClientName());
		projectDTO.setCreationDate(p.getCreationDate());
		projectDTO.setCode(p.getCode());
		return projectDTO;
	}

	private FullActorDTO mapActorToDTO(Actor a, String projectCode) {
		FullActorDTO actorDTO = new FullActorDTO();
		actorDTO.setId(a.getId());
		actorDTO.setName(a.getName());
		actorDTO.setDescription(a.getDescription());
		actorDTO.setType(a.getType());

		List<Story> stories = storyRepository.findByActorId(a.getId());
		for (Story s : stories) {
			FullStoryDTO storyDTO = mapStoryToDTO(s, projectCode, actorDTO.getName());
			actorDTO.getStories().add(storyDTO);
		}
		return actorDTO;
	}

	private FullStoryDTO mapStoryToDTO(Story s, String projectCode, String actorName) {
		FullStoryDTO storyDTO = new FullStoryDTO();
		storyDTO.setId(s.getId());
		storyDTO.setProjectCode(projectCode);
		storyDTO.setActorName(actorName);
		storyDTO.setAction(s.getAction());
		storyDTO.setObjective(s.getObjective());
		storyDTO.setScenario(s.getScenario());

		List<Feature> features = featureRepository.findByStoryId(s.getId());
		for (Feature f : features) {
			FullFeatureDTO fullFeatureDTO = mapFeatureToDTO(f);
			storyDTO.getFeatures().add(fullFeatureDTO);
		}
		return storyDTO;
	}

	private FullFeatureDTO mapFeatureToDTO(Feature f) {
		FullFeatureDTO fullFeatureDTO = new FullFeatureDTO();
		fullFeatureDTO.setId(f.getId());
		fullFeatureDTO.setName(f.getName());
		fullFeatureDTO.setDescription(f.getDescription());
		fullFeatureDTO.setType(f.getType());
		fullFeatureDTO.setParentID(f.getParentID());

		List<Task> tasks = taskRepository.findByIdReference("feature/" + fullFeatureDTO.getId());
		for (Task task : tasks) {
			FullTaskDTO taskDTO = mapTaskToDTO(task);
			fullFeatureDTO.getTasks().add(taskDTO);
		}
		return fullFeatureDTO;
	}

	private FullTaskDTO mapTaskToDTO(Task task) {
		FullTaskDTO taskDTO = new FullTaskDTO();
		taskDTO.setId(task.getId());
		taskDTO.setProjectId(task.getProjectId());
		taskDTO.setReference(task.getReference());
		taskDTO.setTitle(task.getTitle());
		taskDTO.setDescription(task.getDescription());
		taskDTO.setDueDate(task.getDueDate());
		taskDTO.setDoneDate(task.getDoneDate());
		taskDTO.setIdReference(task.getIdReference());
		taskDTO.setName(task.getName());
		taskDTO.setCreationDate(task.getCreationDate());
		taskDTO.setLastUpdateDate(task.getLastUpdateDate());
		return taskDTO;
	}

	private Actor createActor(Project p, String actorName) {
		Actor actor = new Actor();
		actor.setProjectName(p.getName());
		actor.setName(actorName.toLowerCase());
		return actorRepository.save(actor);
	}

	private String actorToCsv(FullActorDTO actor) {
		StringBuilder ans = new StringBuilder();
		if (actor.getStories().size() > 0) {
			for (FullStoryDTO story : actor.getStories()) {
				String tStoryPart = "\"" + story.getActorName() + "\", \"" + story.getAction() + "\", \""
						+ story.getScenario() + "\"";
				if (story.getFeatures().size() > 0) {
					for (FullFeatureDTO feature : story.getFeatures()) {
						String featurePart = tStoryPart + ", \"[" + feature.getType() + "] " + feature.getName() + "\"";
						if (feature.getTasks().size() > 0) {
							for (FullTaskDTO task : feature.getTasks()) {
								ans.append(featurePart).append(", \"").append(task.getTitle()).append("\"\n");
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
			ans.append("\"").append(actor.getName()).append("\" \n");
		}
		return ans.toString();
	}

	public List<Project> getAll() {
		return repo.findAll();
	}

	public void safeCreateProject(Project projectCanditate) throws DataConflictException, DataDefinitionException {
		List<Project> prjs = null;
		if (projectCanditate.getName() != null) {
			prjs = repo.findByName(projectCanditate.getName());
		} else if (projectCanditate.getCode() != null) {
			prjs = repo.findByCode(projectCanditate.getCode());
			projectCanditate.setName(projectCanditate.getCode());
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
		List<Actor> actors = actorRepository.findByProjectName(project.getName());
		boolean actorFound = false;

		for (Actor tActor : actors) {
			if (tActor.getName().equalsIgnoreCase(storyDTO.getActor())) {
				newStory.setActorId(tActor.getId());
				actorFound = true;
				break;
			}
		}

		if (!actorFound) {
			Actor newActor = createActor(project, storyDTO.getActor());
			newStory.setActorId(newActor.getId());
		}

		newStory.setAction(storyDTO.getAction());
		newStory.setScenario(storyDTO.getScenario());
		newStory.setObjective(storyDTO.getObjective());
		Story ans = storyRepository.save(newStory);
		return gson.toJson(ans);
	}

	public String csv(String projectRef) {
		FullProjectDTO project = objectTree(projectRef);
		StringBuilder ans = new StringBuilder();
		for (FullActorDTO actor : project.getActors()) {
			ans.append(actorToCsv(actor)).append("\n");
		}
		return ans.toString();
	}

	public String csvTasks(String projectRef) {
		Project prj = accessService.findProject(projectRef);
		List<Task> tasks = taskRepository.findByProjectId(prj.getId());
		StringBuilder ans = new StringBuilder();

		for (Task task : tasks) {
			ans.append(task.getTitle()).append(", \"").append(task.getDescription()).append("\"\n");
		}

		List<Actor> actors = actorRepository.findByProjectName(prj.getName());
		for (Actor actor : actors) {
			List<Story> stories = storyRepository.findByActorId(actor.getId());
			for (Story story : stories) {
				List<Feature> features = featureRepository.findByStoryId(story.getId());
				for (Feature feature : features) {
					List<Task> tasksOfFeature = taskRepository.findByIdReference("feature/" + feature.getId());
					for (Task taskOfFeature : tasksOfFeature) {
						ans.append(taskOfFeature.getTitle()).append(", \"").append(taskOfFeature.getDescription())
								.append("\"\n");
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
			tmpDTO.setId(feature.getId());
			tmpDTO.setName(feature.getName());
			tmpDTO.setDescription(feature.getDescription());
			tmpDTO.setParentID(feature.getParentID());
			tmpDTO.setType(feature.getType());
			allDtos.add(tmpDTO);
		}
		return order(allDtos);
	}

	private List<FeatureTreeDTO> order(List<FeatureTreeDTO> in) {
		return new ArrayList<>();
	}
}
