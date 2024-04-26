package com.menkaix.backlogs.services;

import java.util.List;
import java.util.NoSuchElementException;

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

}
