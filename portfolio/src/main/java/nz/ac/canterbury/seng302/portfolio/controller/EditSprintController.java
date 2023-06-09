package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
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
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;
    @Autowired
    private DateSocketService dateSocketService;

    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Redirects to the edit sprint html page
     * @param model The model to be used by the application for web integration
     * @return
     */
    @GetMapping("/edit-sprint")
    public String sprintForm(@AuthenticationPrincipal AuthState principal, @RequestParam(value="id") Integer projectId, @RequestParam(value="ids") Integer sprintId, Model model) throws Exception {
        /* Add sprint details to the model */

        Project project = projectService.getProjectById(projectId);
        Sprint sprint = sprintService.getSprintById(sprintId);
        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);
        model.addAttribute("projectStart", project.getStartDateStringHtml());
        model.addAttribute("projectEnd", project.getEndDateStringHtml());
        model.addAttribute("sprint", sprint);
        model.addAttribute("project", project);
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            return "editSprint";
        } else {
            return "projectDetails";
        }
    }

    /**
     * Updates the given sprint with form data
     * @param principal auth token
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
        String role = AuthStateInformer.getRole(principal);
        if (role.equals("teacher") || role.equals("admin")) {

            Sprint sprint = sprintService.getSprintById(sprintId);
            Project project = projectService.getProjectById(projectId);

            Date projStartDate = DateParser.stringToDate(project.getStartDateString()); // project.getStartDateString();
            Date projEndDate = DateParser.stringToDate(project.getEndDateString()); // project.getEndDateString();
            Date checkStartDate = DateParser.stringToDate(sprintStartDate);
            Date checkEndDate = DateParser.stringToDate(sprintEndDate);

            errorShow = "";

            String redirect = "redirect:edit-sprint?id=" + projectId + "&ids=" + sprintId;

            // check if sprint name is blank
            if (sprintName.isBlank()) {
                errorCode = "Sprint requires a name";
                return redirect;
            }
            sprint.setName(sprintName);
            sprint.setDescription(sprintDescription);

            // check if the sprint dates are within the project dates
            if (!checkStartDate.after(projStartDate) || !checkEndDate.before(projEndDate)) {

                // check to is if the sprint isn't equal to the project start and end date
                if (!checkStartDate.equals(projStartDate) && !checkEndDate.equals(projEndDate)) {
                    errorCode = "Sprint is outside of the Project's timeline";
                    return redirect;
                }
            }

            // check if sprint start is before sprint end
            if (!checkStartDate.before(checkEndDate)) {
                errorCode = "Start and End date overlap";
                return redirect;
            }

            // moving the harder sprint date checking to a service helper class that can be easily unit tested
            List<Sprint> sprints = sprintService.getSprintByParentId(projectId);
            if (!DateParser.sprintDateCheck(sprints, sprint, checkStartDate, checkEndDate)) {
                errorCode = "Sprint dates overlap with " + DateParser.sprintIdFail;
                return redirect;
            }

            // show no errors, update the sprint and save it to the db
            errorShow = "display:none;";
            sprint.setStartDate(checkStartDate);
            sprint.setEndDate(checkEndDate);
            repository.save(sprint);
            dateSocketService.sendSprintCalendarChange(projectId);
        }
        return "redirect:details?id=" + projectId;
    }

}
