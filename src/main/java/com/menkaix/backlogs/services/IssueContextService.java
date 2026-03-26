package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.dto.IssueContextDTO;
import com.menkaix.backlogs.models.dto.IssueContextDTO.IssueSnapshot;
import com.menkaix.backlogs.models.dto.TaskContextDTO;
import com.menkaix.backlogs.models.dto.TaskContextDTO.*;
import com.menkaix.backlogs.models.entities.*;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.repositories.*;
import com.menkaix.backlogs.services.applicatif.DataAccessService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Assemble le contexte complet d'une issue pour usage LLM ou client REST logique.
 *
 * Résolution de la lignée :
 *   Issue.idReference = "feature/{featureId}"
 *     → Feature.storyId → Story.actorId → Actor.projectName
 *     → Project (via DataAccessService)
 *
 * Si l'une des étapes est absente (issue sans feature, sans story, etc.),
 * la portion correspondante du contexte est simplement nulle.
 */
@Service
public class IssueContextService {

    private final IssueRepository issueRepository;
    private final FeatureRepository featureRepository;
    private final StoryRepository storyRepository;
    private final ActorRepository actorRepository;
    private final ProjectRepository projectRepository;
    private final DataAccessService dataAccessService;

    public IssueContextService(
            IssueRepository issueRepository,
            FeatureRepository featureRepository,
            StoryRepository storyRepository,
            ActorRepository actorRepository,
            ProjectRepository projectRepository,
            DataAccessService dataAccessService) {
        this.issueRepository = issueRepository;
        this.featureRepository = featureRepository;
        this.storyRepository = storyRepository;
        this.actorRepository = actorRepository;
        this.projectRepository = projectRepository;
        this.dataAccessService = dataAccessService;
    }

    public IssueContextDTO buildContext(String issueId) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue introuvable : " + issueId));

        IssueContextDTO ctx = new IssueContextDTO();
        ctx.setIssue(toIssueSnapshot(issue));

        // ── Résolution de la lignée ──────────────────────────────────────────────
        Feature feature = resolveFeature(issue);
        Story story = null;
        Actor actor = null;
        Project project = null;

        if (feature != null) {
            ctx.setFeature(toFeatureSnapshot(feature));
            ctx.setSiblingIssues(resolveSiblingIssues(feature.getId(), issueId));

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

        // Fallback : résolution du projet via issue.projectId si la lignée n'a pas suffi
        if (project == null && issue.getProjectId() != null) {
            project = projectRepository.findById(issue.getProjectId()).orElse(null);
        }

        if (project != null) {
            ctx.setProject(toProjectSnapshot(project));
            ctx.setTeam(project.getTeam());
        }

        return ctx;
    }

    // ─── Résolution de la lignée ─────────────────────────────────────────────────

    private Feature resolveFeature(Issue issue) {
        String ref = issue.getIdReference();
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

    private List<IssueSnapshot> resolveSiblingIssues(String featureId, String excludeIssueId) {
        return issueRepository.findByIdReference("feature/" + featureId).stream()
                .filter(i -> !i.getId().equals(excludeIssueId))
                .map(this::toIssueSnapshot)
                .toList();
    }

    // ─── Mapping vers snapshots ──────────────────────────────────────────────────

    private IssueSnapshot toIssueSnapshot(Issue i) {
        IssueSnapshot s = new IssueSnapshot();
        s.setId(i.getId());
        s.setTitle(i.getTitle());
        s.setDescription(i.getDescription());
        s.setStatus(i.getStatus());
        s.setEstimate(i.getEstimate());
        s.setEstimatedManHours(i.getEstimatedManHours());
        s.setAssignees(i.getAssignees());
        s.setReference(i.getReference());
        s.setIdReference(i.getIdReference());
        s.setTrackingReference(i.getTrackingReference());
        s.setPlannedStart(i.getPlannedStart());
        s.setDueDate(i.getDueDate());
        s.setStartDate(i.getStartDate());
        s.setDoneDate(i.getDoneDate());
        s.setType(i.getType() != null ? i.getType().name() : null);
        s.setSeverity(i.getSeverity() != null ? i.getSeverity().name() : null);
        s.setPriority(i.getPriority());
        s.setReporter(i.getReporter());
        s.setEnvironment(i.getEnvironment());
        s.setPlatform(i.getPlatform());
        s.setComponent(i.getComponent());
        s.setAffectedVersion(i.getAffectedVersion());
        s.setFixedInVersion(i.getFixedInVersion());
        s.setReproductionSteps(i.getReproductionSteps());
        s.setExpectedBehavior(i.getExpectedBehavior());
        s.setActualBehavior(i.getActualBehavior());
        s.setWorkaround(i.getWorkaround());
        s.setExternalReference(i.getExternalReference());
        s.setDuplicateOfId(i.getDuplicateOfId());
        s.setResolution(i.getResolution());
        s.setClosedDate(i.getClosedDate());
        s.setLabels(i.getLabels());
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
