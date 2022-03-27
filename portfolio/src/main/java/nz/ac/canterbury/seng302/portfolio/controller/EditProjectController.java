package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
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
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {


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
    public String projectForm(@AuthenticationPrincipal AuthState principal , @RequestParam(value="id") Integer projectId, Model model)  throws Exception  {

        System.out.println("please");
        Project project = projectService.getProjectById(projectId);
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("project", project);
        model.addAttribute("projectStartDate", project.getStartDateString());
        model.addAttribute("projectEndDate", project.getEndDateString());
        model.addAttribute("projectDescription", project.getDescription());

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        if (role.equals("teacher")) {
            return "editProject";
        } else {
            return "userProjectDetails";
        }
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
            @RequestParam(value="projectId") Integer projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    )  throws Exception  {
        System.out.println("Hello");
        Project project = projectService.getProjectById(projectId);
        System.out.println("World");
        project.setName(projectName);

        Date checkStartDate = Project.stringToDate(projectStartDate);
        Date checkEndDate = Project.stringToDate(projectEndDate);

        if (checkStartDate.before(checkEndDate)) {
            project.setStartDateString(projectStartDate);
        }
        if (checkEndDate.after(checkStartDate)) {
            project.setEndDateString(projectEndDate);
        }
        project.setDescription(projectDescription);
        System.out.println("Its");
        repository.save(project);
        System.out.println("Me");
        return "redirect:/details?id=" + projectId;
    }

}
