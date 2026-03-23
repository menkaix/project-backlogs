package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Task;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Touches (saves) the parent project of any entity so that
 * {@code @LastModifiedDate} on the project is updated whenever a
 * related entity (task, feature, story, actor, comment, link…) is
 * created, modified or deleted.
 */
@Service
public class ProjectTouchService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectTouchService.class);

    private final ProjectRepository projectRepository;
    private final ActorRepository actorRepository;
    private final StoryRepository storyRepository;
    private final FeatureRepository featureRepository;

    @Autowired
    @Lazy
    private ProjectTouchService self;

    public ProjectTouchService(ProjectRepository projectRepository,
                               ActorRepository actorRepository,
                               StoryRepository storyRepository,
                               FeatureRepository featureRepository) {
        this.projectRepository = projectRepository;
        this.actorRepository = actorRepository;
        this.storyRepository = storyRepository;
        this.featureRepository = featureRepository;
    }

    /** Touch via a direct project id. */
    public void touchByProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) return;
        projectRepository.findById(projectId).ifPresentOrElse(
                self::touch,
                () -> logger.warn("ProjectTouchService: project not found for id={}", projectId));
    }

    /** Touch via a project name. */
    public void touchByProjectName(String projectName) {
        if (projectName == null || projectName.isBlank()) return;
        projectRepository.findByName(projectName).stream().findFirst().ifPresentOrElse(
                self::touch,
                () -> logger.warn("ProjectTouchService: project not found for name={}", projectName));
    }

    /** Touch via an actor id (actor → projectName → project). */
    public void touchByActorId(String actorId) {
        if (actorId == null || actorId.isBlank()) return;
        actorRepository.findById(actorId).ifPresentOrElse(
                actor -> touchByProjectName(actor.getProjectName()),
                () -> logger.warn("ProjectTouchService: actor not found for id={}", actorId));
    }

    /** Touch via a story id (story → actorId → project). */
    public void touchByStoryId(String storyId) {
        if (storyId == null || storyId.isBlank()) return;
        storyRepository.findById(storyId).ifPresentOrElse(
                story -> touchByActorId(story.getActorId()),
                () -> logger.warn("ProjectTouchService: story not found for id={}", storyId));
    }

    /** Touch via a feature id (feature → storyId → actor → project). */
    public void touchByFeatureId(String featureId) {
        if (featureId == null || featureId.isBlank()) return;
        featureRepository.findById(featureId).ifPresentOrElse(
                feature -> touchByStoryId(feature.getStoryId()),
                () -> logger.warn("ProjectTouchService: feature not found for id={}", featureId));
    }

    /**
     * Touch via a task. Resolves the project through:
     * <ol>
     *   <li>{@code task.projectId} if present</li>
     *   <li>{@code task.idReference} when it has the form {@code feature/<id>}</li>
     * </ol>
     */
    public void touchByTask(Task task) {
        if (task == null) return;
        if (task.getProjectId() != null && !task.getProjectId().isBlank()) {
            touchByProjectId(task.getProjectId());
        } else if (task.getIdReference() != null && task.getIdReference().startsWith("feature/")) {
            String featureId = task.getIdReference().substring("feature/".length());
            touchByFeatureId(featureId);
        }
    }

    /** Touch directly when the project object is already at hand. */
    public void touch(Project project) {
        if (project == null) return;
        projectRepository.save(project);
    }
}
