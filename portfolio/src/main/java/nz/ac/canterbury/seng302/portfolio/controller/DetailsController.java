package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.text.SimpleDateFormat;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private SprintRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private AccountClientService accountClientService;

    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Returns the html page based on the user's role
     * @param principal the auth token
     * @param model The model to be used by the application for web integration
     * @return The html page to direct to
     * @throws Exception
     */
    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal , @RequestParam(value="id") Integer projectId, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        model.addAttribute("date",  DateParser.displayDate(userReply));
        model.addAttribute("username", userReply.getUsername());

        List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);
        model.addAttribute("sprints", sprintList);
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if (role.equals("teacher") || role.equals("admin")) {
            return "teacherProjectDetails";
        } else {
            return "userProjectDetails";
        }
    }

    /**
     * The mapping to delete a sprint
     * @param principal auth token
     * @param projectId id param for project to delete sprint from
     * @param sprintId sprint id under project to delete
     * @param model the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("delete-sprint")
    public String sprintDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="deleteprojectId") Integer projectId,
            @RequestParam(value="sprintId") Integer sprintId,
            Model model
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);

        Sprint sprint = sprintService.getSprintById(sprintId);

        if (role.equals("teacher")) {
            repository.deleteById(sprintId);
        }

        Integer i = 1;

        List<Sprint> sprintLists = sprintService.getSprintByParentId(projectId);
        for (Sprint temp : sprintLists) {

            temp.setLabel("Sprint " + i);
            repository.save(temp);
            i += 1;

        }

        return "redirect:/details?id=" + projectId;
    }

    /**
     * The mapping to create a new sprint for a specified project
     * @param principal auth token
     * @param projectId id param for project to create sprint for
     * @param model the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("/new-sprint")
    public String newSprint(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectId") Integer projectId,
            Model model
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);
        List<Sprint> sprints = sprintService.getSprintByParentId(projectId);

        Integer valueId = 0;

        valueId = sprints.size();

        Project project = projectService.getProjectById(projectId);
        Date startDate;
        Date endDate;
        Calendar calendar = Calendar.getInstance();
        int noOfDays = 21;
        //check to see if the project already has a sprint
        if (valueId == 0) {
            // if not use the project start date to get the new sprint start date
            startDate = project.getStartDate();

            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
            endDate = calendar.getTime();

        } else {
            // if it does use the last sprint end date for the new sprint start date
            startDate = sprints.get(sprints.size()-1).getEndDate();
            calendar.setTime(startDate);
            calendar.add(Calendar.DATE, +1); // add one more day to the date so sprints don't overlap with end date
            startDate = calendar.getTime();
            calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
            endDate = calendar.getTime();
            if (endDate.after(project.getEndDate())) {

                endDate = project.getEndDate();

            }

            if (Objects.equals(sprints.get(sprints.size() - 1).getEndDateString(), project.getEndDateString())) {

                errorShow="";
                errorCode="There is not enough time in your project for another sprint";
                return "redirect:/details?id=" + projectId;

            }

        }

        valueId += 1;


        if (role.equals("teacher")) {
            Sprint sprint = new Sprint(projectId, "Sprint " + valueId.toString(), "Sprint " + valueId.toString(), "", startDate, endDate);
            repository.save(sprint);
            return "redirect:/edit-sprint?id=" + projectId +"&ids=" + sprint.getId();
        }

        return "redirect:/details?id=" + projectId;
    }

}
