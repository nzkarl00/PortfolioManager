package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class AddDatesController {

    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private SprintRepository repository;
    @Autowired
    private DeadlineRepository deadlineRepository;
    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;
    @Autowired
    private EventRepository eventRepository;

    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Gets the page for adding dates to a project and passes date boundaries to the model.
     * @param principal auth token
     * @param projectId id for the project dates will be added to
     * @param model webpage model to hold data
     * @return the date adding page
     * @throws Exception
     */
    @GetMapping("/add-dates")
    public String addDates(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            Model model
    ) throws Exception {
        Project project = projectService.getProjectById(projectId);
        Integer id = AuthStateInformer.getId(principal);
        // Attributes For header
        UserResponse userReply;
        userReply =accountClientService.getUserById(id);
        // Attributes for webpage
        navController.updateModelForNav(principal,model,userReply,id);
        model.addAttribute("projectStart",project.getStartDateStringHtml());
        model.addAttribute("projectEnd",project.getEndDateStringHtml());
        model.addAttribute("project",project);
        model.addAttribute("errorShow",errorShow);
        model.addAttribute("errorCode",errorCode);
        // Reset for the next display of the page
        errorShow ="display:none;";
        errorCode ="";
        String role = AuthStateInformer.getRole(principal);
        if(role.equals("teacher")||role.equals("admin")) {
            return "addDates";
        } else {
            List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);
            model.addAttribute("sprints", sprintList);
            return "userProjectDetails";
        }
    }

    /**
     * Saves a new date supplied by the user and redirects to the project page afterwards
     * @param principal auth token
     * @param projectId project that had a date added to it
     * @param eventName name of event
     * @param eventStartDate event start date
     * @param eventEndDate event end date
     * @param eventDescription event description
     * @return project details page on successful update, redirect to add dates again on failure
     * @throws Exception
     */
    @PostMapping("/add-dates")
    public String newSprint(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "eventName") String eventName,
            @RequestParam(value = "eventType") Optional <String> eventType,
            @RequestParam(value = "eventStartDate") String eventStartDate,
            @RequestParam(value = "eventEndDate") String eventEndDate,
            @RequestParam(value = "eventDescription") String eventDescription
    ) throws Exception {



        String role = AuthStateInformer.getRole(principal);
        Project project = projectService.getProjectById(projectId);
        String messageReturned = "redirect:details?id=";

        String event = eventType.orElse("");

        if (role.equals("teacher") || role.equals("admin")) {
            if (event.equals("Deadline")) {
                messageReturned = addDeadlines(project, eventName, eventDescription, eventStartDate, eventEndDate);
            } else if (event.equals("Sprint") || event.equals("")){
                messageReturned = addSprint(project, eventName, eventDescription, eventStartDate, eventEndDate);
            } else if (event.equals("Milestone")) {
                messageReturned = addMilestone(project, eventName, eventDescription, eventStartDate);
            } else if (event.equals("Event")) {
                messageReturned = addEvent(project, eventName, eventDescription, eventStartDate, eventEndDate);
            }
        }

        return messageReturned + projectId;
    }

    /**
     * Saves a new date supplied by the user and redirects to the project page afterwards
     * @param project project in which a deadline is being added
     * @param eventName name of event
     * @param eventStartDate event start date
     * @param eventEndDate event end date
     * @param eventDescription event description
     * @return project details page on successful update, return string to indicate success or failure
     * @throws Exception
     */
    private String addSprint(Project project, String eventName, String eventDescription, String eventStartDate, String eventEndDate) throws Exception {
        Date projStart = project.getStartDate();
        Date projEnd = project.getEndDate();
        Integer projectId = project.getId();
        List<Sprint> sprints = sprintService.getSprintByParentId(projectId);
        Date newStart = DateParser.stringToDate(eventStartDate);
        Date newEnd = DateParser.stringToDate(eventEndDate);

        if (eventName == "") {
            eventName = "Sprint " + (sprints.size() + 1);
        }
        if (!sprintService.areNewSprintDatesValid(newStart, newEnd, projectId) || newStart.before(projStart) || newEnd.after(projEnd)) {
            errorShow="";
            errorCode="Invalid dates";
            return "redirect:add-dates?projectId=";
        }
        
        Sprint sprint = new Sprint(projectId, eventName, eventName, eventDescription, newStart, newEnd);
        repository.save(sprint);
        sendSprintCalendarChange(projectId);
        return "redirect:details?id=";
    }

    /**
     * Saves a new date supplied by the user and redirects to the project page afterwards
     * @param project project in which a deadline is being added
     * @param eventName name of event
     * @param eventStartDate event start date
     * @param eventEndDate event end date
     * @param eventDescription event description
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String addDeadlines(Project project, String eventName, String eventDescription, String eventStartDate, String eventEndDate){
        Date projStart = DateParser.stringToDate(project.getStartDateString());
        Date projEnd = DateParser.stringToDate(project.getEndDateString());
        LocalDateTime endDate = DateParser.stringToLocalDateTime(eventStartDate, eventEndDate);
        Date checkForValidationDate = DateParser.stringToDate(eventStartDate);
        
        if (eventName.isBlank()) {
            List<Deadline> deadlines = deadlineRepository.findAllByParentProject(project);
            eventName = "Deadline " + (deadlines.size() + 1);
        }

        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Deadline date must be within the project dates";
            return "redirect:add-dates?projectId=";
        }

        Deadline deadline = new Deadline(project, eventName, eventDescription, endDate);
        deadlineRepository.save(deadline);
        sendDeadlineCalendarChange(project, deadline);
        return "redirect:details?id=";
    }

    /**
     * Saves a new date supplied by the user and redirects to the project page afterwards
     * @param project project in which a deadline is being added
     * @param eventName name of event
     * @param eventStartDate event start date
     * @param eventDescription event description
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String addMilestone(Project project, String eventName, String eventDescription, String eventStartDate){
        Date projStart = DateParser.stringToDate(project.getStartDateString());
        Date projEnd = DateParser.stringToDate(project.getEndDateString());
        LocalDateTime endDate = DateParser.stringToLocalDateTime(eventStartDate, "");
        Date checkForValidationDate = DateParser.stringToDate(eventStartDate);

        if (eventName.isBlank()) {
            List<Milestone> milestones = milestoneRepository.findAllByParentProject(project);
            eventName = "Milestone " + (milestones.size() + 1);
        }

        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Milestone date must be within the project dates";
            return "redirect:add-dates?projectId=";
        }

        Milestone milestone = new Milestone(project, eventName, eventDescription, endDate);
        milestoneRepository.save(milestone);
        sendMilestoneCalendarChange(project, milestone);
        return "redirect:details?id=";
    }

    /**
     * Saves new event supplied by the user and redirects to the project page afterwards
     * @param project project in which a event is being added
     * @param eventName name of event
     * @param eventStartDate event start date
     * @param eventEndDate event end date
     * @param eventDescription event description
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String addEvent(Project project, String eventName, String eventDescription, String eventStartDate, String eventEndDate){
        Date projStart = DateParser.stringToDate(project.getStartDateString());
        Date projEnd = DateParser.stringToDate(project.getEndDateString());
        LocalDateTime startDate = DateParser.stringToLocalDateTime(eventStartDate, "");
        LocalDateTime endDate = DateParser.stringToLocalDateTime(eventEndDate, "");
        Date checkForValidationDate = DateParser.stringToDate(eventStartDate);

        if (eventName.isBlank()) {
            List<Event> events = eventRepository.findAllByParentProject(project);
            eventName = "Event " + (events.size() + 1);
        }

        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Event dates must be within the project dates";
            return "redirect:add-dates?projectId=";
        }

        Event event = new Event(project, eventName, eventDescription, startDate, endDate);
        eventRepository.save(event);
        sendEventCalendarChange(project, event);
        return "redirect:details?id=";
    }

    /**
     * Send an update sprint message through websockets to all the users on the same project details page
     */
    public void sendSprintCalendarChange(int id) {
        this.template.convertAndSend("/topic/calendar/" + id, new EventUpdate(FetchUpdateType.SPRINT));
    }

    /**
     * Send an update event message through websockets to all the users on the same project details page
     */
    public void sendEventCalendarChange(Project project, Event event) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (event.getEndDate().isAfter(startDate) && event.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                    , new EventUpdate(FetchUpdateType.EVENT, sprint.getId()));
            }
        }
    }

    /**
     * Send an update deadline message through websockets to all the users on the same project details page
     */
    public void sendDeadlineCalendarChange(Project project, Deadline deadline) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (deadline.getEndDate().isAfter(startDate) && deadline.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                        , new EventUpdate(FetchUpdateType.DEADLINE, sprint.getId()));
            }
        }
    }

    /**
     * Send an update milestone message through websockets to all the users on the same project details page
     */
    public void sendMilestoneCalendarChange(Project project, Milestone milestone) {
        List<Sprint> sprints = repository.findByParentProjectId(project.getId());
        // loop through sprints
        for (Sprint sprint: sprints) {
            LocalDateTime startDate = DateParser.convertToLocalDateTime(sprint.getStartDate());
            LocalDateTime endDate = DateParser.convertToLocalDateTime(sprint.getEndDate());
            // if deadline is within sprint
            if (milestone.getEndDate().isAfter(startDate) && milestone.getStartDate().isBefore(endDate)) {
                /// send a deadline update
                this.template.convertAndSend("/topic/calendar/" + project.getId()
                        , new EventUpdate(FetchUpdateType.MILESTONE, sprint.getId()));
            }
        }
    }

}
