package com.menkaix.backlogs.services;

import com.menkaix.backlogs.entities.FeatureType;
import com.menkaix.backlogs.repositories.FeatureTypeRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FeatureTypeService {

    static Logger logger = LoggerFactory.getLogger(FeatureTypeService.class);

    @Autowired
    FeatureTypeRepository repo ;

    @PostConstruct
    public void build(){
        logger.info("rebuilding featureTypes");
        repo.deleteAll();

        FeatureType buttonType = new FeatureType() ;

        buttonType.name = "button" ;
        buttonType.keyFeatures.add("text");
        buttonType.keyFeatures.add("action");
        repo.save(buttonType) ;

        FeatureType valueType = new FeatureType();
        valueType.name = "value";
        valueType.keyFeatures.add("value");
        repo.save(valueType) ;

        FeatureType functionType = new FeatureType();
        functionType.name = "function";
        repo.save(functionType) ;

        FeatureType screenType = new FeatureType();
        screenType.name = "screen";
        screenType.isContainer = true ;
        repo.save(screenType) ;

        FeatureType formType = new FeatureType();
        formType.name = "form";
        formType.isContainer = true ;
        repo.save(formType) ;

        FeatureType fieldType = new FeatureType();
        fieldType.name = "field";
        fieldType.keyFeatures.add("type");
        repo.save(fieldType) ;

        FeatureType nodeType = new FeatureType();
        nodeType.name = "node";
        nodeType.isContainer = true ;
        repo.save(nodeType) ;

    }

}
