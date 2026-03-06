package com.menkaix.backlogs.mcptools;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.People;
import com.menkaix.backlogs.services.PersonService;

@Service
public class PersonServiceMCPTools {

    private final PersonService personService;
    private final Gson gson;

    public PersonServiceMCPTools(PersonService personService, Gson gson) {
        this.personService = personService;
        this.gson = gson;
    }

    public People createPerson(String personJson) {
        if (personJson == null || personJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de la personne ne peut pas être vide");
        }
        People person = gson.fromJson(personJson, People.class);
        if (person.getFirstName() == null || person.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Le prénom est requis");
        }
        if (person.getLastName() == null || person.getLastName().isBlank()) {
            throw new IllegalArgumentException("Le nom est requis");
        }
        if (!isValidEmail(person.getEmail())) {
            throw new IllegalArgumentException("Un email valide est requis");
        }
        return personService.create(person);
    }

    public Optional<People> findPersonById(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        return personService.findById(id);
    }

    public Optional<People> findPersonByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        return personService.findByEmail(email);
    }

    public People updatePerson(String personJson) {
        if (personJson == null || personJson.isBlank()) {
            throw new IllegalArgumentException("Le JSON de la personne ne peut pas être vide");
        }
        People details = gson.fromJson(personJson, People.class);
        if (details.getId() == null || details.getId().isBlank()) {
            throw new IllegalArgumentException("L'ID est requis pour la mise à jour");
        }
        People existing = personService.findById(details.getId())
                .orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + details.getId()));
        if (details.getFirstName() != null) existing.setFirstName(details.getFirstName());
        if (details.getLastName() != null) existing.setLastName(details.getLastName());
        if (details.getEmail() != null) {
            if (!isValidEmail(details.getEmail())) {
                throw new IllegalArgumentException("L'email doit être valide");
            }
            existing.setEmail(details.getEmail());
        }
        return personService.update(existing);
    }

    public String deletePerson(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("L'ID ne peut pas être vide");
        }
        personService.findById(id).orElseThrow(() -> new RuntimeException("Personne non trouvée avec l'ID: " + id));
        personService.delete(id);
        return "Personne supprimée avec succès: " + id;
    }

    public Page<People> findAllPersons(Pageable pageable, String search) {
        return personService.findAll(pageable, search);
    }

    public List<People> findAllPersons() {
        return personService.findAll();
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
