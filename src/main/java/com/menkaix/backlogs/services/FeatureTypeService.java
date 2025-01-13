package com.menkaix.backlogs.services;

import com.menkaix.backlogs.models.entities.FeatureType;
import com.menkaix.backlogs.repositories.FeatureTypeRepository;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureTypeService {

    static Logger logger = LoggerFactory.getLogger(FeatureTypeService.class);
    private final FeatureTypeRepository repo;

    @Autowired
    public FeatureTypeService(FeatureTypeRepository repo) {
        this.repo = repo;
    }

    public void build() {
        logger.info("rebuilding featureTypes");
        repo.deleteAll();

        FeatureType buttonType = new FeatureType();

        buttonType.name = "button";
        buttonType.keyFeatures.add("text");
        buttonType.keyFeatures.add("action");
        buttonType.usualTask.put("integration", "integration du bouton %s");
        buttonType.usualTask.put("action", "développement de l'action pour le bouton bouton %s");
        repo.save(buttonType);

        FeatureType valueType = new FeatureType();
        valueType.name = "value";
        valueType.keyFeatures.add("value");
        repo.save(valueType);

        FeatureType functionType = new FeatureType();
        functionType.name = "function";
        functionType.usualTask.put("dev", "développement de la fonction %s");
        repo.save(functionType);

        FeatureType screenType = new FeatureType();
        screenType.name = "screen";
        screenType.isContainer = true;
        screenType.usualTask.put("integration", "integration de l'écran %s");
        repo.save(screenType);

        FeatureType formType = new FeatureType();
        formType.name = "form";
        formType.isContainer = true;
        formType.keyFeatures.add("action");

        formType.usualTask.put("integration", "integration du formulaire %s");

        repo.save(formType);

        FeatureType fieldType = new FeatureType();
        fieldType.name = "field";
        fieldType.usualTask.put("integration", "integration du champ %s");
        fieldType.usualTask.put("dev", "implémentation des règles de validation pour %s");
        repo.save(fieldType);

        FeatureType nodeType = new FeatureType();
        nodeType.name = "node";
        nodeType.isContainer = true;
        repo.save(nodeType);

        FeatureType rgType = new FeatureType();
        rgType.name = "business-rule";
        rgType.usualTask.put("dev", "implémentation des règles de getsion  %s");
        repo.save(rgType);

        FeatureType restType = new FeatureType();
        restType.name = "rest-api";
        restType.usualTask.put("dev", "mise en place de l'API  %s");
        repo.save(restType);

        FeatureType procedureType = new FeatureType();
        procedureType.usualTask.put("dev", "developpement de la procédure  %s");
        procedureType.name = "procedure";
        repo.save(procedureType);

        FeatureType behaviour = new FeatureType();
        behaviour.name = "behaviour";
        behaviour.usualTask.put("dev", "developpement du behaviour  %s");
        repo.save(behaviour);

        FeatureType restClientType = new FeatureType();
        restClientType.usualTask.put("dev", "consommation de l'API  %s");
        restClientType.name = "rest-client";
        repo.save(restClientType);

        FeatureType displayType = new FeatureType();
        displayType.name = "display";
        displayType.usualTask.put("integration", "integration de l'affichage %s");
        repo.save(displayType);

        FeatureType listType = new FeatureType();
        listType.name = "list";
        listType.usualTask.put("integration-liste", "integration de la liste %s");
        listType.usualTask.put("integration-element", "integration d'un élémement de la liste %s");
        listType.usualTask.put("dev", "dynamisation de la liste %s");
        repo.save(listType);

        FeatureType draggableType = new FeatureType();
        draggableType.name = "draggable";
        draggableType.usualTask.put("dev", "developpement du draggable sur %s");
        repo.save(draggableType);

        FeatureType cliquableType = new FeatureType();
        cliquableType.name = "clickable";
        cliquableType.usualTask.put("dev", "developpement l'action du cliquable sur %s");
        repo.save(cliquableType);

        FeatureType hoverType = new FeatureType();
        hoverType.name = "hover";
        hoverType.usualTask.put("dev", "developpement l'action du hover sur %s");
        repo.save(hoverType);

        FeatureType dropZoneType = new FeatureType();
        dropZoneType.name = "dropzone";
        dropZoneType.usualTask.put("dev", "developpement de la drop zone sur %s");
        repo.save(dropZoneType);

        FeatureType radioGroupType = new FeatureType();
        radioGroupType.name = "radiogroup";
        radioGroupType.usualTask.put("dev", "developpement de la radio group %s");
        radioGroupType.usualTask.put("integration", "integration de la radio group %s");
        repo.save(radioGroupType);

        FeatureType optionGroupType = new FeatureType();
        optionGroupType.name = "optiongroup";
        optionGroupType.usualTask.put("dev", "developpement de la radio group %s");
        optionGroupType.usualTask.put("integration", "integration de la radio group %s");
        repo.save(optionGroupType);

        FeatureType selectType = new FeatureType();
        selectType.name = "select";
        selectType.usualTask.put("dev", "dynamisation de la liste de sélection %s");
        selectType.usualTask.put("integration", "integration du select %s");
        repo.save(selectType);

    }

    public FeatureType getFeatureType(String type) {

        List<FeatureType> ans = repo.findByName(type);
        if (ans.size() > 0) {
            return ans.get(0);
        } else {
            return null;
        }

    }

}
