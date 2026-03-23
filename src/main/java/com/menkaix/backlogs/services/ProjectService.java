package com.menkaix.backlogs.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.menkaix.backlogs.models.dto.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import com.menkaix.backlogs.models.entities.Actor;
import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Raci;
import com.menkaix.backlogs.models.entities.Story;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.repositories.TaskRepository;

import com.menkaix.backlogs.services.applicatif.DataAccessService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.repositories.RaciRepository;
import com.menkaix.backlogs.utilities.exceptions.DataConflictException;
import com.menkaix.backlogs.utilities.exceptions.DataDefinitionException;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import com.menkaix.backlogs.services.ProjectTouchService;

@Service
public class ProjectService {

	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
			.create();

	private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

	private final ProjectRepository repo;
	private final ActorRepository actorRepository;
	private final TaskRepository taskRepository;
	private final StoryRepository storyRepository;
	private final FeatureRepository featureRepository;
	private final FeatureService featureService;
	private final DataAccessService accessService;
	private final RaciRepository raciRepository;
	private final ProjectTouchService projectTouchService;

	// Self-reference pour que @Cacheable soit intercepté par le proxy Spring AOP
	@Lazy
	@Autowired
	private ProjectService self;

	@Autowired
	public ProjectService(ProjectRepository repo, ActorRepository actorRepository, TaskRepository taskRepository,
			StoryRepository storyRepository, FeatureRepository featureRepository, FeatureService featureService,
			DataAccessService accessService, RaciRepository raciRepository, ProjectTouchService projectTouchService) {
		this.repo = repo;
		this.actorRepository = actorRepository;
		this.taskRepository = taskRepository;
		this.storyRepository = storyRepository;
		this.featureRepository = featureRepository;
		this.featureService = featureService;
		this.accessService = accessService;
		this.raciRepository = raciRepository;
		this.projectTouchService = projectTouchService;
	}

	// Méthodes getter pour les repositories et services
	public ActorRepository getActorRepository() {
		return actorRepository;
	}

	public DataAccessService getAccessService() {
		return accessService;
	}

	public FeatureRepository getFeatureRepository() {
		return featureRepository;
	}

	public FeatureService getFeatureService() {
		return featureService;
	}

	public ProjectRepository getRepo() {
		return repo;
	}

	public StoryRepository getStoryRepository() {
		return storyRepository;
	}

	public TaskRepository getTaskRepository() {
		return taskRepository;
	}

