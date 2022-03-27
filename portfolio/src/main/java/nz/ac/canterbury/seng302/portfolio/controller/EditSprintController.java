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
import java.util.Date;
import java.util.List;
import java.util.Objects;


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

        Sprint sprint = sprintService.getSprintById(sprintId);

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
        Project project = projectService.getProjectById(projectId);

        Date projStartDate = project.getStartDate();
        String stringStartDate = project.getStartDateString();
        String stringEndDate = project.getEndDateString();
        Date projEndDate = project.getEndDate();
        Date checkStartDate = Project.stringToDate(sprintStartDate);
        Date checkEndDate = Project.stringToDate(sprintEndDate);



        sprint.setName(sprintName);
        sprint.setDescription(sprintDescription);
        if ((projStartDate.before(checkStartDate) | (Objects.equals(stringStartDate, sprintStartDate))) & (projEndDate.after(checkEndDate) | (Objects.equals(stringEndDate, sprintEndDate)))) {
            if (checkStartDate.before(checkEndDate)) {
                sprint.setStartDateStringSprint(sprintStartDate);
            }
            if (checkEndDate.after(checkStartDate)) {
                sprint.setEndDateStringSprint(sprintEndDate);
            }
        }


        repository.save(sprint);

        return "redirect:/details?id=" + projectId;
    }
}
