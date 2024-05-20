package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.models.*;
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

	public List<Project> getAll() {

		return repo.findAll() ;

	}

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
			return "project not found" ;
		}

		FullProjectDTO projectDTO = new FullProjectDTO() ;
		projectDTO.id = p.id ;
		projectDTO.name = p.name ;
		projectDTO.description = p.description ;
		projectDTO.clientName = p.clientName ;
		projectDTO.creationDate = p.creationDate ;
		projectDTO.code = p.code ;

		List<Actor> actors = actorRepisitory.findByProjectName(p.name) ;
		for(Actor a : actors){

			List<Story> stories = storyRepository.findByActorId(a.id) ;

			FullActorDTO actorDTO = new FullActorDTO();
			actorDTO.id = a.id ;
			actorDTO.name = a.name ;
			actorDTO.description = a.description ;
			actorDTO.type = a.type ;

			for(Story s : stories){

				FullStoryDTO storyDTO = new FullStoryDTO() ;

				storyDTO.id = s.id ;
				storyDTO.projectCode = projectDTO.code ;
				storyDTO.actorName = actorDTO.name ;
				storyDTO.action = s.action ;
				storyDTO.objective = s.objective ;
				storyDTO.scenario = s.scenario ;

				List<Feature> features = featureRepository.findByStoryId(s.id) ;
				for(Feature f : features){

					FullFeatureDTO fullFeatureDTO = new FullFeatureDTO() ;

					fullFeatureDTO.id=f.id;
					fullFeatureDTO.name =f.name;
					fullFeatureDTO.description=f.description;
					fullFeatureDTO.type=f.type;
					fullFeatureDTO.parentID=f.parentID;

					storyDTO.features.add(fullFeatureDTO);

				}

				actorDTO.stories.add(storyDTO) ;

			}


			projectDTO.actors.add(actorDTO) ;
;
		}

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
				.create() ;

		return gson.toJson(projectDTO) ;
    }

	public String ingestStory(String project, String prompt) {

		Project prj = findProject(project) ;

		if(prj==null){
			return  null ;
		}

		String str = buildFullPrompt(prj.description, prompt) ;

		try {
			String json = geminiService.predictFunction(str);

			Gson gson = new GsonBuilder().setPrettyPrinting().create() ;

			UserStoryDTO storyDTO = gson.fromJson(json,UserStoryDTO.class) ;

			return  createStory(prj, storyDTO) ;

		} catch (IOException e) {
			logger.error(e.getMessage());
			return e.getMessage() ;
		}
	}

	public String createStory(Project project, UserStoryDTO storyDTO){

		Gson gson = new GsonBuilder().setPrettyPrinting().create() ;

		Story newStory = new Story() ;

		List<Actor> actors = actorRepisitory.findByProjectName(project.name) ;

		boolean actorFound = false ;

		for (Actor tActor:actors) {
			if(tActor.name.equalsIgnoreCase(storyDTO.actor)){

				newStory.actorId = tActor.id ;

				actorFound =true ;
				break ;
			}
		}

		if(!actorFound){
			Actor newActor = createActor(project, storyDTO.actor) ;
			newStory.actorId = newActor.id ;
		}

		newStory.action = storyDTO.action ;
		newStory.scenario = storyDTO.scenario ;
		newStory.objective = storyDTO.objective ;

		Story ans = storyRepository.save(newStory) ;

		return gson.toJson(ans) ;
	}

	private Actor createActor(Project p, String actorName){
		Actor actor = new Actor() ;
		actor.projectName = p.name ;
		actor.name = actorName.toLowerCase() ;

		return actorRepisitory.save(actor) ;
	}

	private String buildFullPrompt(String description, String prompt){

		String str = "Tu es un Business Analyst, et ton travail est de décrire les systèmes informatiques et les logiciels " +
				"de façon à ce que ce soit compréhensible par des personnes qui n'on pas de base de programmation." +
				"Voici la description du projet sur lequel tu travailles : %s\n" +
				"Ecris un objet json contenant les proprietés suivantes : \n" +
				"- 'actor' : qui représente celui qui parle,  \n" +
				"- 'action' : décrit l'action qu'il voudrait faire  \n" +
				"- 'objectif' (optionnel) : décrit son benefice attendu, sa motivation, ou alors une nouvelle possibilité d'action à postériori \n" +
				"- 'scenario' (optionnel) : une paragraphe qui décrit les étapes exécutées par l'acteur pour réaliser l'opération.\n" +
				"Utilise en entrée, apres l'avoir reformulée pour qu'elle soit compréhensible par des personnes qui n'ont pas de notion de programmation, " +
				"la phrase suivante : %s." ;

		return String.format(str, description, prompt) ;
	}


}
