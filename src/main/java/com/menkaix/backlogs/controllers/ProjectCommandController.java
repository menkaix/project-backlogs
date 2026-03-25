package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.dto.FeatureTreeDTO;
import com.menkaix.backlogs.models.entities.Actor;
import com.menkaix.backlogs.models.entities.Project;
import com.menkaix.backlogs.models.transients.ProjectEnvironment;
import com.menkaix.backlogs.models.transients.ProjectMember;
import com.menkaix.backlogs.models.transients.ProjectVersion;
import com.menkaix.backlogs.services.ActorService;
import com.menkaix.backlogs.services.ProjectService;
import com.menkaix.backlogs.utilities.exceptions.EntityNotFoundException;
import com.menkaix.backlogs.models.dto.RaciDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import com.menkaix.backlogs.models.values.ProjectPhase;
import com.menkaix.backlogs.models.values.ProjectState;

@RestController
@RequestMapping("/project-command")
public class ProjectCommandController {

    static Logger logger = LoggerFactory.getLogger(ProjectCommandController.class);

    @Autowired
    ActorService actorService;

    @Autowired
    private ProjectService projectService;

    @GetMapping({ "/{project}/tree" })
    public String tree(@PathVariable("project") String projectRef) {

        return projectService.tree(projectRef);
    }

    @GetMapping({ "/{project}/feature-tree" })
    public ResponseEntity<List<FeatureTreeDTO>> featureTree(@PathVariable("project") String projectRef) {

        List<FeatureTreeDTO> ans = projectService.featureTree(projectRef);

        return new ResponseEntity<List<FeatureTreeDTO>>(ans, HttpStatus.OK);
    }

    @GetMapping({ "/{project}/csv" })
    public String csv(@PathVariable("project") String projectRef) {

        return projectService.csv(projectRef);
    }

    @GetMapping({ "/{project}/csv-tasks" })
    public String csvTasks(@PathVariable("project") String projectRef) {

        return projectService.csvTasks(projectRef);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAll() {

        List<Project> ans = projectService.getAll();

        return new ResponseEntity<>(ans, HttpStatus.OK);
    }

    @PostMapping("/{project}/add-actor")
    public ResponseEntity<Actor> addActor(@PathVariable("project") String project, @RequestBody Actor actor) {

        try {
            Actor ans = actorService.addNew(project, actor);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }

    @PostMapping("/{project}/add-raci")
    public ResponseEntity<RaciDTO> addRaci(@PathVariable("project") String project, @RequestBody RaciDTO raci) {

        try {
            RaciDTO ans = projectService.addRaci(project, raci);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }

    }

    @GetMapping("/{project}/list-actors")
    public ResponseEntity<List<Actor>> listActor(@PathVariable("project") String project) {

        try {
            List<Actor> ans = actorService.listActors(project);
            return new ResponseEntity<>(ans, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }

    }

    @PatchMapping("/{project}/phase")
    public ResponseEntity<?> updatePhase(
            @PathVariable("project") String projectRef,
            @RequestBody Map<String, String> body) {
        String phaseValue = body.get("phase");
        if (phaseValue == null || phaseValue.isBlank()) {
            return new ResponseEntity<>("Le champ 'phase' est obligatoire", HttpStatus.BAD_REQUEST);
        }
        ProjectPhase phase;
        try {
            phase = ProjectPhase.valueOf(phaseValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Phase inconnue : " + phaseValue, HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(projectService.updatePhase(projectRef, phase), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/by-status/{state}")
    public ResponseEntity<?> getByStatus(@PathVariable("state") String stateValue) {
        ProjectState state;
        try {
            state = ProjectState.valueOf(stateValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("État inconnu : " + stateValue + ". Valeurs valides : ACTIVE, STANDBY, CLOSED",
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(projectService.getByState(state), HttpStatus.OK);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Gestion de l'équipe projet
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/{project}/team")
    public ResponseEntity<?> getTeam(@PathVariable("project") String projectRef) {
        try {
            return new ResponseEntity<>(projectService.getTeam(projectRef), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{project}/team/{personId}")
    public ResponseEntity<?> addTeamMember(
            @PathVariable("project") String projectRef,
            @PathVariable("personId") String personId) {
        try {
            return new ResponseEntity<>(projectService.addTeamMember(projectRef, personId), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping("/{project}/team/{personId}")
    public ResponseEntity<?> removeTeamMember(
            @PathVariable("project") String projectRef,
            @PathVariable("personId") String personId) {
        try {
            return new ResponseEntity<>(projectService.removeTeamMember(projectRef, personId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{project}/team/{personId}/refresh-skills")
    public ResponseEntity<?> refreshTeamMemberSkills(
            @PathVariable("project") String projectRef,
            @PathVariable("personId") String personId) {
        try {
            return new ResponseEntity<>(projectService.refreshTeamMemberSkills(projectRef, personId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Gestion des versions projet
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/{project}/versions")
    public ResponseEntity<?> getVersions(@PathVariable("project") String projectRef) {
        try {
            return new ResponseEntity<>(projectService.getVersions(projectRef), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{project}/versions")
    public ResponseEntity<?> addVersion(
            @PathVariable("project") String projectRef,
            @RequestBody ProjectVersion version) {
        try {
            return new ResponseEntity<>(projectService.addVersion(projectRef, version), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{project}/versions/{versionId}")
    public ResponseEntity<?> updateVersion(
            @PathVariable("project") String projectRef,
            @PathVariable("versionId") String versionId,
            @RequestBody ProjectVersion patch) {
        try {
            return new ResponseEntity<>(projectService.updateVersion(projectRef, versionId, patch), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{project}/versions/{versionId}")
    public ResponseEntity<?> removeVersion(
            @PathVariable("project") String projectRef,
            @PathVariable("versionId") String versionId) {
        try {
            return new ResponseEntity<>(projectService.removeVersion(projectRef, versionId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Gestion des environnements projet
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/{project}/environments")
    public ResponseEntity<?> getEnvironments(@PathVariable("project") String projectRef) {
        try {
            return new ResponseEntity<>(projectService.getEnvironments(projectRef), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{project}/environments")
    public ResponseEntity<?> addEnvironment(
            @PathVariable("project") String projectRef,
            @RequestBody ProjectEnvironment environment) {
        try {
            return new ResponseEntity<>(projectService.addEnvironment(projectRef, environment), HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{project}/environments/{environmentId}")
    public ResponseEntity<?> updateEnvironment(
            @PathVariable("project") String projectRef,
            @PathVariable("environmentId") String environmentId,
            @RequestBody ProjectEnvironment patch) {
        try {
            return new ResponseEntity<>(projectService.updateEnvironment(projectRef, environmentId, patch), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{project}/environments/{environmentId}")
    public ResponseEntity<?> removeEnvironment(
            @PathVariable("project") String projectRef,
            @PathVariable("environmentId") String environmentId) {
        try {
            return new ResponseEntity<>(projectService.removeEnvironment(projectRef, environmentId), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}
