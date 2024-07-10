package com.menkaix.backlogs.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.menkaix.backlogs.entities.Actor;
import com.menkaix.backlogs.entities.Feature;
import com.menkaix.backlogs.entities.Story;
import com.menkaix.backlogs.entities.Task;
import com.menkaix.backlogs.models.*;
import com.menkaix.backlogs.repositories.ActorRepisitory;
import com.menkaix.backlogs.repositories.FeatureRepository;
import com.menkaix.backlogs.repositories.StoryRepository;
import com.menkaix.backlogs.repositories.TaskRepository;

import com.menkaix.backlogs.services.applicatif.DataAccessService;
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
	private TaskRepository taskRepository ;

	@Autowired
	private StoryRepository storyRepository ;

	@Autowired
	private FeatureRepository featureRepository ;
	
	@Autowired
	private FeatureService featureService ;
	
	@Deprecated
	@Autowired
	private GeminiService geminiService ;


	@Autowired
	private DataAccessService accessService ;

	private FullProjectDTO objectTree(String projectRef){
	
		Project p = accessService.findProject(projectRef) ;
		if(p==null) {
			return null ;
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
	
					List<Task> tasks = taskRepository.findByIdReference("feature/"+fullFeatureDTO.id) ;
	
					for (Task task : tasks) {
						FullTaskDTO taskDTO = new FullTaskDTO() ;
	
						taskDTO.id=task.id;
						taskDTO.projectId=task.projectId;
						taskDTO.reference=task.reference;
						taskDTO.title=task.title;
						taskDTO.description=task.description;
						taskDTO.dueDate=task.dueDate;
						taskDTO.doneDate=task.doneDate;
						taskDTO.idReference=task.idReference;
	
						
						taskDTO.name=task.name;
						taskDTO.creationDate= task.creationDate;
						taskDTO.lastUpdateDate= task.lastUpdateDate;
						
						fullFeatureDTO.tasks.add(taskDTO);						
	
					}
	
					storyDTO.features.add(fullFeatureDTO);
	
				}
	
				actorDTO.stories.add(storyDTO) ;
	
			}
	
	
			projectDTO.actors.add(actorDTO) ;
	
		}
	
		return projectDTO ;
	}

	private Actor createActor(Project p, String actorName){
		Actor actor = new Actor() ;
		actor.projectName = p.name ;
		actor.name = actorName.toLowerCase() ;
	
		return actorRepisitory.save(actor) ;
	}

	private String actorToCsv(FullActorDTO actor) {
	
		String ans = "" ;
	
		if(actor.stories.size()>0){
			for (FullStoryDTO story: actor.stories) {
				String tStoryPart =  "\""+story.actorName + "\", \"" + story.action+"\", \""+story.scenario+"\"" ;
	
				if(story.features.size()>0){
	
					for (FullFeatureDTO feature:story.features) {
	
						String featurePart = tStoryPart +", \"[" + feature.type+"] " + feature.name + "\"" ;
	
						if(feature.tasks.size()>0){
							for (FullTaskDTO task : feature.tasks) {
								ans += featurePart + ", \""+task.title +"\"" + "\n" ;
							}
						}
						else {
							ans += featurePart + "\n" ;
						}
					}
	
				}
				else{
					ans += tStoryPart + "\n";
				}
			}
		}
		else {
			ans = "\""+actor.name+"\" \n" ;
		}
	
	
	
	
		return  ans ;
	}

	@Deprecated
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



	public String tree(String projectRef) {

		FullProjectDTO tAns = objectTree(projectRef) ;


		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
				.create() ;

		return gson.toJson(tAns) ;
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

	public String csv(String projectRef) {

		FullProjectDTO project = objectTree(projectRef);

		String ans = "" ;

		for (FullActorDTO actor: project.actors ) {
			ans += actorToCsv(actor) + "\n";
		}



		return ans ;

	}

	public String csvTasks(String projectRef) {

		Project prj = accessService.findProject(projectRef) ;

		List<Task> tasks = taskRepository.findByProjectId(prj.id) ;

		String ans = "" ;

		for (Task task: tasks) {
			ans += task.title + ", \"" + task.description + "\"\n" ;
		}

		List<Actor> actors = actorRepisitory.findByProjectName(prj.name) ;

		for (Actor actor: actors) {

			List<Story> stories = storyRepository.findByActorId(actor.id);
			for (Story story: stories) {

				List<Feature> features = featureRepository.findByStoryId(story.id) ;

				for (Feature feature: features) {
					List<Task> tasksOfFeature = taskRepository.findByIdReference("feature/"+feature.id);

					for (Task taskOfFeature: tasksOfFeature) {

						ans += taskOfFeature.title + ", \"" + taskOfFeature.description + "\"\n" ;

					}

				}

			}

		}

		return ans ;

	}
	
	
	
	public List<FeatureTreeDTO> featureTree(String projectRef){
		
		ArrayList<FeatureTreeDTO> ans = new ArrayList<>() ;
		
		Project prj = accessService.findProject(projectRef) ;
		
		List<Feature> features = featureService.getFeatures(prj);
		
		List<FeatureTreeDTO> allDtos = new ArrayList<FeatureTreeDTO>();

		for (Feature feature : features) {
			
			FeatureTreeDTO tmpDTO = new FeatureTreeDTO() ;
			
			tmpDTO.id= feature.id;
			tmpDTO.name= feature.name;
			tmpDTO.description=feature.description;
			tmpDTO.parentID= feature.parentID;
			tmpDTO.type= feature.type;
			
			allDtos.add(tmpDTO);
		}
		
		return order(allDtos) ;
	}
	
	private List<FeatureTreeDTO> order(List<FeatureTreeDTO> in){
		
		ArrayList<FeatureTreeDTO> ans = new ArrayList<>() ;
		
		while(in.size()>0) {
			
			for(int i = 0 ; i<in.size() ; i++) {
				
				if(in.get(i).parentID == null || in.get(i).parentID.length()==0) {
					ans.add(in.get(i));
					in.remove(i);
					break ;
				}else {
					for (FeatureTreeDTO featureTreeDTO : ans) {
						if(featureTreeDTO.id.equals(in.get(i).parentID)) {
							ans.add(in.get(i));
							in.remove(i);
							break ;
						}
					}
				}
				
			}
			
		}
		
	
		return ans ;
	
	}
	
	

	@Deprecated
	public String ingestStory(String project, String prompt) {
	
		Project prj = accessService.findProject(project) ;
	
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
}
