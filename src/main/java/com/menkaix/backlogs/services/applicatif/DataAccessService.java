package com.menkaix.backlogs.services.applicatif;

import com.menkaix.backlogs.entities.Project;
import com.menkaix.backlogs.repositories.ProjectRepository;
import com.menkaix.backlogs.services.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DataAccessService {

    private static Logger logger = LoggerFactory.getLogger(DataAccessService.class);
    private final ProjectRepository repo;

    @Autowired
    public DataAccessService(ProjectRepository repo) {
        this.repo = repo;
    }

    // finds a project by name, code, or id
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
                    return p;
                } catch (NoSuchElementException e) {
                    logger.warn("no project found with " + in);
                    return null;
                }

            }
        }

    }

    public List<Project> findProjectByName(String projectName) {

        return repo.findByName(projectName);

    }
}
