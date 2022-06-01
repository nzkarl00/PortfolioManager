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
import java.util.Date;
import java.util.List;

@Controller
public class AddDatesController {

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
            @RequestParam(value = "eventStartDate") String eventStartDate,
            @RequestParam(value = "eventEndDate") String eventEndDate,
            @RequestParam(value = "eventDescription") String eventDescription
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);
        List<Sprint> sprints = sprintService.getSprintByParentId(projectId);
        Project project = projectService.getProjectById(projectId);
        Date projStart = project.getStartDate();
        Date projEnd = project.getEndDate();
        Date newStart = DateParser.stringToDate(eventStartDate);
        Date newEnd = DateParser.stringToDate(eventEndDate);
        if (eventName == "") {
            eventName = "Sprint " + (sprints.size() + 1);
        }
        if (!sprintService.areNewSprintDatesValid(newStart, newEnd, projectId) || newStart.before(projStart) || newEnd.after(projEnd)) {
            errorShow="";
            errorCode="Invalid dates";
            return "redirect:addDates?projectId=" + projectId;
        }
        if (role.equals("teacher") || role.equals("admin")) {
            Sprint sprint = new Sprint(projectId, eventName, eventName, eventDescription, newStart, newEnd);
            repository.save(sprint);
            sendSprintCalendarChange(projectId);
            return "redirect:details?id=" + projectId;
        }
        return "redirect:details?id=" + projectId;
    }

    /**
     * Send an update sprint message through websockets to all the users on the same project details page
     */
    public void sendSprintCalendarChange(int id) {
        this.template.convertAndSend("/topic/calendar/" + id, new EventUpdate(FetchUpdateType.SPRINT));
    }
}
