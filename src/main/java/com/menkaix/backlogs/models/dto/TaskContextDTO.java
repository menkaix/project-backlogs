package com.menkaix.backlogs.models.dto;

import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.values.ProjectPhase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contexte complet d'une tâche, destiné à un LLM ou à un client REST logique.
 *
 * Cas d'usage typiques :
 *   - Estimation en heures.homme
 *   - Suggestion d'affectation selon les compétences de l'équipe
 *   - Proposition de planification (dates, dépendances)
 *   - Génération de description ou de critères d'acceptation
 *
 * La lignée (feature → story → actor) peut être nulle si la tâche
 * est rattachée directement à un projet sans passer par ces entités.
 */
public class TaskContextDTO {

    private TaskSnapshot task;
    private ProjectSnapshot project;
    private FeatureSnapshot feature;        // null si tâche sans feature
    private StorySnapshot story;            // null si tâche sans story
    private ActorSnapshot actor;            // null si tâche sans actor
    private List<TaskSnapshot> siblingTasks = new ArrayList<>();  // autres tâches sur la même feature
    private List<ProjectMember> team = new ArrayList<>();         // équipe projet avec skillsets

    // ─── Getters / Setters ───────────────────────────────────────────────────────

    public TaskSnapshot getTask() { return task; }
    public void setTask(TaskSnapshot task) { this.task = task; }

    public ProjectSnapshot getProject() { return project; }
    public void setProject(ProjectSnapshot project) { this.project = project; }

    public FeatureSnapshot getFeature() { return feature; }
    public void setFeature(FeatureSnapshot feature) { this.feature = feature; }

    public StorySnapshot getStory() { return story; }
    public void setStory(StorySnapshot story) { this.story = story; }

    public ActorSnapshot getActor() { return actor; }
    public void setActor(ActorSnapshot actor) { this.actor = actor; }

    public List<TaskSnapshot> getSiblingTasks() { return siblingTasks; }
    public void setSiblingTasks(List<TaskSnapshot> siblingTasks) { this.siblingTasks = siblingTasks; }

    public List<ProjectMember> getTeam() { return team; }
    public void setTeam(List<ProjectMember> team) { this.team = team; }

    // ═══════════════════════════════════════════════════════════════════════════
    // Classes snapshot imbriquées
    // ═══════════════════════════════════════════════════════════════════════════

    public static class TaskSnapshot {
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
    }

    public static class ProjectSnapshot {
        private String id;
        private String name;
        private String code;
        private String description;
        private String clientName;
        private ProjectPhase phase;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getClientName() { return clientName; }
        public void setClientName(String clientName) { this.clientName = clientName; }
        public ProjectPhase getPhase() { return phase; }
        public void setPhase(ProjectPhase phase) { this.phase = phase; }
    }

    public static class FeatureSnapshot {
        private String id;
        private String name;
        private String description;
        private String type;
        private String parentID;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getParentID() { return parentID; }
        public void setParentID(String parentID) { this.parentID = parentID; }
    }

    public static class StorySnapshot {
        private String id;
        private String action;
        private String objective;
        private String scenario;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getObjective() { return objective; }
        public void setObjective(String objective) { this.objective = objective; }
        public String getScenario() { return scenario; }
        public void setScenario(String scenario) { this.scenario = scenario; }
    }

    public static class ActorSnapshot {
        private String id;
        private String name;
        private String description;
        private String type;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
