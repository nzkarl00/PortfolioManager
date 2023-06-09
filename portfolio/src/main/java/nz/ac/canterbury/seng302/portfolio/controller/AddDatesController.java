package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.CustomExceptions;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.*;
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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class AddDatesController {

    @Autowired
    private DateSocketService dateSocketService;
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
    Integer nameLen = 0;
    final String addDatesRedirectUrl = "redirect:add-dates?projectId=";
    final String detailsRedirectUrl = "redirect:details?id=";
    
    /**
     * Gets the page for adding dates to a project and passes date boundaries to the model.
     * @param principal auth token
     * @param projectId id for the project dates will be added to
     * @param model webpage model to hold data
     * @return the date adding page
     */
    @GetMapping("/add-dates")
    public String addDates(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            Model model
    ) throws Exception {
        Project project = projectService.getProjectById(projectId);
        int id = AuthStateInformer.getId(principal);
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
            return "projectDetails";
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
        String messageReturned = detailsRedirectUrl;

        String event = eventType.orElse("");

        if (role.equals("teacher") || role.equals("admin")) {
            switch (event) {
                case "Deadline" -> messageReturned = addDeadlines(project, eventName, eventDescription, eventStartDate, eventEndDate);
                case "Sprint", "" -> messageReturned = addSprint(project, eventName, eventDescription, eventStartDate, eventEndDate);
                case "Milestone" -> messageReturned = addMilestone(project, eventName, eventDescription, eventStartDate);
                case "Event" -> messageReturned = addEvent(project, eventName, eventDescription, eventStartDate, eventEndDate);
                default -> throw new CustomExceptions.ProjectItemTypeException("Project item type does not exist: " + event);
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
     */
    private String addSprint(Project project, String eventName, String eventDescription, String eventStartDate, String eventEndDate) throws Exception {
        Date projStart = project.getStartDate();
        Date projEnd = project.getEndDate();
        int projectId = project.getId();
        List<Sprint> sprints = sprintService.getSprintByParentId(projectId);
        Date newStart = DateParser.stringToDate(eventStartDate);
        Date newEnd = DateParser.stringToDate(eventEndDate);

        if (eventName.equals("")) {
            eventName = "Sprint " + (sprints.size() + 1);
        }
        assert newStart != null;
        if (!sprintService.areNewSprintDatesValid(newStart, newEnd, projectId) || newStart.before(projStart) || Objects.requireNonNull(newEnd).after(projEnd)) {
            errorShow="";
            errorCode="Invalid dates";
            return addDatesRedirectUrl;
        }
        
        Sprint sprint = new Sprint(projectId, eventName, eventName, eventDescription, newStart, newEnd);
        repository.save(sprint);
        dateSocketService.sendSprintCalendarChange(projectId);
        return detailsRedirectUrl;
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
            List<Deadline> deadlines = deadlineRepository.findAllByParentProjectOrderByStartDateAsc(project);
            eventName = "Deadline " + (deadlines.size() + 1);
        }

        assert checkForValidationDate != null;
        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Deadline date must be within the project dates";
            return addDatesRedirectUrl;
        }

        Deadline deadline = new Deadline(project, eventName, eventDescription, endDate);
        deadlineRepository.save(deadline);
        dateSocketService.sendDeadlineCalendarChange(project);
        return detailsRedirectUrl;
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
            List<Milestone> milestones = milestoneRepository.findAllByParentProjectOrderByStartDateAsc(project);
            eventName = "Milestone " + (milestones.size() + 1);
        }

        assert checkForValidationDate != null;
        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Milestone date must be within the project dates";
            return addDatesRedirectUrl;
        }

        Milestone milestone = new Milestone(project, eventName, eventDescription, endDate);
        milestoneRepository.save(milestone);
        dateSocketService.sendMilestoneCalendarChange(project);
        return detailsRedirectUrl;
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
        LocalDateTime startDate = DateParser.stringToLocalDateTime(eventStartDate.split("T")[0], eventStartDate.split("T")[1]);
        LocalDateTime endDate = DateParser.stringToLocalDateTime(eventEndDate.split("T")[0], eventEndDate.split("T")[1]);

        Date checkForValidationDate = DateParser.stringToDate(eventEndDate.split("T")[0]);

        if (eventName.isBlank()) {
            List<Event> events = eventRepository.findAllByParentProjectOrderByStartDateAsc(project);
            eventName = "Event " + (events.size() + 1);
        }

        assert checkForValidationDate != null;
        if (checkForValidationDate.before(projStart) || checkForValidationDate.after(projEnd)){
            errorShow="";
            errorCode="Event dates must be within the project dates";
            return addDatesRedirectUrl;
        }
        if (!startDate.isBefore(endDate)) {
            errorShow="";
            errorCode="Events must start before they end";
            return addDatesRedirectUrl;
        }

        Event event = new Event(project, eventName, eventDescription, startDate, endDate);
        eventRepository.save(event);
        dateSocketService.sendEventCalendarChange(project);
        return detailsRedirectUrl;
    }



}