	// Méthode pour obtenir l'arbre des fonctionnalités d'un projet
	public List<FeatureTreeDTO> featureTree(String projectRef) {

		Project prj;
		try {
			prj = accessService.findProject(projectRef);
		} catch (NoSuchElementException e) {
			logger.error("Project not found: {}", projectRef, e);
			throw new NoSuchElementException("Project not found: " + projectRef);
		}
		List<Feature> features = featureService.getFeatures(prj);
		features.sort(Comparator.comparing(Feature::getLastUpdateDate, Comparator.nullsLast(Comparator.reverseOrder())));
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

	// Méthode pour obtenir tous les projets
	public List<Project> getAll() {
		return repo.findAll(Sort.by(Sort.Direction.DESC, "lastUpdateDate"));
	}

	// Méthode pour créer un projet en toute sécurité
	public void safeCreateProject(Project projectCanditate) throws DataConflictException, DataDefinitionException {
		List<Project> prjs = null;
		if (projectCanditate.getName() != null) {
			prjs = repo.findByName(projectCanditate.getName());
		} else if (projectCanditate.getCode() != null) {
			prjs = repo.findByCode(projectCanditate.getCode());
			projectCanditate.setName(projectCanditate.getCode());
		} else {
			logger.error("Missing project name and code");
			throw new DataDefinitionException("Missing project name and code");
		}

		if (prjs.size() != 0) {
			logger.error("Project already exists: {}", projectCanditate.getName());
			throw new DataConflictException("Project already exists");
		} else {
			repo.save(projectCanditate);
			logger.info("Project created successfully: {}", projectCanditate.getName());
		}
	}

	// Méthode pour créer une histoire utilisateur
	public String createStory(Project project, UserStoryDTO storyDTO) {
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
		projectTouchService.touch(project);
		logger.info("Story created successfully for project {}: {}", project.getName(), newStory.getId());
		return GSON.toJson(ans);
	}

	// Méthode pour générer un CSV des acteurs d'un projet
	public String csv(String projectRef) {
		FullProjectDTO project;
		try {
			project = self.objectTree(projectRef);
		} catch (NoSuchElementException e) {
			logger.error("Project not found: {}", projectRef, e);
			throw new NoSuchElementException("Project not found: " + projectRef);
		}
		StringBuilder ans = new StringBuilder();
		for (FullActorDTO actor : project.getActors()) {
			ans.append(actorToCsv(actor)).append("\n");
		}
		return ans.toString();
	}

	// Méthode pour générer un CSV des tâches d'un projet
	public String csvTasks(String projectRef) {
		Project prj;
		try {
			prj = accessService.findProject(projectRef);
		} catch (NoSuchElementException e) {
			logger.error("Project not found: {}", projectRef, e);
			throw new NoSuchElementException("Project not found: " + projectRef);
		}
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

	// Méthode pour obtenir l'arbre d'un projet au format JSON
	public String tree(String projectRef) {
		FullProjectDTO tAns;
		try {
			tAns = self.objectTree(projectRef);
		} catch (NoSuchElementException e) {
			logger.error("Project not found: {}", projectRef, e);
			throw new NoSuchElementException("Project not found: " + projectRef);
		}
		return GSON.toJson(tAns);
	}

	// Méthode privée pour créer un acteur
	private Actor createActor(Project p, String actorName) {
		Actor actor = new Actor();
		actor.setProjectName(p.getName());
		actor.setName(actorName.toLowerCase());
		return actorRepository.save(actor);
	}

	// Méthode privée pour mapper un projet à un DTO
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

	// Méthode privée pour mapper une tâche à un DTO
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
		taskDTO.setStatus(task.getStatus());
		return taskDTO;
	}

	// Construit l'arbre complet du projet en 4 requêtes MongoDB (bulk loading)
	public FullProjectDTO objectTree(String projectRef) {
		Project p = accessService.findProject(projectRef);
		if (p == null) {
			logger.error("Project not found: {}", projectRef);
			throw new NoSuchElementException("Project not found: " + projectRef);
		}

		// 4 requêtes au total au lieu de N*M*K
		List<Actor> actors = actorRepository.findByProjectName(p.getName());
		List<String> actorIds = actors.stream().map(Actor::getId).toList();

		List<Story> allStories = storyRepository.findByActorIdIn(actorIds);
		List<String> storyIds = allStories.stream().map(Story::getId).toList();
		Map<String, List<Story>> storiesByActorId = allStories.stream()
				.collect(Collectors.groupingBy(Story::getActorId));

		List<Feature> allFeatures = featureRepository.findByStoryIdIn(storyIds);
		List<String> featureRefs = allFeatures.stream().map(f -> "feature/" + f.getId()).toList();
		Map<String, List<Feature>> featuresByStoryId = allFeatures.stream()
				.collect(Collectors.groupingBy(Feature::getStoryId));

		List<Task> allTasks = taskRepository.findByIdReferenceIn(featureRefs);
		Map<String, List<Task>> tasksByFeatureRef = allTasks.stream()
				.collect(Collectors.groupingBy(Task::getIdReference));

		// Assemblage en mémoire
		FullProjectDTO projectDTO = mapProjectToDTO(p);
		for (Actor a : actors) {
			FullActorDTO actorDTO = new FullActorDTO();
			actorDTO.setId(a.getId());
			actorDTO.setName(a.getName());
			actorDTO.setDescription(a.getDescription());
			actorDTO.setType(a.getType());

			for (Story s : storiesByActorId.getOrDefault(a.getId(), List.of())) {
				FullStoryDTO storyDTO = new FullStoryDTO();
				storyDTO.setId(s.getId());
				storyDTO.setProjectCode(projectDTO.getCode());
				storyDTO.setActorName(actorDTO.getName());
				storyDTO.setAction(s.getAction());
				storyDTO.setObjective(s.getObjective());
				storyDTO.setScenario(s.getScenario());

				for (Feature f : featuresByStoryId.getOrDefault(s.getId(), List.of())) {
					FullFeatureDTO featureDTO = new FullFeatureDTO();
					featureDTO.setId(f.getId());
					featureDTO.setName(f.getName());
					featureDTO.setDescription(f.getDescription());
					featureDTO.setType(f.getType());
					featureDTO.setParentID(f.getParentID());

					for (Task t : tasksByFeatureRef.getOrDefault("feature/" + f.getId(), List.of())) {
						featureDTO.getTasks().add(mapTaskToDTO(t));
					}
					storyDTO.getFeatures().add(featureDTO);
				}
				actorDTO.getStories().add(storyDTO);
			}
			projectDTO.getActors().add(actorDTO);
		}
		return projectDTO;
	}

	// Méthode privée pour ordonner une liste de fonctionnalités
	private List<FeatureTreeDTO> order(List<FeatureTreeDTO> in) {
		return in;
	}

	// Méthode privée pour convertir un acteur en CSV
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

	// Méthode pour fusionner deux listes de chaînes de caractères
	public List<String> merge(List<String> a, List<String> b) {

		if (b == null)
			return a;

		for (String c : b) {
			if (!a.contains(c)) {
				a.add(c);
			}
		}
		return a;
	}

	// Méthode pour ajouter un RACI à un projet
	public RaciDTO addRaci(String project, RaciDTO raciDTO) throws EntityNotFoundException {
		Project prj = accessService.findProject(project);

		if (prj == null)
			throw new EntityNotFoundException(project);

		Raci raci = raciRepository.findByprojectID(prj.code);

		if (raci == null) {
			raci = new Raci();
			raci.setprojectID(prj.code);
		}

		raci.setResponsible(merge(raci.getResponsible(), raciDTO.getR()));
		raci.setAccountable(merge(raci.getAccountable(), raciDTO.getA()));
		raci.setConsulted(merge(raci.getConsulted(), raciDTO.getC()));
		raci.setInformed(merge(raci.getInformed(), raciDTO.getI()));

		Raci saved = raciRepository.save(raci);

		raciDTO.setProjectCode(prj.code);
		raciDTO.setR(saved.getResponsible());
		raciDTO.setA(saved.getAccountable());
		raciDTO.setC(saved.getConsulted());
		raciDTO.setI(saved.getInformed());

		return raciDTO;

	}
}
