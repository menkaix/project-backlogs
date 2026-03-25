package com.menkaix.backlogs.mcptools;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.Issue;
import com.menkaix.backlogs.models.values.IssueSeverity;
import com.menkaix.backlogs.models.values.IssueStatus;
import com.menkaix.backlogs.models.values.IssueType;
import com.menkaix.backlogs.services.IssueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class IssueServiceMCPTools {

    private final IssueService issueService;
    private final Gson gson;

    public IssueServiceMCPTools(IssueService issueService, Gson gson) {
        this.issueService = issueService;
        this.gson = gson;
    }

    public Issue createIssue(String issueJson) {
        if (issueJson == null || issueJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de l'issue ne peut pas être vide");
        }
        Issue issue = gson.fromJson(issueJson, Issue.class);
        if (issue.getTitle() == null || issue.getTitle().isBlank()) {
            throw new IllegalArgumentException("Le titre de l'issue est requis");
        }
        return issueService.create(issue);
    }

    public Optional<Issue> findIssueById(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        return issueService.findById(id);
    }

    public Optional<Issue> findIssueByTrackingReference(String ref) {
        if (ref == null || ref.isBlank()) throw new IllegalArgumentException("La référence ne peut pas être vide");
        return issueService.findByTrackingReference(ref);
    }

    public Issue updateIssue(String issueJson) {
        if (issueJson == null || issueJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de l'issue ne peut pas être vide");
        }
        Issue details = gson.fromJson(issueJson, Issue.class);
        if (details.getId() == null || details.getId().isBlank()) {
            throw new IllegalArgumentException("L'ID est requis pour la mise à jour");
        }
        Issue existing = issueService.findById(details.getId())
                .orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + details.getId()));
        if (details.getTitle() != null) existing.setTitle(details.getTitle());
        if (details.getDescription() != null) existing.setDescription(details.getDescription());
        if (details.getType() != null) existing.setType(details.getType());
        if (details.getSeverity() != null) existing.setSeverity(details.getSeverity());
        if (details.getPriority() != null) existing.setPriority(details.getPriority());
        if (details.getStatus() != null) existing.setStatus(details.getStatus());
        if (details.getReporter() != null) existing.setReporter(details.getReporter());
        if (details.getEnvironment() != null) existing.setEnvironment(details.getEnvironment());
        if (details.getPlatform() != null) existing.setPlatform(details.getPlatform());
        if (details.getComponent() != null) existing.setComponent(details.getComponent());
        if (details.getAffectedVersion() != null) existing.setAffectedVersion(details.getAffectedVersion());
        if (details.getFixedInVersion() != null) existing.setFixedInVersion(details.getFixedInVersion());
        if (details.getReproductionSteps() != null) existing.setReproductionSteps(details.getReproductionSteps());
        if (details.getExpectedBehavior() != null) existing.setExpectedBehavior(details.getExpectedBehavior());
        if (details.getActualBehavior() != null) existing.setActualBehavior(details.getActualBehavior());
        if (details.getWorkaround() != null) existing.setWorkaround(details.getWorkaround());
        if (details.getResolution() != null) existing.setResolution(details.getResolution());
        if (details.getExternalReference() != null) existing.setExternalReference(details.getExternalReference());
        if (details.getDuplicateOfId() != null) existing.setDuplicateOfId(details.getDuplicateOfId());
        if (details.getLabels() != null && !details.getLabels().isEmpty()) existing.setLabels(details.getLabels());
        if (details.getDueDate() != null) existing.setDueDate(details.getDueDate());
        if (details.getEstimatedManHours() != null) existing.setEstimatedManHours(details.getEstimatedManHours());
        return issueService.update(existing);
    }

    public String deleteIssue(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        issueService.findById(id).orElseThrow(() -> new NoSuchElementException("Issue non trouvée : " + id));
        issueService.delete(id);
        return "Issue supprimée avec succès : " + id;
    }

    public Issue updateIssueStatus(String issueId, String status) {
        if (issueId == null || issueId.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Le statut ne peut pas être vide");
        return issueService.updateStatus(issueId, status);
    }

    public Issue assignPerson(String issueId, String email) {
        if (issueId == null || issueId.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email ne peut pas être vide");
        return issueService.assignPerson(issueId, email);
    }

    public Issue unassignPerson(String issueId, String email) {
        if (issueId == null || issueId.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email ne peut pas être vide");
        return issueService.unassignPerson(issueId, email);
    }

    public Page<Issue> findAllIssues(Pageable pageable, String search, String filter) {
        return issueService.findAll(pageable, search, filter);
    }

    public List<Issue> findByProjectId(String projectId) {
        if (projectId == null || projectId.isBlank()) throw new IllegalArgumentException("L'ID projet ne peut pas être vide");
        return issueService.findByProjectId(projectId);
    }

    public List<Issue> findByProjectRef(String projectRef) {
        if (projectRef == null || projectRef.isBlank()) throw new IllegalArgumentException("La référence projet ne peut pas être vide");
        return issueService.findByProjectRef(projectRef);
    }

    public List<Issue> findByFeatureId(String featureId) {
        if (featureId == null || featureId.isBlank()) throw new IllegalArgumentException("L'ID feature ne peut pas être vide");
        return issueService.findByFeatureId(featureId);
    }

    public List<Issue> findByStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Le statut ne peut pas être vide");
        return issueService.findByStatus(status);
    }

    public List<Issue> findByType(String type) {
        if (type == null || type.isBlank()) throw new IllegalArgumentException("Le type ne peut pas être vide");
        return issueService.findByType(type);
    }

    public List<Issue> findBySeverity(String severity) {
        if (severity == null || severity.isBlank()) throw new IllegalArgumentException("La sévérité ne peut pas être vide");
        return issueService.findBySeverity(severity);
    }

    public List<Issue> findByReporter(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email ne peut pas être vide");
        return issueService.findByReporter(email);
    }

    public List<Issue> findByAssignee(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("L'email ne peut pas être vide");
        return issueService.findByAssigneeEmail(email);
    }

    public List<Issue> findByAffectedVersion(String version) {
        if (version == null || version.isBlank()) throw new IllegalArgumentException("La version ne peut pas être vide");
        return issueService.findByAffectedVersion(version);
    }

    public List<Issue> findByEnvironment(String environment) {
        if (environment == null || environment.isBlank()) throw new IllegalArgumentException("L'environnement ne peut pas être vide");
        return issueService.findByEnvironment(environment);
    }

    public List<Issue> findByPlatform(String platform) {
        if (platform == null || platform.isBlank()) throw new IllegalArgumentException("La plateforme ne peut pas être vide");
        return issueService.findByPlatform(platform);
    }

    public List<Issue> findByComponent(String component) {
        if (component == null || component.isBlank()) throw new IllegalArgumentException("Le composant ne peut pas être vide");
        return issueService.findByComponent(component);
    }

    public List<Issue> findOpenIssues() {
        return issueService.findOpenIssues();
    }

    public List<Issue> findCriticalIssues() {
        return issueService.findCriticalIssues();
    }

    public List<Issue> findOverdueIssues() {
        return issueService.findOverdueIssues();
    }

    public IssueStatus[] listStatuses() { return issueService.listStatuses(); }
    public IssueType[] listTypes() { return issueService.listTypes(); }
    public IssueSeverity[] listSeverities() { return issueService.listSeverities(); }
}
