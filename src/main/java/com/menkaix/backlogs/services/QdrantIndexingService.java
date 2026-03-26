package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.dto.IssueContextDTO;
import com.menkaix.backlogs.models.dto.TaskContextDTO;
import com.menkaix.backlogs.models.entities.Actor;
import com.menkaix.backlogs.models.entities.Feature;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.entities.Story;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Asynchronous indexing service: builds context, serializes to flat text,
 * computes embedding via Vertex AI and upserts into the appropriate Qdrant collection.
 */
@Service
@ConditionalOnProperty(name = "qdrant.enabled", havingValue = "true")
public class QdrantIndexingService {

    private static final Logger logger = LoggerFactory.getLogger(QdrantIndexingService.class);

    private final TaskContextService taskContextService;
    private final IssueContextService issueContextService;
    private final ContextTextSerializer serializer;
    private final ProjectRepository projectRepository;
    private final ActorRepository actorRepository;
    private final StoryRepository storyRepository;
    private final FeatureRepository featureRepository;

    private final VectorStore taskContextVectorStore;
    private final VectorStore issueContextVectorStore;
    private final VectorStore projectTreeVectorStore;

    public QdrantIndexingService(
            TaskContextService taskContextService,
            IssueContextService issueContextService,
            ContextTextSerializer serializer,
            ProjectRepository projectRepository,
            ActorRepository actorRepository,
            StoryRepository storyRepository,
            FeatureRepository featureRepository,
            @Qualifier("taskContextVectorStore") VectorStore taskContextVectorStore,
            @Qualifier("issueContextVectorStore") VectorStore issueContextVectorStore,
            @Qualifier("projectTreeVectorStore") VectorStore projectTreeVectorStore) {
        this.taskContextService = taskContextService;
        this.issueContextService = issueContextService;
        this.serializer = serializer;
        this.projectRepository = projectRepository;
        this.actorRepository = actorRepository;
        this.storyRepository = storyRepository;
        this.featureRepository = featureRepository;
        this.taskContextVectorStore = taskContextVectorStore;
        this.issueContextVectorStore = issueContextVectorStore;
        this.projectTreeVectorStore = projectTreeVectorStore;
    }

    // ── Task context ──────────────────────────────────────────────────────────

    @Async
    public void indexTaskContext(String taskId) {
        try {
            TaskContextDTO ctx = taskContextService.buildContext(taskId);
            String text = serializer.toText(ctx);
            if (text.isBlank()) return;

            Map<String, Object> metadata = buildTaskMetadata(ctx, taskId);
            Document doc = new Document(toUUID(taskId), text, metadata);
            taskContextVectorStore.add(List.of(doc));

            logger.debug("Indexed task context: {}", taskId);
        } catch (NoSuchElementException e) {
            logger.warn("Cannot index task context — task not found: {}", taskId);
        } catch (Exception e) {
            logger.error("Failed to index task context for taskId={}: {}", taskId, e.getMessage());
        }
    }

    // ── Issue context ─────────────────────────────────────────────────────────

    @Async
    public void indexIssueContext(String issueId) {
        try {
            IssueContextDTO ctx = issueContextService.buildContext(issueId);
            String text = serializer.toText(ctx);
            if (text.isBlank()) return;

            Map<String, Object> metadata = buildIssueMetadata(ctx, issueId);
            Document doc = new Document(toUUID(issueId), text, metadata);
            issueContextVectorStore.add(List.of(doc));

            logger.debug("Indexed issue context: {}", issueId);
        } catch (NoSuchElementException e) {
            logger.warn("Cannot index issue context — issue not found: {}", issueId);
        } catch (Exception e) {
            logger.error("Failed to index issue context for issueId={}: {}", issueId, e.getMessage());
        }
    }

    // ── Project tree ──────────────────────────────────────────────────────────

    @Async
    public void indexProjectTree(String projectId) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new NoSuchElementException("Project not found: " + projectId));

            List<Actor> actors = actorRepository.findByProjectName(project.getName());
            List<String> actorIds = actors.stream().map(Actor::getId).toList();
            List<Story> stories = storyRepository.findByActorIdIn(actorIds);
            List<String> storyIds = stories.stream().map(Story::getId).toList();
            List<Feature> features = featureRepository.findByStoryIdIn(storyIds);

            String text = serializer.toText(project, features, stories);
            if (text.isBlank()) return;

            Map<String, Object> metadata = Map.of(
                    "projectId", projectId,
                    "projectName", project.getName() != null ? project.getName() : "",
                    "type", "project-tree"
            );
            Document doc = new Document(toUUID(projectId), text, metadata);
            projectTreeVectorStore.add(List.of(doc));

            logger.debug("Indexed project tree: {}", projectId);
        } catch (NoSuchElementException e) {
            logger.warn("Cannot index project tree — project not found: {}", projectId);
        } catch (Exception e) {
            logger.error("Failed to index project tree for projectId={}: {}", projectId, e.getMessage());
        }
    }

    // ── Metadata builders ─────────────────────────────────────────────────────

    private Map<String, Object> buildTaskMetadata(TaskContextDTO ctx, String taskId) {
        String projectId = ctx.getProject() != null ? ctx.getProject().getId() : "";
        String projectName = ctx.getProject() != null && ctx.getProject().getName() != null
                ? ctx.getProject().getName() : "";
        String status = ctx.getTask() != null && ctx.getTask().getStatus() != null
                ? ctx.getTask().getStatus() : "";
        String featureId = ctx.getFeature() != null ? ctx.getFeature().getId() : "";

        return Map.of(
                "taskId", taskId,
                "projectId", projectId,
                "projectName", projectName,
                "status", status,
                "featureId", featureId,
                "type", "task-context"
        );
    }

    /** Converts a MongoDB ObjectId to a deterministic UUID (name-based, namespace OID). */
    private String toUUID(String mongoId) {
        return UUID.nameUUIDFromBytes(mongoId.getBytes()).toString();
    }

    private Map<String, Object> buildIssueMetadata(IssueContextDTO ctx, String issueId) {
        String projectId = ctx.getProject() != null ? ctx.getProject().getId() : "";
        String projectName = ctx.getProject() != null && ctx.getProject().getName() != null
                ? ctx.getProject().getName() : "";
        String status = ctx.getIssue() != null && ctx.getIssue().getStatus() != null
                ? ctx.getIssue().getStatus() : "";
        String severity = ctx.getIssue() != null && ctx.getIssue().getSeverity() != null
                ? ctx.getIssue().getSeverity() : "";
        String issueType = ctx.getIssue() != null && ctx.getIssue().getType() != null
                ? ctx.getIssue().getType() : "";
        String featureId = ctx.getFeature() != null ? ctx.getFeature().getId() : "";

        return Map.of(
                "issueId", issueId,
                "projectId", projectId,
                "projectName", projectName,
                "status", status,
                "severity", severity,
                "issueType", issueType,
                "featureId", featureId,
                "type", "issue-context"
        );
    }
}
