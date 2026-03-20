package com.menkaix.backlogs.models.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "task")
public class Task extends AbstractEntity {

	@Id
	public String id;

	@Indexed
	public String projectId;
	public String reference;
	public String title;
	public String description;

	public Date plannedStart;
	public Date dueDate;
	public Date doneDate;

	@Indexed
	public String idReference;

	// Champs enrichis depuis bug-tracking-janitor
	public String status;
	public List<String> assignees = new ArrayList<>();
	public String estimate;
	public Double estimatedManHours;
	public String trackingReference;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getDoneDate() {
		return doneDate;
	}

	public void setDoneDate(Date doneDate) {
		this.doneDate = doneDate;
	}

	public String getIdReference() {
		return idReference;
	}

	public void setIdReference(String idReference) {
		this.idReference = idReference;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getAssignees() {
		return assignees;
	}

	public void setAssignees(List<String> assignees) {
		this.assignees = assignees != null ? assignees : new ArrayList<>();
	}

	public String getEstimate() {
		if (estimatedManHours != null) {
			long totalMinutes = Math.round(estimatedManHours * 60);
			long days = totalMinutes / 480;
			long hours = (totalMinutes % 480) / 60;
			long minutes = totalMinutes % 60;
			StringBuilder sb = new StringBuilder();
			if (days > 0) sb.append(days).append("j ");
			if (hours > 0) sb.append(hours).append("h ");
			if (minutes > 0) sb.append(minutes).append("min");
			return sb.toString().trim();
		}
		return estimate;
	}

	public void setEstimate(String estimate) {
		this.estimate = estimate;
		if (estimate == null || estimate.isBlank()) return;
		double total = 0;
		java.util.regex.Matcher m;
		m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*j").matcher(estimate);
		if (m.find()) total += Double.parseDouble(m.group(1)) * 8;
		m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*h").matcher(estimate);
		if (m.find()) total += Double.parseDouble(m.group(1));
		m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*min").matcher(estimate);
		if (m.find()) total += Double.parseDouble(m.group(1)) / 60;
		if (total > 0) this.estimatedManHours = total;
	}

	public Double getEstimatedManHours() {
		return estimatedManHours;
	}

	public void setEstimatedManHours(Double estimatedManHours) {
		this.estimatedManHours = estimatedManHours;
	}

	public String getTrackingReference() {
		return trackingReference;
	}

	public void setTrackingReference(String trackingReference) {
		this.trackingReference = trackingReference;
	}

	public Date getPlannedStart() {
		return plannedStart;
	}

	public void setPlannedStart(Date plannedStart) {
		this.plannedStart = plannedStart;
	}


}
