package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// more info here https://codebun.com/spring-boot-crud-application-using-thymeleaf-and-spring-data-jpa/

@Service
public class ProjectService {
    @Autowired
    private ProjectRepository repository;

    Logger logger = LoggerFactory.getLogger(ProjectService.class);

    /**
     * Get list of all projects
     */
    public List<Project> getAllProjects() {
        List<Project> list = (List<Project>) repository.findAll();
        return list;
    }

    /**
     * Get project by id
     */
    public Project getProjectById(Integer id) throws CustomExceptions.ProjectItemNotFoundException {
        logger.trace(String.format("Getting project by ID=<%d>", id));
        Optional<Project> project = repository.findById(id);
        if(project.orElse(null)!=null) {
            return project.get();
        }
        else
        {
            throw new CustomExceptions.ProjectItemNotFoundException("Project not found");
        }
    }

    /**
     * Create a default project
     */
    public Project createDefaultProject() throws Exception {
        Project project = new Project(
            "New Project",
            "A newly created project. Edit it!",
            LocalDate.now(),
            LocalDate.now().plusMonths(3)
        );
        repository.save(project);
        return project;
    }
}
