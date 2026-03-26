package com.menkaix.backlogs.models.dto;

import com.menkaix.backlogs.models.transients.ProjectMember;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contexte complet d'une issue, destiné à un LLM ou à un client REST logique.
 *
 * Cas d'usage typiques :
 *   - Analyse d'impact et priorisation
 *   - Suggestion d'assignation selon les compétences de l'équipe
 *   - Génération d'une description de correctif ou de critères de résolution
 *   - Mise en relation avec les autres issues de la même feature
 *
 * La lignée (feature → story → actor) peut être nulle si l'issue
 * est rattachée directement à un projet sans passer par ces entités.
 */
public class IssueContextDTO {

    private IssueSnapshot issue;
    private TaskContextDTO.ProjectSnapshot project;
    private TaskContextDTO.FeatureSnapshot feature;        // null si issue sans feature
    private TaskContextDTO.StorySnapshot story;            // null si issue sans story
    private TaskContextDTO.ActorSnapshot actor;            // null si issue sans actor
    private List<IssueSnapshot> siblingIssues = new ArrayList<>();  // autres issues sur la même feature
    private List<ProjectMember> team = new ArrayList<>();           // équipe projet avec skillsets

    // ─── Getters / Setters ───────────────────────────────────────────────────────

    public IssueSnapshot getIssue() { return issue; }
    public void setIssue(IssueSnapshot issue) { this.issue = issue; }

    public TaskContextDTO.ProjectSnapshot getProject() { return project; }
    public void setProject(TaskContextDTO.ProjectSnapshot project) { this.project = project; }

    public TaskContextDTO.FeatureSnapshot getFeature() { return feature; }
    public void setFeature(TaskContextDTO.FeatureSnapshot feature) { this.feature = feature; }

    public TaskContextDTO.StorySnapshot getStory() { return story; }
    public void setStory(TaskContextDTO.StorySnapshot story) { this.story = story; }

    public TaskContextDTO.ActorSnapshot getActor() { return actor; }
    public void setActor(TaskContextDTO.ActorSnapshot actor) { this.actor = actor; }

    public List<IssueSnapshot> getSiblingIssues() { return siblingIssues; }
    public void setSiblingIssues(List<IssueSnapshot> siblingIssues) { this.siblingIssues = siblingIssues; }

    public List<ProjectMember> getTeam() { return team; }
    public void setTeam(List<ProjectMember> team) { this.team = team; }

    // ═══════════════════════════════════════════════════════════════════════════
    // Snapshot issue (champs Task + champs Issue spécifiques)
    // ═══════════════════════════════════════════════════════════════════════════

    public static class IssueSnapshot {
        // ── Héritage Task ──────────────────────────────────────────────────────
        private String id;
        private String title;
        private String description;
        private String status;
        private String estimate;
        private Double estimatedManHours;
        private List<String> assignees = new ArrayList<>();
        private String reference;
        private String idReference;
        private String trackingReference;
        private Date plannedStart;
        private Date dueDate;
        private Date startDate;
        private Date doneDate;

        // ── Champs spécifiques Issue ───────────────────────────────────────────
        private String type;
        private String severity;
        private String priority;
        private String reporter;
        private String environment;
        private String platform;
        private String component;
        private String affectedVersion;
        private String fixedInVersion;
        private String reproductionSteps;
        private String expectedBehavior;
        private String actualBehavior;
        private String workaround;
        private String externalReference;
        private String duplicateOfId;
        private String resolution;
        private Date closedDate;
        private List<String> labels = new ArrayList<>();

        // ── Getters / Setters ──────────────────────────────────────────────────

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getEstimate() { return estimate; }
        public void setEstimate(String estimate) { this.estimate = estimate; }
        public Double getEstimatedManHours() { return estimatedManHours; }
        public void setEstimatedManHours(Double estimatedManHours) { this.estimatedManHours = estimatedManHours; }
        public List<String> getAssignees() { return assignees; }
        public void setAssignees(List<String> assignees) { this.assignees = assignees; }
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }
        public String getIdReference() { return idReference; }
        public void setIdReference(String idReference) { this.idReference = idReference; }
        public String getTrackingReference() { return trackingReference; }
        public void setTrackingReference(String trackingReference) { this.trackingReference = trackingReference; }
        public Date getPlannedStart() { return plannedStart; }
        public void setPlannedStart(Date plannedStart) { this.plannedStart = plannedStart; }
        public Date getDueDate() { return dueDate; }
        public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
        public Date getStartDate() { return startDate; }
        public void setStartDate(Date startDate) { this.startDate = startDate; }
        public Date getDoneDate() { return doneDate; }
        public void setDoneDate(Date doneDate) { this.doneDate = doneDate; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getReporter() { return reporter; }
        public void setReporter(String reporter) { this.reporter = reporter; }
        public String getEnvironment() { return environment; }
        public void setEnvironment(String environment) { this.environment = environment; }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public String getComponent() { return component; }
        public void setComponent(String component) { this.component = component; }
        public String getAffectedVersion() { return affectedVersion; }
        public void setAffectedVersion(String affectedVersion) { this.affectedVersion = affectedVersion; }
        public String getFixedInVersion() { return fixedInVersion; }
        public void setFixedInVersion(String fixedInVersion) { this.fixedInVersion = fixedInVersion; }
        public String getReproductionSteps() { return reproductionSteps; }
        public void setReproductionSteps(String reproductionSteps) { this.reproductionSteps = reproductionSteps; }
        public String getExpectedBehavior() { return expectedBehavior; }
        public void setExpectedBehavior(String expectedBehavior) { this.expectedBehavior = expectedBehavior; }
        public String getActualBehavior() { return actualBehavior; }
        public void setActualBehavior(String actualBehavior) { this.actualBehavior = actualBehavior; }
        public String getWorkaround() { return workaround; }
        public void setWorkaround(String workaround) { this.workaround = workaround; }
        public String getExternalReference() { return externalReference; }
        public void setExternalReference(String externalReference) { this.externalReference = externalReference; }
        public String getDuplicateOfId() { return duplicateOfId; }
        public void setDuplicateOfId(String duplicateOfId) { this.duplicateOfId = duplicateOfId; }
        public String getResolution() { return resolution; }
        public void setResolution(String resolution) { this.resolution = resolution; }
        public Date getClosedDate() { return closedDate; }
        public void setClosedDate(Date closedDate) { this.closedDate = closedDate; }
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
    }
}
