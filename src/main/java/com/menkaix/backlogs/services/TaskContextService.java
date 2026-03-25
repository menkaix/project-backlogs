package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.dto.TaskContextDTO;
import com.menkaix.backlogs.models.dto.TaskContextDTO.*;
import com.menkaix.backlogs.models.entities.*;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.repositories.*;
import com.menkaix.backlogs.services.applicatif.DataAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Assemble le contexte complet d'une tâche pour usage LLM ou client REST logique.
 *
 * Résolution de la lignée :
 *   Task.idReference = "feature/{featureId}"
 *     → Feature.storyId → Story.actorId → Actor.projectName
 *     → Project (via DataAccessService)
 *
 * Si l'une des étapes est absente (tâche sans feature, sans story, etc.),
 * la portion correspondante du contexte est simplement nulle.
 */
@Service
public class TaskContextService {

    private static final Logger logger = LoggerFactory.getLogger(TaskContextService.class);

    private final TaskRepository taskRepository;
    private final FeatureRepository featureRepository;
    private final StoryRepository storyRepository;
    private final ActorRepository actorRepository;
    private final ProjectRepository projectRepository;
    private final DataAccessService dataAccessService;

    public TaskContextService(
            TaskRepository taskRepository,
            FeatureRepository featureRepository,
            StoryRepository storyRepository,
            ActorRepository actorRepository,
            ProjectRepository projectRepository,
            DataAccessService dataAccessService) {
        this.taskRepository = taskRepository;
        this.featureRepository = featureRepository;
        this.storyRepository = storyRepository;
        this.actorRepository = actorRepository;
        this.projectRepository = projectRepository;
        this.dataAccessService = dataAccessService;
    }

    public TaskContextDTO buildContext(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Tâche introuvable : " + taskId));

        TaskContextDTO ctx = new TaskContextDTO();
        ctx.setTask(toTaskSnapshot(task));

        // ── Résolution de la lignée ──────────────────────────────────────────────
        Feature feature = resolveFeature(task);
        Story story = null;
        Actor actor = null;
        Project project = null;

        if (feature != null) {
            ctx.setFeature(toFeatureSnapshot(feature));
            ctx.setSiblingTasks(resolveSiblingTasks(feature.getId(), taskId));

            story = resolveStory(feature);
            if (story != null) {
                ctx.setStory(toStorySnapshot(story));
                actor = resolveActor(story);
                if (actor != null) {
                    ctx.setActor(toActorSnapshot(actor));
                    project = resolveProjectByActor(actor);
                }
            }
        }

        // Fallback : résolution du projet via task.projectId si la lignée n'a pas suffi
        if (project == null && task.getProjectId() != null) {
            project = projectRepository.findById(task.getProjectId()).orElse(null);
        }

        if (project != null) {
            ctx.setProject(toProjectSnapshot(project));
            ctx.setTeam(project.getTeam());
        }

        return ctx;
    }

    // ─── Résolution de la lignée ─────────────────────────────────────────────────

    private Feature resolveFeature(Task task) {
        String ref = task.getIdReference();
        if (ref == null || !ref.startsWith("feature/")) return null;
        String featureId = ref.substring("feature/".length());
        return featureRepository.findById(featureId).orElse(null);
    }

    private Story resolveStory(Feature feature) {
        if (feature.getStoryId() == null) return null;
        return storyRepository.findById(feature.getStoryId()).orElse(null);
    }

    private Actor resolveActor(Story story) {
        if (story.getActorId() == null) return null;
        return actorRepository.findById(story.getActorId()).orElse(null);
    }

    private Project resolveProjectByActor(Actor actor) {
        if (actor.getProjectName() == null) return null;
        return dataAccessService.findProject(actor.getProjectName());
    }

    private List<TaskSnapshot> resolveSiblingTasks(String featureId, String excludeTaskId) {
        return taskRepository.findByIdReference("feature/" + featureId).stream()
                .filter(t -> !t.getId().equals(excludeTaskId))
                .map(this::toTaskSnapshot)
                .toList();
    }

    // ─── Mapping vers snapshots ──────────────────────────────────────────────────

    private TaskSnapshot toTaskSnapshot(Task t) {
        TaskSnapshot s = new TaskSnapshot();
        s.setId(t.getId());
        s.setTitle(t.getTitle());
        s.setDescription(t.getDescription());
        s.setStatus(t.getStatus());
        s.setEstimate(t.getEstimate());
        s.setEstimatedManHours(t.getEstimatedManHours());
        s.setAssignees(t.getAssignees());
        s.setReference(t.getReference());
        s.setIdReference(t.getIdReference());
        s.setTrackingReference(t.getTrackingReference());
        s.setPlannedStart(t.getPlannedStart());
        s.setDueDate(t.getDueDate());
        s.setStartDate(t.getStartDate());
        s.setDoneDate(t.getDoneDate());
        return s;
    }

    private ProjectSnapshot toProjectSnapshot(Project p) {
        ProjectSnapshot s = new ProjectSnapshot();
        s.setId(p.getId());
        s.setName(p.getName());
        s.setCode(p.getCode());
        s.setDescription(p.getDescription());
        s.setClientName(p.getClientName());
        s.setPhase(p.getPhase());
        return s;
    }

    private FeatureSnapshot toFeatureSnapshot(Feature f) {
        FeatureSnapshot s = new FeatureSnapshot();
        s.setId(f.getId());
        s.setName(f.getName());
        s.setDescription(f.getDescription());
        s.setType(f.getType());
        s.setParentID(f.getParentID());
        return s;
    }

    private StorySnapshot toStorySnapshot(Story st) {
        StorySnapshot s = new StorySnapshot();
        s.setId(st.getId());
        s.setAction(st.getAction());
        s.setObjective(st.getObjective());
        s.setScenario(st.getScenario());
        return s;
    }

    private ActorSnapshot toActorSnapshot(Actor a) {
        ActorSnapshot s = new ActorSnapshot();
        s.setId(a.getId());
        s.setName(a.getName());
        s.setDescription(a.getDescription());
        s.setType(a.getType() != null ? a.getType().name() : null);
        return s;
    }
}
