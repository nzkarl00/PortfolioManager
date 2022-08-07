package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Deadline;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Event;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.Milestone;
import nz.ac.canterbury.seng302.portfolio.model.timeBoundItems.ProjectTimeBoundItem;
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

/**
 * Controller used to handle editing of projects items, including events, deadlines and milestones.
 */
@Controller
public class EditDatesController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectItemService projectItemService;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private DateSocketService dateSocketService;
    @Autowired
    private NavController navController;

    String errorShow = "display:none;";
    String errorCode = "";
    String redirect = "";
    Integer dateId;

    /**
     * Get request for the edit dates page
     * @param principal
     * @param projectId
     * @param dateId
     * @param model
     * @return Edit date page for the related project item
     * @throws Exception
     */
    @GetMapping("edit-date")
    public String dateEditPage(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer dateId,
            @RequestParam(value = "itemType") String type,
            Model model
    ) throws Exception {
        Integer id = AuthStateInformer.getId(principal);
        String role = AuthStateInformer.getRole(principal);
        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        navController.updateModelForNav(principal, model, userReply, id);
        if (role.equals("teacher") || role.equals("admin")) {
            this.dateId = dateId;
            ProjectTimeBoundItem projectItem = projectItemService.getProjectItemByIdAndType(type, dateId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("dateName", projectItem.getName());
            model.addAttribute("dateStart", projectItem.getStartDate());
            model.addAttribute("dateEnd", projectItem.getEndDate());
            model.addAttribute("dateDesc", projectItem.getDescription());
            model.addAttribute("roleName", "teacher");
            model.addAttribute("type", type);
            model.addAttribute("date", projectItem);
            model.addAttribute("project", project);
            model.addAttribute("errorShow",errorShow);
            model.addAttribute("errorCode",errorCode);
            errorShow ="display:none;";
            errorCode ="";
            return "editDates";
        } else {
            model.addAttribute("roleName", "student");
            return "error";
        }
    }

    /**
     * Method to save edits made to dates to the database, CURRENTLY ONLY WORKS FOR DEADLINES.
     * @param principal auth token
     * @param projectId unique id for the project which the project item belongs to
     * @param dateId unique id for the project item
     * @param dateName project item name
     * @param dateType project item type, should be one of Deadline, Event, Milestone
     * @param dateStartDate start date for the project item
     * @param dateEndDate end date for the project item, in the case of a deadline this will be a time instead
     * @param dateDescription description of the project item
     * @param model
     * @return Project details page for the project which the date belongs to.
     * @throws Exception
     */
    @PostMapping("/edit-date")
    public String dateEditSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer dateId,
            @RequestParam(value = "eventName") String dateName,
            @RequestParam(value = "eventType") String dateType,
            @RequestParam(value = "eventStartDate") String dateStartDate,
            @RequestParam(value = "eventEndDate") String dateEndDate,
            @RequestParam(value = "eventDescription") String dateDescription,
            Model model
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);
        redirect = "redirect:edit-date?id=" + projectId + "&ids=" + dateId;
        if (role.equals("teacher") || role.equals("admin")) {
           Project project = projectService.getProjectById(projectId);
            if (dateName.isBlank()) {
                errorCode = "Project date requires a name";
                return redirect;
            }
            errorShow = "";
            switch (dateType) {
                case "Deadline":
                    return editDeadline(project, dateId, dateName, dateDescription, dateStartDate, dateEndDate);
                case "Milestone":
                    return editMilestone(project, dateId, dateName, dateDescription, dateStartDate);
                case "Event":
                    return editEvent(project, dateId, dateName, dateDescription, dateStartDate, dateEndDate);
            }
        }
        return "redirect:details?id=" + projectId;
    }

    /**
     * Saves edits to a deadline in the repository and posts the changes over the web socket
     * @param project project in which a deadline is being added
     * @param id id of deadline
     * @param name name of deadline
     * @param startDate the date of the deadline
     * @param endDate the time of the deadline
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String editDeadline(Project project, Integer id, String name, String description, String startDate, String endDate) throws Exception {
        LocalDateTime dateStart = DateParser.stringToLocalDateTime(startDate, endDate);
        Deadline deadline = projectItemService.getDeadlineById(id);
        if (dateStart.isAfter(DateParser.convertToLocalDateTime(project.getEndDate())) || dateStart.isBefore(DateParser.convertToLocalDateTime(project.getStartDate()))) {
            errorCode = "Deadline is outside of the project's timeline";
            return redirect;
        }
        errorShow = "display:none;";
        deadline.setName(name);
        deadline.setDescription(description);
        deadline.setStartDate(dateStart);
        projectItemService.saveDeadlineEdit(deadline);
        dateSocketService.sendDeadlineCalendarChange(project, deadline);
        return "redirect:details?id=" + project.getId();
    }

    /**
     * Saves edits to an event in the repository and posts the changes over the web socket
     * @param project project in which a event is being added
     * @param id id of event
     * @param name name of event
     * @param startDate the date of the event
     * @param endDate the time of the event
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String editEvent(Project project, Integer id, String name, String description, String startDate, String endDate) throws Exception {
        LocalDateTime dateStart = DateParser.stringToLocalDateTime(startDate.split("T")[0], startDate.split("T")[1]);
        LocalDateTime dateEnd = DateParser.stringToLocalDateTime(endDate.split("T")[0], startDate.split("T")[1]);
        Event event = projectItemService.getEventById(id);
        if (dateStart.isAfter(DateParser.convertToLocalDateTime(project.getEndDate()))
                || dateStart.isBefore(DateParser.convertToLocalDateTime(project.getStartDate()))
                || dateEnd.isBefore(DateParser.convertToLocalDateTime(project.getStartDate()))) {
            errorCode = "Event is outside of the project's timeline";
            return "redirect:edit-date?projectId=" + project.getId() + "&dateId=" + dateId + "&itemType=Event";
        }

        if (dateEnd.isBefore(dateStart)) {
            errorCode = "End date for event must occur after start date";
            return "redirect:edit-date?projectId=" + project.getId() + "&dateId=" + dateId + "&itemType=Event";
        }
        errorShow = "display:none;";
        event.setName(name);
        event.setDescription(description);
        event.setStartDate(dateStart);
        event.setEndDate(dateEnd);
        projectItemService.saveEventEdit(event);
        dateSocketService.sendEventCalendarChange(project, event);
        return "redirect:details?id=" + project.getId();
    }

    /**
     * Saves edits to a milestone in the repository and posts the changes over the web socket
     * @param project project in which a deadline is being added
     * @param id milestone id
     * @param name name of milestone
     * @param startDate milestone date
     * @param description milestone description
     * @return project details page on successful update, return string to indicate success or failure
     */
    private String editMilestone(Project project, Integer id, String name, String description, String startDate) throws Exception {
        LocalDateTime dateStart = DateParser.stringToLocalDateTime(startDate, "");
        Milestone milestone = projectItemService.getMilestoneById(id);
        if (dateStart.isAfter(DateParser.convertToLocalDateTime(project.getEndDate())) || dateStart.isBefore(DateParser.convertToLocalDateTime(project.getStartDate()))) {
            errorCode = "Milestone is outside of the project's timeline";
            return redirect;
        }
        errorShow = "display:none;";
        milestone.setName(name);
        milestone.setDescription(description);
        milestone.setStartDate(dateStart);
        projectItemService.saveMilestoneEdit(milestone);
        dateSocketService.sendMilestoneCalendarChange(project, milestone);
        return "redirect:details?id=" + project.getId();
    }
}
