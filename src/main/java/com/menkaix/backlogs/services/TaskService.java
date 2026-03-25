package com.menkaix.backlogs.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.FeatureType;
import com.menkaix.backlogs.models.entities.Story;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.models.values.TaskStatus;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.repositories.TaskRepository;

@Service
public class TaskService {

	private static Logger logger = LoggerFactory.getLogger(TaskService.class);
	private final TaskRepository repository;
	private final FeatureTypeService featureTypeService;
	private final MongoTemplate mongoTemplate;
	private final ProjectTouchService projectTouchService;
	private final FeatureRepository featureRepository;
	private final StoryRepository storyRepository;
	private final ActorRepository actorRepository;
	private final ProjectRepository projectRepository;

	@Autowired
	public TaskService(TaskRepository repository, FeatureTypeService featureTypeService,
			MongoTemplate mongoTemplate, ProjectTouchService projectTouchService,
			FeatureRepository featureRepository, StoryRepository storyRepository,
			ActorRepository actorRepository, ProjectRepository projectRepository) {
		this.repository = repository;
		this.featureTypeService = featureTypeService;
		this.mongoTemplate = mongoTemplate;
		this.projectTouchService = projectTouchService;
		this.featureRepository = featureRepository;
		this.storyRepository = storyRepository;
		this.actorRepository = actorRepository;
		this.projectRepository = projectRepository;
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
			projectTouchService.touchByFeatureId(feature.getId());
		} else {
			logger.error("no feature type named " + feature.getType());
		}
	}

	public Task create(Task task) {
		Task saved = repository.save(task);
		projectTouchService.touchByTask(saved);
		return saved;
	}

	public Task addNewTaskToFeature(String featureId, Task task) {
		task.setIdReference("feature/" + featureId);
		Task saved = repository.save(task);
		projectTouchService.touchByFeatureId(featureId);
		return saved;
	}

	public Task assignTaskToFeature(String featureId, String taskId) {
		Task task = repository.findById(taskId)
				.orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
		task.setIdReference("feature/" + featureId);
		if (task.getReference() != null && task.getReference().split("/").length == 3) {
			String taskKey = task.getReference().split("/")[2];
			task.setReference("feature/" + featureId + "/" + taskKey);
		}
		Task saved = repository.save(task);
		projectTouchService.touchByFeatureId(featureId);
		return saved;
	}

	public Task assignPerson(String taskId, String email) {
		Task task = repository.findById(taskId)
				.orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
		if (!task.getAssignees().contains(email)) {
			task.getAssignees().add(email);
			Task saved = repository.save(task);
			projectTouchService.touchByTask(saved);
			return saved;
		}
		return task;
	}

	public Task unassignPerson(String taskId, String email) {
		Task task = repository.findById(taskId)
				.orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));
		task.getAssignees().remove(email);
		Task saved = repository.save(task);
		projectTouchService.touchByTask(saved);
		return saved;
	}

	public Optional<Task> findById(String id) {
		return repository.findById(id);
	}

	public Optional<Task> findByTrackingReference(String trackingReference) {
		return repository.findByTrackingReference(trackingReference);
	}

	public Task update(Task task) {
		Task saved = repository.save(task);
		projectTouchService.touchByTask(saved);
		return saved;
	}

	/**
	 * Met à jour uniquement le statut d'une tâche.
	 * Gère automatiquement les dates associées :
	 * - startDate : renseignée automatiquement au premier passage en IN_PROGRESS
	 * - doneDate  : renseignée automatiquement au passage en DONE, effacée si la tâche est rouverte
	 *
	 * @param taskId     identifiant de la tâche
	 * @param rawStatus  valeur brute du nouveau statut (tolerant aux variantes)
	 * @return la tâche mise à jour
	 * @throws NoSuchElementException si la tâche n'existe pas
	 * @throws IllegalArgumentException si le statut fourni est invalide
	 */
	public Task updateStatus(String taskId, String rawStatus) {
		Task task = repository.findById(taskId)
				.orElseThrow(() -> new NoSuchElementException("Task not found: " + taskId));

		TaskStatus newStatus = TaskStatus.normalize(rawStatus);
		if (newStatus == null || newStatus == TaskStatus.UNKNOWN) {
			throw new IllegalArgumentException("Statut inconnu : " + rawStatus);
		}

		task.setStatus(newStatus.name());

		Date now = new Date();
		if (newStatus == TaskStatus.IN_PROGRESS && task.getStartDate() == null) {
			task.setStartDate(now);
		}
		if (newStatus == TaskStatus.DONE && task.getDoneDate() == null) {
			task.setDoneDate(now);
		}
		if (newStatus != TaskStatus.DONE && task.getDoneDate() != null) {
			task.setDoneDate(null);
		}

		Task saved = repository.save(task);
		projectTouchService.touchByTask(saved);
		return saved;
	}

	public void delete(String id) {
		repository.findById(id).ifPresent(task -> {
			repository.delete(task);
			projectTouchService.touchByTask(task);
		});
	}

	private static final Sort LAST_UPDATE_DESC = Sort.by(Sort.Direction.DESC, "lastUpdateDate");

	// ── Recherche par projet (directe + indirecte via feature chain) ──────────

	public List<Task> findByProjectId(String projectId) {
		return repository.findByProjectIdOrderByLastUpdateDateDesc(projectId);
	}

	/**
	 * Retourne toutes les tâches d'un projet identifié par son nom, code ou id
	 * MongoDB. Inclut les tâches directes (champ projectId) et les tâches
	 * indirectes liées via la chaîne feature → story → actor → project.
	 */
	public List<Task> findByProjectRef(String projectRef) {
		List<Task> result = new ArrayList<>();

		projectRepository.findByName(projectRef).stream().findFirst()
				.or(() -> projectRepository.findByCode(projectRef).stream().findFirst())
				.or(() -> projectRepository.findById(projectRef))
				.ifPresent(project -> {
					// tâches directes
					result.addAll(repository.findByProjectId(project.getId()));
					// tâches indirectes : actors → stories → features → tasks
					actorRepository.findByProjectName(project.getName()).forEach(actor ->
						storyRepository.findByActorId(actor.getId()).forEach(story ->
							featureRepository.findByStoryId(story.getId()).forEach(feature ->
								result.addAll(repository.findByIdReference("feature/" + feature.getId()))
							)
						)
					);
				});

		result.sort(Comparator.comparing(Task::getLastUpdateDate,
				Comparator.nullsLast(Comparator.reverseOrder())));
		return result;
	}

	// ── Recherche par assignee ────────────────────────────────────────────────

	/**
	 * Retourne toutes les tâches où l'email donné figure dans la liste des
	 * assignees. Une tâche avec plusieurs assignees est retournée dès qu'un
	 * des assignees correspond.
	 */
	public List<Task> findByAssigneeEmail(String email) {
		return repository.findByAssigneesContainingOrderByLastUpdateDateDesc(email);
	}

	// ── Recherche par feature ─────────────────────────────────────────────────

	public List<Task> findByFeatureId(String featureId) {
		return repository.findByIdReferenceOrderByLastUpdateDateDesc("feature/" + featureId);
	}

	// ── Recherche par story ───────────────────────────────────────────────────

	public List<Task> findByStoryId(String storyId) {
		List<Task> result = new ArrayList<>();
		featureRepository.findByStoryId(storyId).forEach(feature ->
			result.addAll(repository.findByIdReference("feature/" + feature.getId()))
		);
		result.sort(Comparator.comparing(Task::getLastUpdateDate,
				Comparator.nullsLast(Comparator.reverseOrder())));
		return result;
	}

	// ── Recherche par actor ───────────────────────────────────────────────────

	public List<Task> findByActorId(String actorId) {
		List<Task> result = new ArrayList<>();
		storyRepository.findByActorId(actorId).forEach(story ->
			result.addAll(findByStoryId(story.getId()))
		);
		result.sort(Comparator.comparing(Task::getLastUpdateDate,
				Comparator.nullsLast(Comparator.reverseOrder())));
		return result;
	}

	public List<Task> findByStatus(String status) {
		TaskStatus normalized = TaskStatus.normalize(status);
		String key = normalized != null ? normalized.name() : status;
		return repository.findByStatusOrderByLastUpdateDateDesc(key);
	}

	public List<Task> findOverdueTasks() {
		Query query = new Query(Criteria.where("dueDate").lt(new Date()).and("doneDate").isNull())
				.with(LAST_UPDATE_DESC);
		return mongoTemplate.find(query, Task.class);
	}

	public List<Task> findUpcomingTasks() {
		Date now = new Date();
		Date sevenDaysLater = new Date(now.getTime() + 7L * 24 * 60 * 60 * 1000);
		Query query = new Query(Criteria.where("dueDate").gt(now).lt(sevenDaysLater).and("doneDate").isNull())
				.with(LAST_UPDATE_DESC);
		return mongoTemplate.find(query, Task.class);
	}

	public Page<Task> findAll(Pageable pageable, String search, String filter) {
		Query baseQuery = buildQuery(search, filter);
		Query countQuery = buildQuery(search, filter);
		Query dataQuery = Query.of(baseQuery).with(LAST_UPDATE_DESC);
		if (pageable.isPaged()) {
			dataQuery.skip(pageable.getOffset()).limit(pageable.getPageSize());
		}
		List<Task> tasks = mongoTemplate.find(dataQuery, Task.class);
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
