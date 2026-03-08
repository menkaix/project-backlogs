package com.menkaix.backlogs.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.repositories.PeopleRepository;

@Service
public class PersonService {

    private final PeopleRepository repository;
    private final MongoTemplate mongoTemplate;

    public PersonService(PeopleRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public People create(People person) {
        if (person.getEmail() == null || person.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return repository.save(person);
    }

    public Optional<People> findById(String id) {
        return repository.findById(id);
    }

    public Optional<People> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public People update(People person) {
        return repository.save(person);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public List<People> findAll() {
        return repository.findAll();
    }

    public Page<People> findAll(Pageable pageable, String search) {
        Query query = new Query();
        if (search != null && !search.isBlank()) {
            List<Criteria> criteria = new ArrayList<>();
            criteria.add(Criteria.where("firstName").regex(Pattern.quote(search), "i"));
            criteria.add(Criteria.where("lastName").regex(Pattern.quote(search), "i"));
            criteria.add(Criteria.where("email").regex(Pattern.quote(search), "i"));
            query.addCriteria(new Criteria().orOperator(criteria.toArray(new Criteria[0])));
        }
        List<People> persons = mongoTemplate.find(query.with(pageable), People.class);
        Query finalQuery = query;
        return PageableExecutionUtils.getPage(persons, pageable,
                () -> mongoTemplate.count(Query.of(finalQuery).limit(-1).skip(-1), People.class));
    }
}
