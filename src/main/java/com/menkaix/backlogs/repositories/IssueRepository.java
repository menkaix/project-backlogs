package com.menkaix.backlogs.repositories;

import com.menkaix.backlogs.models.entities.Issue;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.webmvc.RepositoryRestController;

import java.util.List;
import java.util.Optional;

@RepositoryRestController(path = "issues", value = "issues")
public interface IssueRepository extends MongoRepository<Issue, String> {

    List<Issue> findByProjectId(String projectId);
    List<Issue> findByProjectIdOrderByLastUpdateDateDesc(String projectId);

    List<Issue> findByIdReference(String idReference);
    List<Issue> findByIdReferenceOrderByLastUpdateDateDesc(String idReference);

    List<Issue> findByStatus(String status);
    List<Issue> findByStatusOrderByLastUpdateDateDesc(String status);

    List<Issue> findByType(String type);
    List<Issue> findByTypeOrderByLastUpdateDateDesc(String type);

    List<Issue> findBySeverity(String severity);
    List<Issue> findBySeverityOrderByLastUpdateDateDesc(String severity);

    List<Issue> findByReporter(String reporter);
    List<Issue> findByReporterOrderByLastUpdateDateDesc(String reporter);

    List<Issue> findByAffectedVersion(String affectedVersion);

    List<Issue> findByEnvironment(String environment);

    List<Issue> findByPlatform(String platform);

    List<Issue> findByComponent(String component);

    List<Issue> findByAssigneesContainingOrderByLastUpdateDateDesc(String email);

    Optional<Issue> findByTrackingReference(String trackingReference);
}
