package com.menkaix.backlogs.mcptools;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Skill;
import com.menkaix.backlogs.models.transients.PersonSkill;
import com.menkaix.backlogs.models.values.SkillLevel;
import com.menkaix.backlogs.services.SkillService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SkillServiceMCPTools {

    private final SkillService skillService;
    private final Gson gson;

    public SkillServiceMCPTools(SkillService skillService, Gson gson) {
        this.skillService = skillService;
        this.gson = gson;
    }

    // ─── Catalogue ───────────────────────────────────────────────────────────────

    public List<Skill> listSkills() {
        return skillService.findAll();
    }

    public Optional<Skill> findSkillById(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        return skillService.findById(id);
    }

    public Optional<Skill> findSkillByName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Le nom ne peut pas être vide");
        return skillService.findByName(name);
    }

    public List<Skill> findSkillsByCategory(String category) {
        if (category == null || category.isBlank()) throw new IllegalArgumentException("La catégorie ne peut pas être vide");
        return skillService.findByCategory(category);
    }

    public Skill createSkill(String skillJson) {
        if (skillJson == null || skillJson.isBlank()) throw new IllegalArgumentException("Le JSON du skill ne peut pas être vide");
        Skill skill = gson.fromJson(skillJson, Skill.class);
        if (skill.getName() == null || skill.getName().isBlank()) throw new IllegalArgumentException("Le nom du skill est requis");
        return skillService.create(skill);
    }

    public Skill updateSkill(String id, String skillJson) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        if (skillJson == null || skillJson.isBlank()) throw new IllegalArgumentException("Le JSON du skill ne peut pas être vide");
        Skill patch = gson.fromJson(skillJson, Skill.class);
        return skillService.update(id, patch);
    }

    public String deleteSkill(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("L'ID ne peut pas être vide");
        skillService.findById(id).orElseThrow(() -> new NoSuchElementException("Skill introuvable : " + id));
        skillService.delete(id);
        return "Skill supprimé avec succès : " + id;
    }

    public SkillLevel[] listSkillLevels() {
        return SkillLevel.values();
    }

    // ─── Skillset d'une personne ─────────────────────────────────────────────────

    public List<PersonSkill> getPersonSkills(String personId) {
        if (personId == null || personId.isBlank()) throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        return skillService.getPersonSkills(personId);
    }

    public People addPersonSkill(String personId, String skillId, String levelName) {
        if (personId == null || personId.isBlank()) throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        if (skillId == null || skillId.isBlank()) throw new IllegalArgumentException("L'ID du skill ne peut pas être vide");
        SkillLevel level = parseLevel(levelName);
        return skillService.addSkill(personId, skillId, level);
    }

    public People updatePersonSkillLevel(String personId, String skillId, String levelName) {
        if (personId == null || personId.isBlank()) throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        if (skillId == null || skillId.isBlank()) throw new IllegalArgumentException("L'ID du skill ne peut pas être vide");
        SkillLevel level = parseLevel(levelName);
        return skillService.updateSkillLevel(personId, skillId, level);
    }

    public People removePersonSkill(String personId, String skillId) {
        if (personId == null || personId.isBlank()) throw new IllegalArgumentException("L'ID de la personne ne peut pas être vide");
        if (skillId == null || skillId.isBlank()) throw new IllegalArgumentException("L'ID du skill ne peut pas être vide");
        return skillService.removeSkill(personId, skillId);
    }

    public List<People> findPeopleBySkill(String skillId) {
        if (skillId == null || skillId.isBlank()) throw new IllegalArgumentException("L'ID du skill ne peut pas être vide");
        return skillService.findPeopleBySkill(skillId);
    }

    public List<People> findPeopleBySkillAndMinLevel(String skillId, String levelName) {
        if (skillId == null || skillId.isBlank()) throw new IllegalArgumentException("L'ID du skill ne peut pas être vide");
        SkillLevel level = parseLevel(levelName);
        return skillService.findPeopleBySkillAndMinLevel(skillId, level);
    }

    // ─── Utilitaire ──────────────────────────────────────────────────────────────

    private SkillLevel parseLevel(String levelName) {
        if (levelName == null || levelName.isBlank()) throw new IllegalArgumentException("Le niveau ne peut pas être vide");
        try {
            return SkillLevel.valueOf(levelName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Niveau inconnu : " + levelName +
                    ". Valeurs valides : NEVER_HEARD, HEARD_OF, THEORETICAL, POC, ONE_PROJECT, SEVERAL_PROJECTS, REGULAR_USE");
        }
    }
}
