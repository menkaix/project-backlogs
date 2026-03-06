package com.menkaix.backlogs.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.repositories.ProjectRepository;

@Service
public class ProjectManagementService {

    private final ProjectRepository repository;
    private final MongoTemplate mongoTemplate;

    public ProjectManagementService(ProjectRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public Project create(Project project) {
        return repository.save(project);
    }

    public Optional<Project> findById(String id) {
        return repository.findById(id);
    }

    public Project update(Project project) {
        return repository.save(project);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public Optional<Project> findByProjectCode(String code) {
        return repository.findByCode(code).stream().findFirst();
    }

    public Optional<Project> findByProjectName(String name) {
        return repository.findByName(name).stream().findFirst();
    }

    public Page<Project> findAll(Pageable pageable, String search, String filter) {
        if (pageable == null) {
            pageable = Pageable.unpaged();
        }
        Query query = buildQuery(search, filter);
        Query finalQuery = query;
        Pageable finalPageable = pageable;
        List<Project> projects = mongoTemplate.find(query.with(pageable), Project.class);
        return PageableExecutionUtils.getPage(projects, finalPageable,
                () -> mongoTemplate.count(Query.of(finalQuery).limit(-1).skip(-1), Project.class));
    }

    private Query buildQuery(String search, String filter) {
        List<Criteria> criteria = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            criteria.add(new Criteria().orOperator(
                    Criteria.where("name").regex(search, "i"),
                    Criteria.where("description").regex(search, "i"),
                    Criteria.where("code").regex(search, "i")
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
