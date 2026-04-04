package com.menkaix.backlogs.controllers;


import com.menkaix.backlogs.models.entities.FeatureType;
import com.menkaix.backlogs.repositories.FeatureTypeRepository;
import com.menkaix.backlogs.repositories.TaskRepository;
import com.menkaix.backlogs.utilities.GcpUserInfoExtractor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

@RestController
public class GeneralController {

    @Autowired
    private FeatureTypeRepository typeRepository ;

    @Autowired
    private TaskRepository taskRepository ;

    @Autowired
    private MongoTemplate mongoTemplate ;



    @GetMapping("/me")
    public ResponseEntity<java.util.Map<String, String>> getCurrentUser(HttpServletRequest request) {
        var claims = GcpUserInfoExtractor.extractClaims(request);
        var result = new java.util.HashMap<String, String>();

        claims.ifPresentOrElse(c -> {
            if (c.has("email"))       result.put("email",      c.get("email").getAsString());
            if (c.has("given_name"))  result.put("firstName",  c.get("given_name").getAsString());
            if (c.has("family_name")) result.put("lastName",   c.get("family_name").getAsString());
            if (c.has("picture"))     result.put("picture",    c.get("picture").getAsString());
        }, () -> result.put("email", "anonymous"));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/featuretypes")
    public ResponseEntity<List<FeatureType>> getFeatureTypes(){

        List<FeatureType> ans = typeRepository.findAll() ;

        return  new ResponseEntity<>(ans, HttpStatus.OK) ;

    }

    @GetMapping("/normalize-tasks")
    public String normaliezTasks() {

        Query query = new Query(Criteria.where("reference").regex(Pattern.compile("^[^/]+/[^/]+/[^/]+$")));
        var tasks = mongoTemplate.find(query, com.menkaix.backlogs.models.entities.Task.class);

        if (tasks.isEmpty()) {
            return "OK - 0 tasks updated";
        }

        var bulkOps = mongoTemplate.bulkOps(org.springframework.data.mongodb.core.BulkOperations.BulkMode.UNORDERED,
                com.menkaix.backlogs.models.entities.Task.class);

        for (var task : tasks) {
            String[] parts = task.reference.split("/");
            if (parts.length == 3) {
                String newIdRef = parts[0] + "/" + parts[1];
                Query updateQuery = new Query(Criteria.where("_id").is(task.id));
                Update update = new Update().set("idReference", newIdRef);
                bulkOps.updateOne(updateQuery, update);
            }
        }

        var result = bulkOps.execute();
        return "OK - " + result.getModifiedCount() + " tasks updated";
    }

}
