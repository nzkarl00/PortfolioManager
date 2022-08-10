package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private DeadlineRepository deadlineRepo;
    @Autowired
    private MilestoneRepository milestoneRepo;
    @Autowired
    private EventRepository eventRepo;
    @Autowired
    private DateSocketService dateSocketService;
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

    @Value("${portfolio.base-url}")
    private String baseUrl;

    String errorShow = "display:none;";
    String errorCode = "";
    String successCalendarShow = "display:none;";
    String successCalendarCode = "";
    String errorCalendarShow = "display:none;";
    String errorCalendarCode = "";

    Logger logger = LoggerFactory.getLogger(DetailsController.class);

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
        logger.info(String.format("Fetching details for project=<%s>", projectId));
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        Integer id = AuthStateInformer.getId(principal);

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);

        navController.updateModelForNav(principal, model, userReply, id);

        // setup the default lists of timeBoundItems
        List<Sprint> sprintList = repository.getSprintByParentProjectIdOrderBySprintStartDateAsc(projectId);
        model.addAttribute("sprints", sprintList);
        List<Event> eventList = eventRepo.findAllByParentProjectOrderByStartDateAsc(project);
        model.addAttribute("events", eventList);
        List<Milestone> milestoneList = milestoneRepo.findAllByParentProjectOrderByStartDateAsc(project);
        model.addAttribute("milestones", milestoneList);
        List<Deadline> deadlineList = deadlineRepo.findAllByParentProjectOrderByStartDateAsc(project);
        model.addAttribute("deadlines", deadlineList);


        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("successCalendarShow", successCalendarShow);
        model.addAttribute("successCalendarCode", successCalendarCode);
        model.addAttribute("errorCalendarShow", errorCalendarShow);
        model.addAttribute("errorCalendarCode", errorCalendarCode);
        model.addAttribute("baseUrl", baseUrl);

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
        // if you are a teacher or an admin you can add a new group
        if (role.equals("teacher") || role.equals("admin")) {
            model.addAttribute("display", "");
            model.addAttribute("role", role);
        } else {
            model.addAttribute("display", "display:none;");
            model.addAttribute("role", role);
        }
        return "projectDetails";
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
    ) throws CustomExceptions.ProjectItemNotFoundException {
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

            dateSocketService.sendSprintCalendarChange(projectId);
        }

        return "redirect:details?id=" + projectId;
    }

    /**
     * The mapping to delete an event
     *
     * @param principal auth token
     * @param projectId id param for project to delete event from
     * @param eventId eventId id under project to delete
     * @param model the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("delete-event")
    public String eventDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer eventId,
            Model model
    ) throws CustomExceptions.ProjectItemNotFoundException {
        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            eventRepo.deleteById(eventId);
            dateSocketService.sendEventCalendarChange(projectService.getProjectById(projectId));
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
     */
    @PostMapping("delete-deadline")
    public String deadlineDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer deadlineId,
            Model model
    ) throws CustomExceptions.ProjectItemNotFoundException {
        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            deadlineRepo.deleteById(deadlineId);
            dateSocketService.sendDeadlineCalendarChange(projectService.getProjectById(projectId));
        }

        return "redirect:details?id=" + projectId;
    }
    /**
     * The mapping to delete a milestone
     *
     * @param principal auth token
     * @param projectId id param for project to delete sprint from
     * @param milestoneId  milestone id under project to delete
     * @param model     the model to add attributes to
     * @return A location of where to go next
     * @throws Exception
     */
    @PostMapping("delete-milestone")
    public String deadlineMilestone(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer milestoneId,
            Model model
    ) throws CustomExceptions.ProjectItemNotFoundException {
        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher") || role.equals("admin")) {
            milestoneRepo.deleteById(milestoneId);
            dateSocketService.sendMilestoneCalendarChange(projectService.getProjectById(projectId));
        }

        return "redirect:details?id=" + projectId;
    }

    @PostMapping("/details")
    public String sprintSaveFromCalendar(@AuthenticationPrincipal AuthState principal,
                                         @RequestParam(value="id") Integer projectId,
                                         @RequestParam(value="sprintId") Integer sprintId,
                                         @RequestParam(value="start") Date sprintStartDate,
                                         @RequestParam(value="end") Date sprintEndDate) throws CustomExceptions.ProjectItemNotFoundException {

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
            dateSocketService.sendSprintCalendarChange(projectId);
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
        return ResponseEntity.ok(repository.findByParentProjectIdOrderBySprintStartDate(projectId));
    }

    /**
     * Sends all the deadlines in JSON for a given project
     * @param principal authstate to validate the user
     * @param projectId the id of the project to
     * @return the list of deadlines in JSON
     */
    @GetMapping("/deadlines")
    public ResponseEntity<List<Deadline>> getProjectDeadlines(@AuthenticationPrincipal AuthState principal,
                                                              @RequestParam(value="id") Integer projectId) throws Exception {
        List<Deadline> deadlines = deadlineRepo.findAllByParentProjectOrderByStartDateAsc(projectService.getProjectById(projectId));

        return ResponseEntity.ok(deadlines);
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * Sends all the milestones in JSON for a given project
     * @param principal authstate to validate the user
     * @param projectId the id of the project to
     * @return the list of milestones in JSON
     */
    @GetMapping("/milestones")
    public ResponseEntity<List<Milestone>> getProjectMilestones(@AuthenticationPrincipal AuthState principal,
                                                                @RequestParam(value="id") Integer projectId) throws Exception {
        List<Milestone> milestones = milestoneRepo.findAllByParentProjectOrderByStartDateAsc(projectService.getProjectById(projectId));

        return ResponseEntity.ok(milestones);
    }


    /**
     * Sends all the events in JSON for a given project
     * @param principal authstate to validate the user
     * @param projectId the id of the project to
     * @return the list of events in JSON
     */
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getProjectEvents(@AuthenticationPrincipal AuthState principal,
                                                        @RequestParam(value="id") Integer projectId) throws Exception {
        List<Event> events = eventRepo.findAllByParentProjectOrderByStartDateAsc(projectService.getProjectById(projectId));

        return ResponseEntity.ok(events);
    }
}
