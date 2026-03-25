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
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.values.ProjectState;
import com.menkaix.backlogs.repositories.PeopleRepository;
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
	private final PeopleRepository peopleRepository;

	// Self-reference pour que @Cacheable soit intercepté par le proxy Spring AOP
	@Lazy
	@Autowired
	private ProjectService self;

	@Autowired
	public ProjectService(ProjectRepository repo, ActorRepository actorRepository, TaskRepository taskRepository,
			StoryRepository storyRepository, FeatureRepository featureRepository, FeatureService featureService,
			DataAccessService accessService, RaciRepository raciRepository, ProjectTouchService projectTouchService,
			PeopleRepository peopleRepository) {
		this.repo = repo;
		this.actorRepository = actorRepository;
		this.taskRepository = taskRepository;
		this.storyRepository = storyRepository;
		this.featureRepository = featureRepository;
		this.featureService = featureService;
		this.accessService = accessService;
		this.raciRepository = raciRepository;
		this.projectTouchService = projectTouchService;
		this.peopleRepository = peopleRepository;
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

	/**
	 * Retourne tous les projets dont le statut calculé correspond à l'état demandé.
	 *
	 * @param state état cible (ACTIVE, STANDBY ou CLOSED)
	 * @return liste des projets filtrés
	 */
	public List<Project> getByState(ProjectState state) {
		return repo.findAll(Sort.by(Sort.Direction.DESC, "lastUpdateDate")).stream()
				.filter(p -> {
					List<Task> tasks = taskRepository.findByProjectId(p.getId());
					return ProjectState.compute(tasks) == state;
				})
				.collect(java.util.stream.Collectors.toList());
	}

	/**
	 * Met à jour la phase d'un projet.
	 *
	 * @param projectRef nom, code ou id MongoDB du projet
	 * @param phase      nouvelle phase
	 * @return le projet mis à jour
	 * @throws EntityNotFoundException si le projet n'existe pas
	 */
	public Project updatePhase(String projectRef, com.menkaix.backlogs.models.values.ProjectPhase phase)
			throws EntityNotFoundException {
		Project project;
		try {
			project = accessService.findProject(projectRef);
		} catch (java.util.NoSuchElementException e) {
			throw new EntityNotFoundException("Project not found: " + projectRef);
		}
		project.setPhase(phase);
		Project saved = repo.save(project);
		projectTouchService.touch(saved);
		return saved;
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
		projectDTO.setPhase(p.getPhase());
		projectDTO.setStatus(ProjectState.compute(taskRepository.findByProjectId(p.getId())));
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
		taskDTO.setPlannedStart(task.getPlannedStart());
		taskDTO.setStartDate(task.getStartDate());
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

	// ── Gestion de l'équipe projet ────────────────────────────────────────────

	/**
	 * Retourne l'équipe d'un projet.
	 *
	 * @param projectRef nom, code ou id MongoDB du projet
	 */
	public List<ProjectMember> getTeam(String projectRef) {
		Project project = accessService.findProject(projectRef);
		return project.getTeam();
	}

	/**
	 * Ajoute une personne à l'équipe d'un projet.
	 * Les informations identitaires et le skillset sont dénormalisés depuis People.
	 *
	 * @param projectRef nom, code ou id MongoDB du projet
	 * @param personId   id MongoDB de la personne à ajouter
	 * @return le projet mis à jour
	 */
	public Project addTeamMember(String projectRef, String personId) {
		Project project = accessService.findProject(projectRef);
		com.menkaix.backlogs.models.entities.People person = peopleRepository.findById(personId)
				.orElseThrow(() -> new java.util.NoSuchElementException("Personne introuvable : " + personId));

		boolean alreadyMember = project.getTeam().stream()
				.anyMatch(m -> m.getPersonId().equals(personId));
		if (alreadyMember) {
			throw new IllegalArgumentException("Cette personne fait déjà partie de l'équipe du projet");
		}

		ProjectMember member = new ProjectMember();
		member.setPersonId(person.getId());
		member.setFirstName(person.getFirstName());
		member.setLastName(person.getLastName());
		member.setEmail(person.getEmail());
		member.setSkills(person.getSkills());

		project.getTeam().add(member);
		Project saved = repo.save(project);
		projectTouchService.touch(saved);
		return saved;
	}

	/**
	 * Retire une personne de l'équipe d'un projet.
	 *
	 * @param projectRef nom, code ou id MongoDB du projet
	 * @param personId   id MongoDB de la personne à retirer
	 * @return le projet mis à jour
	 */
	public Project removeTeamMember(String projectRef, String personId) {
		Project project = accessService.findProject(projectRef);
		boolean removed = project.getTeam().removeIf(m -> m.getPersonId().equals(personId));
		if (!removed) {
			throw new java.util.NoSuchElementException("Personne introuvable dans l'équipe du projet : " + personId);
		}
		Project saved = repo.save(project);
		projectTouchService.touch(saved);
		return saved;
	}

	/**
	 * Synchronise les données dénormalisées d'un membre de l'équipe
	 * (nom, email, skills) depuis le document People en base.
	 *
	 * @param projectRef nom, code ou id MongoDB du projet
	 * @param personId   id MongoDB de la personne à rafraîchir
	 * @return le projet mis à jour
	 */
	public Project refreshTeamMemberSkills(String projectRef, String personId) {
		Project project = accessService.findProject(projectRef);
		com.menkaix.backlogs.models.entities.People person = peopleRepository.findById(personId)
				.orElseThrow(() -> new java.util.NoSuchElementException("Personne introuvable : " + personId));

		ProjectMember member = project.getTeam().stream()
				.filter(m -> m.getPersonId().equals(personId))
				.findFirst()
				.orElseThrow(() -> new java.util.NoSuchElementException("Personne introuvable dans l'équipe du projet : " + personId));

		member.setFirstName(person.getFirstName());
		member.setLastName(person.getLastName());
		member.setEmail(person.getEmail());
		member.setSkills(person.getSkills());

		Project saved = repo.save(project);
		projectTouchService.touch(saved);
		return saved;
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
