package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.rpc.context.AttributeContext;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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
    @Autowired
    private NavController navController;

    String errorShow = "display:none;";
    String errorCode = "";
    String successCalendarShow = "display:none;";
    String successCalendarCode = "";
    String errorCalendarShow = "display:none;";
    String errorCalendarCode = "";

    // Colors for the sprints
    List<String> colors = new ArrayList<String>(Arrays.asList("#a3c7d7", "#067d46", "#19DAFF","#f5deb3", "#d470a2", "#9acd32",
        "#a2add0", "#c9a0dc", "#9f1d35", "#e34234", "#5b92e5", "#66023c",
        "#483c32", "#0abab5", "#eee600", "#f28500", "#ffcc33", "#a7fc00",
        "#cdc9c9", "#eee9e9", "#836fff", "#473c8b", "#708090", "#c0c0c0",
        "#ffd800", "#ff2400", "#e30b5d", "#003153", "#ff5a36", "#e5e4e2",
        "#cc3333", "#1c39bb", "#1ca9c9", "#002147", "#d3af37", "#30bfbf",
        "#fdbe02", "#eaa221", "#32cd32", "#c5cbe1"));

    /**
     * Returns the html page based on the user's role
     *
     * @param principal the auth token
     * @param model     The model to be used by the application for web integration
     * @return The html page to direct to
     * @throws Exception
     */
    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal, @RequestParam(value = "id") Integer projectId, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);

        List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);
        model.addAttribute("sprints", sprintList);
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("successCalendarShow", successCalendarShow);
        model.addAttribute("successCalendarCode", successCalendarCode);
        model.addAttribute("errorCalendarShow", errorCalendarShow);
        model.addAttribute("errorCalendarCode", errorCalendarCode);

        // TODO Change this to get lists from repo
        List<Deadline> deadlineList = new ArrayList<>();
        List<Date> eventList = new ArrayList<>();
        List<Date> milestoneList = new ArrayList<>();
        eventList.add(new Date());
        deadlineList.add(new Deadline(project, "testDeadline", "testDeadlineDesc", sprintList.get(0).getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        deadlineList.add(new Deadline(project, "testDeadline2", "testDeadlineDesc2", sprintList.get(0).getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        milestoneList.add(sprintList.get(0).getStartDate());
        milestoneList.add(new Date());
        model.addAttribute("deadlines", deadlineList);
        model.addAttribute("milestones", milestoneList);
        model.addAttribute("events", eventList);
        model.addAttribute("colors", colors);
        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";
        successCalendarShow = "display:none;";
        successCalendarCode = "";
        errorCalendarShow = "display:none;";
        errorCalendarCode = "";

        // Below code is just begging to be added as a method somewhere...
        String role = AuthStateInformer.getRole(principal);

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("roleName", "teacher");
            return "teacherProjectDetails";
        } else {
            model.addAttribute("roleName", "student");
            return "userProjectDetails";
        }
    }

    /**
     * The mapping to delete a sprint
     *
     * @param principal auth token
     * @param projectId id param for project to delete sprint from
     * @param sprintId  sprint id under project to delete
     * @param model     the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("delete-sprint")
    public String sprintDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "deleteprojectId") Integer projectId,
            @RequestParam(value = "sprintId") Integer sprintId,
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

        return "redirect:details?id=" + projectId;
    }

    @PostMapping("/details")
    public String sprintSaveFromCalendar(@AuthenticationPrincipal AuthState principal,
                                         @RequestParam(value="id") Integer projectId,
                                         @RequestParam(value="sprintId") Integer sprintId,
                                         @RequestParam(value="start") Date sprintStartDate,
                                         @RequestParam(value="end") Date sprintEndDate) throws Exception {

        String role = AuthStateInformer.getRole(principal);
        String redirect = "redirect:details?id=" + projectId;
        sprintEndDate = new Date(sprintEndDate.getTime() - Duration.ofDays(1).toMillis());
        if (role.equals("teacher") || role.equals("admin")) {
            List<Sprint> sprints = sprintService.getSprintByParentId(projectId);
            Sprint sprint = sprintService.getSprintById(sprintId);
            Project project = projectService.getProjectById(projectId);

            Date projStartDate = DateParser.stringToDate(project.getStartDateString()); // project.getStartDateString();
            Date projEndDate = DateParser.stringToDate(project.getEndDateString()); // project.getEndDateString();
            Date checkStartDate = sprintStartDate;
            Date checkEndDate = sprintEndDate;

            // check if the sprint dates are within the project dates
            if (!checkStartDate.after(projStartDate) || !checkEndDate.before(projEndDate)) {
                // check to is if the sprint isn't equal to the project start and end date
                if (!checkStartDate.equals(projStartDate) && !checkEndDate.equals(projEndDate)) {
                    successCalendarShow = "display:none;";
                    successCalendarCode = "";
                    errorCalendarShow = "";
                    errorCalendarCode = "Sprints must be between "  + project.getStartDateString() + " - " + project.getEndDateString() + "";
                    return redirect;
                }
            }

            // check if sprint start is before sprint end
            if (!checkStartDate.before(checkEndDate)) {
                successCalendarShow = "display:none;";
                successCalendarCode = "";
                errorCalendarShow = "";
                errorCalendarCode = "A sprint's start date must be before " + sprint.getEndDateString();
                return redirect;
            }

            if (!DateParser.sprintDateCheck(sprints, sprint, checkStartDate, checkEndDate)) {
                successCalendarShow = "display:none;";
                successCalendarCode = "";
                errorCalendarShow = "";
                errorCalendarCode = "A sprint cannot overlap with another sprint";
                return redirect;
            }
            sprint.setStartDate(new Date(sprintStartDate.getTime()));
            sprint.setEndDate(sprintEndDate);
            errorCalendarShow = "display:none;";
            errorCalendarCode = "";
            successCalendarShow = "";
            successCalendarCode = "Sprint time edited to: " + sprint.getStartDateString() + " - " + sprint.getEndDateString() + "";
            repository.save(sprint);
        }

        return redirect;
    }

}
