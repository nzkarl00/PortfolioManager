package nz.ac.canterbury.seng302.portfolio.controller;

import com.google.rpc.context.AttributeContext;
import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import java.util.*;
import java.text.SimpleDateFormat;

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

        Sprint sprint = sprintService.getSprintById(sprintId);

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
     * Send an update sprint message through websockets to all the users on the same project details page
     */
    public void sendSprintCalendarChange(int id) {
        this.template.convertAndSend("/topic/calendar/" + id, new EventUpdate(FetchUpdateType.SPRINT));
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
