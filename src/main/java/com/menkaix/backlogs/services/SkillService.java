package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.models.entities.Skill;
import com.menkaix.backlogs.models.transients.PersonSkill;
import com.menkaix.backlogs.models.values.SkillLevel;
import com.menkaix.backlogs.repositories.PeopleRepository;
import com.menkaix.backlogs.repositories.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final PeopleRepository peopleRepository;

    public SkillService(SkillRepository skillRepository, PeopleRepository peopleRepository) {
        this.skillRepository = skillRepository;
        this.peopleRepository = peopleRepository;
    }

    // ─── Catalogue ──────────────────────────────────────────────────────────────

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public Optional<Skill> findById(String id) {
        return skillRepository.findById(id);
    }

    public Optional<Skill> findByName(String name) {
        return skillRepository.findByName(name);
    }

    public List<Skill> findByCategory(String category) {
        return skillRepository.findByCategory(category);
    }

    public Skill create(Skill skill) {
        if (skill.getName() == null || skill.getName().isBlank()) {
            throw new IllegalArgumentException("Le nom du skill est obligatoire");
        }
        return skillRepository.save(skill);
    }

    public Skill update(String id, Skill patch) {
        Skill existing = skillRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Skill introuvable : " + id));
        if (patch.getName() != null && !patch.getName().isBlank()) {
            existing.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            existing.setDescription(patch.getDescription());
        }
        if (patch.getCategory() != null) {
            existing.setCategory(patch.getCategory());
        }
        if (patch.getTags() != null) {
            existing.setTags(patch.getTags());
        }
        return skillRepository.save(existing);
    }

    public void delete(String id) {
        skillRepository.deleteById(id);
    }

    // ─── Skillset d'une personne ─────────────────────────────────────────────────

    public List<PersonSkill> getPersonSkills(String personId) {
        People person = findPerson(personId);
        return person.getSkills();
    }

    public People addSkill(String personId, String skillId, SkillLevel level) {
        People person = findPerson(personId);
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new NoSuchElementException("Skill introuvable : " + skillId));

        boolean alreadyPresent = person.getSkills().stream()
                .anyMatch(ps -> ps.getSkillId().equals(skillId));
        if (alreadyPresent) {
            throw new IllegalArgumentException("La personne possède déjà ce skill. Utilisez PATCH pour modifier le niveau.");
        }

        person.getSkills().add(new PersonSkill(skillId, skill.getName(), level));
        return peopleRepository.save(person);
    }

    public People updateSkillLevel(String personId, String skillId, SkillLevel level) {
        People person = findPerson(personId);

        PersonSkill ps = person.getSkills().stream()
                .filter(s -> s.getSkillId().equals(skillId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Ce skill n'est pas dans le profil de la personne"));

        ps.setLevel(level);
        return peopleRepository.save(person);
    }

    public People removeSkill(String personId, String skillId) {
        People person = findPerson(personId);

        boolean removed = person.getSkills().removeIf(ps -> ps.getSkillId().equals(skillId));
        if (!removed) {
            throw new NoSuchElementException("Ce skill n'est pas dans le profil de la personne");
        }

        return peopleRepository.save(person);
    }

    public List<People> findPeopleBySkill(String skillId) {
        return peopleRepository.findAll().stream()
                .filter(p -> p.getSkills().stream().anyMatch(ps -> ps.getSkillId().equals(skillId)))
                .toList();
    }

    public List<People> findPeopleBySkillAndMinLevel(String skillId, SkillLevel minLevel) {
        return peopleRepository.findAll().stream()
                .filter(p -> p.getSkills().stream()
                        .anyMatch(ps -> ps.getSkillId().equals(skillId)
                                && ps.getLevel().getRank() >= minLevel.getRank()))
                .toList();
    }

    // ─── Utilitaire ─────────────────────────────────────────────────────────────

    private People findPerson(String personId) {
        return peopleRepository.findById(personId)
                .orElseThrow(() -> new NoSuchElementException("Personne introuvable : " + personId));
    }
}
