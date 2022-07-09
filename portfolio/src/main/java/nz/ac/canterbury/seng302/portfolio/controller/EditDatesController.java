package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller used to handle editing of projects items, including events, deadlines and milestones.
 */
@Controller
public class EditDatesController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private DeadlineService deadlineService;
    @Autowired
    private DeadlineRepository deadlineRepo;
    @Autowired
    private AccountClientService accountClientService;
    @Autowired
    private NavController navController;


    String errorShow = "display:none;";
    String errorCode = "";

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
    public String deadlineEdit(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer dateId,
            Model model
    ) throws Exception {
        Integer id = AuthStateInformer.getId(principal);
        String role = AuthStateInformer.getRole(principal);
        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        navController.updateModelForNav(principal, model, userReply, id);
        if (role.equals("teacher") || role.equals("admin")) {
            Deadline deadline = deadlineService.getDeadlineById(dateId);
            Project project = projectService.getProjectById(projectId);
            model.addAttribute("dateName", deadline.getName());
            model.addAttribute("dateStart", deadline.getStartDate());
            model.addAttribute("dateEnd", deadline.getEndDate());
            model.addAttribute("dateDesc", deadline.getDescription());
            model.addAttribute("roleName", "teacher");
            model.addAttribute("date", deadline);
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
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value = "projectId") Integer projectId,
            @RequestParam(value = "dateId") Integer dateId,
            @RequestParam(value = "eventName") String dateName,
            @RequestParam(value = "eventType") Optional<String> dateType,
            @RequestParam(value = "eventStartDate") String dateStartDate,
            @RequestParam(value = "eventEndDate") String dateEndDate,
            @RequestParam(value = "eventDescription") String dateDescription,
            Model model
    ) throws Exception {
        String role = AuthStateInformer.getRole(principal);
        if (role.equals("teacher") || role.equals("admin")) {
            Deadline date = deadlineService.getDeadlineById(dateId);
            Project project = projectService.getProjectById(projectId);
            LocalDateTime dateStart = DateParser.stringToLocalDateTime(dateStartDate, dateEndDate);
            errorShow = "";
            String redirect = "redirect:edit-date?id=" + projectId + "&ids=" + dateId;
            // Validates new name
            if (dateName.isBlank()) {
                errorCode = "Deadline requires a name";
                System.out.println("name");
                return redirect;
            }
            date.setName(dateName);
            date.setDescription(dateDescription);
            // Validates new date
            if (dateStart.isAfter(DateParser.convertToLocalDateTime(project.getEndDate())) || dateStart.isBefore(DateParser.convertToLocalDateTime(project.getStartDate()))) {
                errorCode = "Deadline is outside of the project's timeline";
                return redirect;
            }
            // Updates the deadline in the database
            errorShow = "display:none;";
            date.setStartDate(dateStart);
            date.setDescription(dateDescription);
            date.setName(dateName);
            deadlineRepo.save(date);
        }
        return "redirect:details?id=" + projectId;
    }

}
