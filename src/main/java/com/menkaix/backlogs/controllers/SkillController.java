package com.menkaix.backlogs.controllers;

import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Skill;
import com.menkaix.backlogs.models.transients.PersonSkill;
import com.menkaix.backlogs.models.values.SkillLevel;
import com.menkaix.backlogs.services.SkillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/skill")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Catalogue de skills
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping
    public ResponseEntity<List<Skill>> listAll() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Skill skill) {
        try {
            return new ResponseEntity<>(skillService.create(skill), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getById(@PathVariable String id) {
        return skillService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Skill> getByName(@PathVariable String name) {
        return skillService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<Skill>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(skillService.findByCategory(category));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody Skill patch) {
        try {
            return ResponseEntity.ok(skillService.update(id, patch));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/levels")
    public ResponseEntity<SkillLevel[]> listLevels() {
        return ResponseEntity.ok(SkillLevel.values());
    }

    @GetMapping("/categories")
    public ResponseEntity<java.util.List<String>> listCategories() {
        return ResponseEntity.ok(skillService.findAllCategories());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Skillset d'une personne
    // ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/person/{personId}")
    public ResponseEntity<?> getPersonSkills(@PathVariable String personId) {
        try {
            List<PersonSkill> skills = skillService.getPersonSkills(personId);
            return ResponseEntity.ok(skills);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Ajoute un skill au profil d'une personne.
     * Corps attendu : { "level": "POC" }
     */
    @PostMapping("/person/{personId}/{skillId}")
    public ResponseEntity<?> addSkill(
            @PathVariable String personId,
            @PathVariable String skillId,
            @RequestBody Map<String, String> body) {

        String levelValue = body.get("level");
        if (levelValue == null || levelValue.isBlank()) {
            return ResponseEntity.badRequest().body("Le champ 'level' est obligatoire");
        }

        SkillLevel level;
        try {
            level = SkillLevel.valueOf(levelValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Niveau inconnu : " + levelValue);
        }

        try {
            People updated = skillService.addSkill(personId, skillId, level);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * Modifie le niveau d'un skill existant dans le profil d'une personne.
     * Corps attendu : { "level": "REGULAR_USE" }
     */
    @PatchMapping("/person/{personId}/{skillId}")
    public ResponseEntity<?> updateSkillLevel(
            @PathVariable String personId,
            @PathVariable String skillId,
            @RequestBody Map<String, String> body) {

        String levelValue = body.get("level");
        if (levelValue == null || levelValue.isBlank()) {
            return ResponseEntity.badRequest().body("Le champ 'level' est obligatoire");
        }

        SkillLevel level;
        try {
            level = SkillLevel.valueOf(levelValue.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Niveau inconnu : " + levelValue);
        }

        try {
            People updated = skillService.updateSkillLevel(personId, skillId, level);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retire un skill du profil d'une personne.
     */
    @DeleteMapping("/person/{personId}/{skillId}")
    public ResponseEntity<?> removeSkill(
            @PathVariable String personId,
            @PathVariable String skillId) {
        try {
            People updated = skillService.removeSkill(personId, skillId);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retourne toutes les personnes qui ont un skill donné (tous niveaux).
     */
    @GetMapping("/{skillId}/people")
    public ResponseEntity<List<People>> getPeopleBySkill(@PathVariable String skillId) {
        return ResponseEntity.ok(skillService.findPeopleBySkill(skillId));
    }

    /**
     * Retourne les personnes qui ont un skill à partir d'un niveau minimum.
     * Ex : GET /skill/{skillId}/people?minLevel=ONE_PROJECT
     */
    @GetMapping("/{skillId}/people/min-level/{minLevel}")
    public ResponseEntity<?> getPeopleBySkillAndMinLevel(
            @PathVariable String skillId,
            @PathVariable String minLevel) {

        SkillLevel level;
        try {
            level = SkillLevel.valueOf(minLevel.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Niveau inconnu : " + minLevel);
        }

        return ResponseEntity.ok(skillService.findPeopleBySkillAndMinLevel(skillId, level));
    }
}
