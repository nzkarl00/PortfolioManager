package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller used to handle editing of projects dates, including events, deadlines and milestones.
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

    @PostMapping("/edit-deadline")
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
            Date dateStart = DateParser.stringToDate(dateStartDate);
            errorShow = "";
            String redirect = "redirect:edit-sprint?id=" + projectId + "&ids=" + dateId;
            // Validates new name
            if (dateName.isBlank()) {
                errorCode = "Deadline requires a name";
                return redirect;
            }
            date.setName(dateName);
            date.setDescription(dateDescription);
            // Validates new date
            if (dateStart.after(project.getEndDate()) || dateStart.before(project.getStartDate())) {
                errorCode = "Deadline is outside of the project's timeline";
                return redirect;
            }
            // Updates the deadline in the database
            errorShow = "display:none;";
            date.setStartDate(dateStart);
            sprint.setEndDate(checkEndDate);
            repository.save(sprint);
        }
        return "redirect:details?id=" + projectId;
    }

}
