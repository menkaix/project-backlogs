package com.menkaix.backlogs.mcptools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.menkaix.backlogs.models.entities.People;

@Service
public class PersonToolsRegistry {

    private final PersonServiceMCPTools tools;
    private final Gson gson;

    public PersonToolsRegistry(PersonServiceMCPTools tools, Gson gson) {
        this.tools = tools;
        this.gson = gson;
    }

    @Tool(name = "create-person", description = "Crée une nouvelle personne. Champs requis: firstName, lastName, email (format valide).")
    public String createPerson(String personJson) {
        try {
            return gson.toJson(tools.createPerson(personJson));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-person-by-id", description = "Récupère une personne par son identifiant unique.")
    public String findPersonById(String id) {
        try {
            Optional<People> person = tools.findPersonById(id);
            return person.isPresent() ? gson.toJson(person.get())
                    : gson.toJson(Map.of("message", "Personne non trouvée: " + id));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-person-by-email", description = "Récupère une personne par son adresse email.")
    public String findPersonByEmail(String email) {
        try {
            Optional<People> person = tools.findPersonByEmail(email);
            return person.isPresent() ? gson.toJson(person.get())
                    : gson.toJson(Map.of("message", "Personne non trouvée pour l'email: " + email));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "update-person", description = "Met à jour une personne existante. Champ requis: id. Champs modifiables: firstName, lastName, email, description.")
    public String updatePerson(String personDetails) {
        try {
            return gson.toJson(tools.updatePerson(personDetails));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "delete-person", description = "Supprime définitivement une personne par son ID.")
    public String deletePerson(String id) {
        try {
            return gson.toJson(Map.of("message", tools.deletePerson(id)));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "find-persons", description = "Recherche des personnes avec pagination. Paramètres: page (défaut 0), size (défaut 10, max 100), search (optionnel, cherche dans prénom/nom/email).")
    public String findPersons(int page, int size, String search) {
        try {
            if (page < 0) page = 0;
            if (size <= 0) size = 10;
            if (size > 100) size = 100;
            Pageable pageable = PageRequest.of(page, size);
            Page<People> persons = tools.findAllPersons(pageable, search);
            Map<String, Object> result = new HashMap<>();
            result.put("content", persons.getContent());
            result.put("totalElements", persons.getTotalElements());
            result.put("totalPages", persons.getTotalPages());
            result.put("currentPage", persons.getNumber());
            result.put("size", persons.getSize());
            result.put("hasNext", persons.hasNext());
            result.put("hasPrevious", persons.hasPrevious());
            return gson.toJson(result);
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }

    @Tool(name = "list-all-persons", description = "Récupère toutes les personnes sans pagination.")
    public String listAllPersons() {
        try {
            List<People> persons = tools.findAllPersons();
            return gson.toJson(Map.of("persons", persons, "count", persons.size()));
        } catch (Exception e) {
            return gson.toJson(Map.of("error", e.getMessage()));
        }
    }
}
