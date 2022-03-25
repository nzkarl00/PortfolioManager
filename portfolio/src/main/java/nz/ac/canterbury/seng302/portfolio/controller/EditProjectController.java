package nz.ac.canterbury.seng302.portfolio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {
    /* Create default project. TODO: use database to check for this*/


    @Autowired
    private ProjectRepository repository;
    @Autowired
    private ProjectService projectService;


    /**
     * Redirects top the edit project page
     * @param model The model to be used by the application for web integration
     * @return the edit project page
     */
    @GetMapping("/edit-project")
    public String projectForm(Model model) {
        Project project = new Project("Project 2022", "", "04/Mar/2022",
                "04/Nov/2022");
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("projectStartDate", project.getStartDateString());
        model.addAttribute("projectEndDate", project.getEndDateString());
        model.addAttribute("projectDescription", project.getDescription());


        /* Return the name of the Thymeleaf template */
        return "editProject";
    }

    /**
     * Updates the project with the form data
     * @param principal
     * @param projectName Name of the project
     * @param projectStartDate Start date of the project (string)
     * @param projectEndDate End date of the project (string)
     * @param projectDescription Description of the project (string)
     * @param model The model to be used by the application for web integration
     * @return
     */
    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    ) {
        Project project = new Project("Project 2022", "", "04/Mar/2022",
                "04/Nov/2022");
        project.setName(projectName);
        project.setStartDateString(projectStartDate);
        project.setEndDateString(projectEndDate);
        project.setDescription(projectDescription);
        System.out.println("Help");
        repository.save(project);
        System.out.println("Me");
        List<Project> project2 = repository.findByProjectName(projectName);
        List<Project> projectList = projectService.getAllProjects();
        System.out.println(project2);
        System.out.println(projectList);
        return "redirect:/edit-project";
    }

}
