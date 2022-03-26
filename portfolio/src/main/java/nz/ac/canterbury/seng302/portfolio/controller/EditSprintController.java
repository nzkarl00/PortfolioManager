package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {


    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private SprintRepository repository;

    /**
     * Redirects to the edit sprint html page
     * @param model The model to be used by the application for web integration
     * @return
     */
    @GetMapping("/edit-sprint")
    public String sprintForm(@AuthenticationPrincipal AuthState principal, @RequestParam(value="id") Integer projectId, @RequestParam(value="ids") Integer sprintId, Model model) throws Exception {
        /* Add sprint details to the model */

        Project project = projectService.getProjectById(projectId);

        List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);

        Sprint sprint = sprintList.get(sprintId-1);

        model.addAttribute("sprint", sprint);

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        if (role.equals("teacher")) {
            return "editSprint";
        } else {
            return "userProjectDetails";
        }
    }

    /**
     * Updates the given sprint with form data
     * @param principal
     * @param sprintName Name of the sprint (string)
     * @param sprintStartDate Start Date of the sprint (string)
     * @param sprintEndDate End Date of the sprint (string)
     * @param sprintDescription Description of the sprint (string)
     * @param model The model to be used by the application for web integration
     * @return redirects to the edit sprint page
     */
    @PostMapping("/edit-sprint")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectId") Integer projectId,
            @RequestParam(value="sprintId") Integer sprintId,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDate,
            @RequestParam(value="sprintEndDate") String sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) throws Exception {

        System.out.println("Hello");

        Sprint sprint = sprintService.getSprintById(sprintId);

        sprint.setName(sprintName);
        sprint.setDescription(sprintDescription);
        sprint.setEndDateStringSprint(sprintEndDate);
        sprint.setStartDateStringSprint(sprintStartDate);

        repository.save(sprint);

        return "redirect:/details?id=" + projectId;
    }
}
