package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.repositories.ProjectRepisitory;
import com.menkaix.backlogs.utilities.exceptions.DataConflictException;
import com.menkaix.backlogs.utilities.exceptions.DataDefinitionException;

@Service
public class ProjectService {

	private static Logger logger = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	private ProjectRepisitory repo;

	@Autowired
	private ActorRepisitory actorRepisitory ;

	@Autowired
	private StoryRepository storyRepository ;

	@Autowired
	private FeatureRepository featureRepository ;
	
	@Autowired
	GeminiService geminiService ;

	public void safeCreateProject(Project projectCanditate) throws DataConflictException, DataDefinitionException {
		List<Project> prjs = null;
		if (projectCanditate.name != null) {
			prjs = repo.findByName(projectCanditate.name);
		} else if (projectCanditate.code != null) {
			prjs = repo.findByCode(projectCanditate.code);
			projectCanditate.name = projectCanditate.code;
		} else {
			throw new DataDefinitionException("Missing project name and code");
		}

		if (prjs.size() != 0) {
			throw new DataConflictException("Project already exists");
		} else {
			repo.save(projectCanditate);
		}
	}

	//finds a project by name, code, or id
	public Project findProject(String in) {

		List<Project> prjs = repo.findByName(in);
		if (prjs.size() > 0) {
			return prjs.get(0);
		} else {
			prjs = repo.findByCode(in);
			if (prjs.size() > 0) {
				return prjs.get(0);
			} else {
				try {
					Project p = repo.findById(in).get();
					return p ;
				} catch (NoSuchElementException e) {
					logger.warn("no project found with " + in);
					return null;
				}

			}
		}

	}

    public String tree(String projectRef) {

		Project p = findProject(projectRef) ;
		if(p==null) {
			return "the project where not found" ;
		}

		String ans = p.name + ": " + p.id + "\n" ;

		List<Actor> actors = actorRepisitory.findByProjectName(p.name) ;
		for(Actor a : actors){

			ans += "\t"+a.name + ": " + a.id + "\n" ;

			List<Story> stories = storyRepository.findByActorRef(p.name+"/"+a.name) ;
			for(Story s : stories){
				ans += "\t\t"+s.action + ": " + s.id + "\n" ;

				List<Feature> features = featureRepository.findByStoryId(s.id) ;
				for(Feature f : features){

					ans+="\t\t\t["+f.type+"] "+f.name+": "+f.id + "\n";

				}

			}
			ans += "\n" ;
		}

		return ans ;
    }

	public String ingestStory(String string) {
		
		String str = "Ecris un objet json contenant les proprietes suivants "
				+ "'acteur' qui représente celui qui parle, "
				+ "et 'action' l'action qu'il voudrait faire "
				+ "et optionnellement 'objectif' son benefice attendu, a partir de la phrase suivante : " +string+".";
		
		try {
			return geminiService.predictFunction(str);
		} catch (IOException e) {
			logger.error(e.getMessage());
			return e.getMessage() ;
		}
	}
}
