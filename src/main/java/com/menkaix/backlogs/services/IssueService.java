package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.entities.Issue;
import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.values.IssueSeverity;
import com.menkaix.backlogs.models.values.IssueStatus;
import com.menkaix.backlogs.models.values.IssueType;
import com.menkaix.backlogs.repositories.ActorRepository;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.IssueRepository;
import com.menkaix.backlogs.repositories.PeopleRepository;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class IssueService {

    private static final Sort LAST_UPDATE_DESC = Sort.by(Sort.Direction.DESC, "lastUpdateDate");

    private final IssueRepository repository;
    private final MongoTemplate mongoTemplate;
    private final ProjectTouchService projectTouchService;
    private final FeatureRepository featureRepository;
    private final StoryRepository storyRepository;
    private final ActorRepository actorRepository;
    private final ProjectRepository projectRepository;
    private final PeopleRepository peopleRepository;

    @Autowired
    public IssueService(IssueRepository repository, MongoTemplate mongoTemplate,
            ProjectTouchService projectTouchService, FeatureRepository featureRepository,
            StoryRepository storyRepository, ActorRepository actorRepository,
            ProjectRepository projectRepository, PeopleRepository peopleRepository) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.projectTouchService = projectTouchService;
        this.featureRepository = featureRepository;
        this.storyRepository = storyRepository;
        this.actorRepository = actorRepository;
        this.projectRepository = projectRepository;
        this.peopleRepository = peopleRepository;
    }

    // ── CRUD de base ──────────────────────────────────────────────────────────

    public Issue create(Issue issue) {
        return repository.save(issue);
    }

    public Optional<Issue> findById(String id) {
        return repository.findById(id);
    }

    public Optional<Issue> findByTrackingReference(String ref) {
        return repository.findByTrackingReference(ref);
    }

    public Issue update(Issue issue) {
        return repository.save(issue);
    }

    public void delete(String id) {
        repository.findById(id).ifPresent(repository::delete);
    }

    // ── Gestion du statut ─────────────────────────────────────────────────────

    /**
     * Met à jour le statut d'une issue en gérant les dates associées :
     * - startDate  : renseignée automatiquement au premier passage en IN_PROGRESS
     * - closedDate : renseignée au passage en CLOSED, WONT_FIX ou DUPLICATE ;
     *                effacée si l'issue est rouverte
     */
    public Issue updateStatus(String issueId, String rawStatus) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + issueId));

        IssueStatus newStatus = IssueStatus.normalize(rawStatus);
        if (newStatus == null) {
            throw new IllegalArgumentException("Statut inconnu : " + rawStatus +
                    ". Valeurs valides : OPEN, TRIAGED, IN_PROGRESS, IN_REVIEW, RESOLVED, CLOSED, WONT_FIX, DUPLICATE, NEED_MORE_INFO, REOPENED");
        }

        issue.setStatus(newStatus.name());

        Date now = new Date();
        if (newStatus == IssueStatus.IN_PROGRESS && issue.getStartDate() == null) {
            issue.setStartDate(now);
        }
        if (newStatus.isTerminal() && issue.getClosedDate() == null) {
            issue.setClosedDate(now);
        }
        if (!newStatus.isTerminal()) {
            issue.setClosedDate(null);
        }

        return repository.save(issue);
    }

    // ── Assignation ───────────────────────────────────────────────────────────

    public Issue assignPerson(String issueId, String email) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + issueId));
        if (!issue.getAssignees().contains(email)) {
            issue.getAssignees().add(email);
            Issue saved = repository.save(issue);
            projectTouchService.touchByIssue(saved);
            peopleRepository.findByEmail(email).ifPresent(person -> {
                person.setActive(true);
                peopleRepository.save(person);
                resolveProject(saved).ifPresent(project -> addToTeam(project, person));
            });
            return saved;
        }
        return issue;
    }

    private Optional<Project> resolveProject(Issue issue) {
        if (issue.getProjectId() != null && !issue.getProjectId().isBlank()) {
            return projectRepository.findById(issue.getProjectId());
        }
        if (issue.getIdReference() != null && issue.getIdReference().startsWith("feature/")) {
            String featureId = issue.getIdReference().substring("feature/".length());
            return featureRepository.findById(featureId)
                    .flatMap(f -> storyRepository.findById(f.getStoryId()))
                    .flatMap(s -> actorRepository.findById(s.getActorId()))
                    .flatMap(a -> projectRepository.findByName(a.getProjectName()).stream().findFirst());
        }
        return Optional.empty();
    }

    private void addToTeam(Project project, People person) {
        boolean alreadyMember = project.getTeam().stream()
                .anyMatch(m -> person.getEmail().equals(m.getEmail()));
        if (!alreadyMember) {
            ProjectMember member = new ProjectMember();
            member.setPersonId(person.getId());
            member.setFirstName(person.getFirstName());
            member.setLastName(person.getLastName());
            member.setEmail(person.getEmail());
            member.setSkills(person.getSkills());
            project.getTeam().add(member);
            projectRepository.save(project);
        }
    }

    public Issue unassignPerson(String issueId, String email) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + issueId));
        issue.getAssignees().remove(email);
        return repository.save(issue);
    }

    // ── Affectation à un projet ou une feature ────────────────────────────────

    public Issue assignToProject(String issueId, String projectId) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + issueId));
        projectRepository.findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Projet non trouvé : " + projectId));
        issue.setProjectId(projectId);
        return repository.save(issue);
    }

    public Issue assignToFeature(String issueId, String featureId) {
        Issue issue = repository.findById(issueId)
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + issueId));
        featureRepository.findById(featureId)
                .orElseThrow(() -> new NoSuchElementException("Feature non trouvée : " + featureId));
        issue.setIdReference("feature/" + featureId);
        return repository.save(issue);
    }

    // ── Recherche par projet ──────────────────────────────────────────────────

    public List<Issue> findByProjectId(String projectId) {
        return repository.findByProjectIdOrderByLastUpdateDateDesc(projectId);
    }

    /**
     * Retourne toutes les issues d'un projet identifié par son nom, code ou ID.
     * Inclut les issues directes (projectId) et celles liées via feature → story → actor → project.
     */
    public List<Issue> findByProjectRef(String projectRef) {
        List<Issue> result = new ArrayList<>();
        projectRepository.findByName(projectRef).stream().findFirst()
                .or(() -> projectRepository.findByCode(projectRef).stream().findFirst())
                .or(() -> projectRepository.findById(projectRef))
                .ifPresent(project -> {
                    result.addAll(repository.findByProjectId(project.getId()));
                    actorRepository.findByProjectName(project.getName()).forEach(actor ->
                        storyRepository.findByActorId(actor.getId()).forEach(story ->
                            featureRepository.findByStoryId(story.getId()).forEach(feature ->
                                result.addAll(repository.findByIdReference("feature/" + feature.getId()))
                            )
                        )
                    );
                });
        result.sort(Comparator.comparing(Issue::getLastUpdateDate,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return result;
    }

    public List<Issue> findByFeatureId(String featureId) {
        return repository.findByIdReferenceOrderByLastUpdateDateDesc("feature/" + featureId);
    }

    // ── Recherche par critères ────────────────────────────────────────────────

    public List<Issue> findByStatus(String status) {
        IssueStatus normalized = IssueStatus.normalize(status);
        String key = normalized != null ? normalized.name() : status;
        return repository.findByStatusOrderByLastUpdateDateDesc(key);
    }

    public List<Issue> findByType(String type) {
        try {
            IssueType t = IssueType.valueOf(type.trim().toUpperCase());
            return repository.findByTypeOrderByLastUpdateDateDesc(t.name());
        } catch (IllegalArgumentException e) {
            return repository.findByTypeOrderByLastUpdateDateDesc(type);
        }
    }

    public List<Issue> findBySeverity(String severity) {
        try {
            IssueSeverity s = IssueSeverity.valueOf(severity.trim().toUpperCase());
            return repository.findBySeverityOrderByLastUpdateDateDesc(s.name());
        } catch (IllegalArgumentException e) {
            return repository.findBySeverityOrderByLastUpdateDateDesc(severity);
        }
    }

    public List<Issue> findByReporter(String reporter) {
        return repository.findByReporterOrderByLastUpdateDateDesc(reporter);
    }

    public List<Issue> findByAssigneeEmail(String email) {
        return repository.findByAssigneesContainingOrderByLastUpdateDateDesc(email);
    }

    public List<Issue> findByAffectedVersion(String version) {
        return repository.findByAffectedVersion(version);
    }

    public List<Issue> findByEnvironment(String environment) {
        return repository.findByEnvironment(environment);
    }

    public List<Issue> findByPlatform(String platform) {
        return repository.findByPlatform(platform);
    }

    public List<Issue> findByComponent(String component) {
        return repository.findByComponent(component);
    }

    public List<Issue> findOpenIssues() {
        Query query = new Query(
                new Criteria().orOperator(
                    Criteria.where("status").is(IssueStatus.OPEN.name()),
                    Criteria.where("status").is(IssueStatus.TRIAGED.name()),
                    Criteria.where("status").is(IssueStatus.REOPENED.name())
                )
        ).with(LAST_UPDATE_DESC);
        return mongoTemplate.find(query, Issue.class);
    }

    public List<Issue> findCriticalIssues() {
        Query query = new Query(
                Criteria.where("severity").is(IssueSeverity.CRITICAL.name())
                        .and("status").nin(IssueStatus.CLOSED.name(), IssueStatus.WONT_FIX.name(), IssueStatus.DUPLICATE.name())
        ).with(LAST_UPDATE_DESC);
        return mongoTemplate.find(query, Issue.class);
    }

    public List<Issue> findOverdueIssues() {
        Query query = new Query(
                Criteria.where("dueDate").lt(new Date()).and("closedDate").isNull()
        ).with(LAST_UPDATE_DESC);
        return mongoTemplate.find(query, Issue.class);
    }

    // ── Pagination / recherche full-text ──────────────────────────────────────

    public Page<Issue> findAll(Pageable pageable, String search, String filter) {
        Query baseQuery = buildQuery(search, filter);
        Query dataQuery = Query.of(baseQuery).with(LAST_UPDATE_DESC);
        if (pageable != null && pageable.isPaged()) {
            dataQuery.skip(pageable.getOffset()).limit(pageable.getPageSize());
        }
        List<Issue> issues = mongoTemplate.find(dataQuery, Issue.class);
        return PageableExecutionUtils.getPage(issues,
                pageable != null ? pageable : Pageable.unpaged(),
                () -> mongoTemplate.count(buildQuery(search, filter), Issue.class));
    }

    // ── Méta ──────────────────────────────────────────────────────────────────

    public IssueStatus[] listStatuses() {
        return IssueStatus.values();
    }

    public IssueType[] listTypes() {
        return IssueType.values();
    }

    public IssueSeverity[] listSeverities() {
        return IssueSeverity.values();
    }

    // ── Interne ───────────────────────────────────────────────────────────────

    private Query buildQuery(String search, String filter) {
        List<Criteria> criteria = new ArrayList<>();
        if (search != null && !search.isBlank()) {
            criteria.add(new Criteria().orOperator(
                    Criteria.where("title").regex(search, "i"),
                    Criteria.where("description").regex(search, "i"),
                    Criteria.where("component").regex(search, "i")
            ));
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
