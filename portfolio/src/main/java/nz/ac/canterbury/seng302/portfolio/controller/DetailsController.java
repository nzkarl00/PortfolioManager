package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private DeadlineRepository deadlineRepo;
    @Autowired
    private SimpMessagingTemplate template;
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
    @Autowired
    private DeadlineService deadlineService;


    String errorShow = "display:none;";
    String errorCode = "";
    String successCalendarShow = "display:none;";
    String successCalendarCode = "";
    String errorCalendarShow = "display:none;";
    String errorCalendarCode = "";
    
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

        if (role.equals("teacher") || role.equals("admin")) {
            repository.deleteById(sprintId);
            Integer i = 1;

            List<Sprint> sprintLists = sprintService.getSprintByParentId(projectId);
            for (Sprint temp : sprintLists) {

                temp.setLabel("Sprint " + i);
                repository.save(temp);
                i += 1;

            }

            sendSprintCalendarChange(projectId);
        }

        return "redirect:details?id=" + projectId;
    }

    /**
     * The mapping to delete a deadline
     *
     * @param principal auth token
     * @param projectId id param for project to delete sprint from
     * @param deadlineId  deadline id under project to delete
     * @param model     the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("delete-deadline")
    public String deadlineDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "deadlineId") Integer deadlineId,
            Model model
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            deadlineRepo.deleteById(deadlineId);
            sendDeadlineCalendarChange(projectService.getProjectById(projectId));
        }

        return "redirect:details?id=" + projectId;
    }

    /**
     * Deadline deletion put request
     * @param principal
     * @param projectId
     * @param deadlineId
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping("edit-deadline")
    public String deadlineEdit(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "deadlineId") Integer deadlineId,
            Model model
    ) throws Exception {
        Integer id = AuthStateInformer.getId(principal);
        String role = AuthStateInformer.getRole(principal);
        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);
        if (role.equals("teacher") || role.equals("admin")) {
            Deadline deadline = deadlineService.getDeadlineById(deadlineId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("dateName", deadline.getName());
            model.addAttribute("dateStart", deadline.getStartDate());
            model.addAttribute("dateEnd", deadline.getEndDate());
            model.addAttribute("dateDesc", deadline.getDescription());
            model.addAttribute("roleName", "teacher");
            model.addAttribute("date", deadline);
            model.addAttribute("project", project);
            return "editDates";
        } else {
            model.addAttribute("roleName", "student");
            return "error";
        }
    }

    /**
     * Send an update sprint message through websockets to all the users on the same project details page
     */
    public void sendSprintCalendarChange(int id) {
        this.template.convertAndSend("/topic/calendar/" + id, new EventUpdate(FetchUpdateType.SPRINT));
    }

    /**
     * Send an update deadline message through websockets to all the users on the same project details page
     */
    public void sendDeadlineCalendarChange(Project project) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());

        for (Sprint sprint: sprints) {
            this.template.convertAndSend("/topic/calendar/" + project.getId()
                    , new EventUpdate(FetchUpdateType.DEADLINE, sprint.getId()));
        }
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
            sendSprintCalendarChange(projectId);
        }
        return redirect;
    }

    /**
     * Sends all the sprints in JSON for a given project
     * @param principal authstate to validate the user
     * @param projectId the id of the project to
     * @return the list of sprints in JSON
     */
    @GetMapping("/sprints")
    public ResponseEntity<List<Sprint>> getProjectSprints(@AuthenticationPrincipal AuthState principal,
                                            @RequestParam(value="id") Integer projectId) {
        return ResponseEntity.ok(repository.findByParentProjectId(projectId));
    }

    /**
     * Sends all the deadlines in JSON for a given project
     * @param principal authstate to validate the user
     * @param projectId the id of the project to
     * @return the list of deadlines in JSON
     */
    @GetMapping("/deadlines")
    public ResponseEntity<List<Deadline>> getProjectDeadlines(@AuthenticationPrincipal AuthState principal,
                                                          @RequestParam(value="id") Integer projectId,
                                                              @RequestParam(value="sprintId") Integer sprintId) throws Exception {
        List<Deadline> deadlines = deadlineRepo.findAllByParentProject(projectService.getProjectById(projectId));
        Optional<Sprint> sprint = repository.findById(sprintId);
        List<Deadline> sendingDeadlines = new ArrayList<>();
        for (Deadline deadline : deadlines) {
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.get().getEndDate());
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.get().getStartDate());
            if (deadline.getEndDate().isBefore(endDate) && deadline.getStartDate().isAfter(startDate)) {
                sendingDeadlines.add(deadline);
            }
        }
        return ResponseEntity.ok(sendingDeadlines);
    }
}
