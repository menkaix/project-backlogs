package com.menkaix.backlogs.mcptools;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Skill;
import com.menkaix.backlogs.models.transients.PersonSkill;
import com.menkaix.backlogs.models.values.SkillLevel;

@Service
public class SkillToolsRegistry {

    private final SkillServiceMCPTools tools;
    private final Gson gson;

    public SkillToolsRegistry(SkillServiceMCPTools tools, Gson gson) {
        this.tools = tools;
        this.gson = gson;
    }

    // ─── Catalogue ───────────────────────────────────────────────────────────────

    @Tool(name = "list-skills", description = "Liste tous les skills du catalogue.")
    public String listSkills() {
        try {
            List<Skill> skills = tools.listSkills();
            return gson.toJson(Map.of("skills", skills, "count", skills.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-skill-by-id", description = "Récupère un skill par son identifiant unique.")
    public String findSkillById(String id) {
        try {
            Optional<Skill> skill = tools.findSkillById(id);
            return skill.isPresent() ? gson.toJson(skill.get())
                    : gson.toJson(Map.of("message", "Skill non trouvé: " + id));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-skill-by-name", description = "Récupère un skill par son nom exact.")
    public String findSkillByName(String name) {
        try {
            Optional<Skill> skill = tools.findSkillByName(name);
            return skill.isPresent() ? gson.toJson(skill.get())
                    : gson.toJson(Map.of("message", "Skill non trouvé pour le nom: " + name));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-skills-by-category", description = "Récupère tous les skills d'une catégorie donnée.")
    public String findSkillsByCategory(String category) {
        try {
            List<Skill> skills = tools.findSkillsByCategory(category);
            return gson.toJson(Map.of("skills", skills, "count", skills.size(), "category", category));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "create-skill", description = "Crée un nouveau skill dans le catalogue. Champs requis: name. Optionnels: description, category, tags (liste de strings).")
    public String createSkill(String skillJson) {
        try {
            return gson.toJson(tools.createSkill(skillJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-skill", description = "Met à jour un skill existant. Paramètres: id (requis), skillJson (champs modifiables: name, description, category, tags).")
    public String updateSkill(String id, String skillJson) {
        try {
            return gson.toJson(tools.updateSkill(id, skillJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "delete-skill", description = "Supprime définitivement un skill du catalogue par son ID.")
    public String deleteSkill(String id) {
        try {
            return gson.toJson(Map.of("message", tools.deleteSkill(id)));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "list-skill-levels", description = "Liste tous les niveaux de maîtrise disponibles: NEVER_HEARD, HEARD_OF, THEORETICAL, POC, ONE_PROJECT, SEVERAL_PROJECTS, REGULAR_USE.")
    public String listSkillLevels() {
        try {
            SkillLevel[] levels = tools.listSkillLevels();
            return gson.toJson(Map.of("levels", levels));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    // ─── Skillset d'une personne ─────────────────────────────────────────────────

    @Tool(name = "get-person-skills", description = "Récupère le skillset complet d'une personne par son ID.")
    public String getPersonSkills(String personId) {
        try {
            List<PersonSkill> skills = tools.getPersonSkills(personId);
            return gson.toJson(Map.of("skills", skills, "count", skills.size(), "personId", personId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "add-person-skill", description = "Ajoute un skill au profil d'une personne avec un niveau de maîtrise. Niveaux valides: NEVER_HEARD, HEARD_OF, THEORETICAL, POC, ONE_PROJECT, SEVERAL_PROJECTS, REGULAR_USE.")
    public String addPersonSkill(String personId, String skillId, String levelName) {
        try {
            People person = tools.addPersonSkill(personId, skillId, levelName);
            return gson.toJson(person);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-person-skill-level", description = "Met à jour le niveau de maîtrise d'un skill déjà associé à une personne.")
    public String updatePersonSkillLevel(String personId, String skillId, String levelName) {
        try {
            People person = tools.updatePersonSkillLevel(personId, skillId, levelName);
            return gson.toJson(person);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "remove-person-skill", description = "Retire un skill du profil d'une personne.")
    public String removePersonSkill(String personId, String skillId) {
        try {
            People person = tools.removePersonSkill(personId, skillId);
            return gson.toJson(person);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-people-by-skill", description = "Récupère toutes les personnes possédant un skill donné, quel que soit leur niveau.")
    public String findPeopleBySkill(String skillId) {
        try {
            List<People> people = tools.findPeopleBySkill(skillId);
            return gson.toJson(Map.of("people", people, "count", people.size(), "skillId", skillId));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-people-by-skill-and-min-level", description = "Récupère les personnes possédant un skill avec un niveau de maîtrise minimum. Niveaux valides: NEVER_HEARD, HEARD_OF, THEORETICAL, POC, ONE_PROJECT, SEVERAL_PROJECTS, REGULAR_USE.")
    public String findPeopleBySkillAndMinLevel(String skillId, String levelName) {
        try {
            List<People> people = tools.findPeopleBySkillAndMinLevel(skillId, levelName);
            return gson.toJson(Map.of("people", people, "count", people.size(), "skillId", skillId, "minLevel", levelName));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
