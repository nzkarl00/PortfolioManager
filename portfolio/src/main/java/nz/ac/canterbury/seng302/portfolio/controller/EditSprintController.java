package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.model.SprintRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Controller for the edit sprint details page
 */
@Controller
public class EditSprintController {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private SprintRepository repository;
    @Autowired
    private AccountClientService accountClientService;


    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Redirects to the edit sprint html page
     * @param model The model to be used by the application for web integration
     * @return
     */
    @GetMapping("/edit-sprint")
    public String sprintForm(@AuthenticationPrincipal AuthState principal, @RequestParam(value="id") Integer projectId, @RequestParam(value="ids") Integer sprintId, Model model) throws Exception {
        /* Add sprint details to the model */

        Project project = projectService.getProjectById(projectId);

        List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);

        Sprint sprint = sprintService.getSprintById(sprintId);

        Integer id = Integer.valueOf(principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("nameid"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100"));

        String username = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("name"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("-100");

        // Attributes For header
        UserResponse userReply;
        userReply = accountClientService.getUserById(id);
        Long seconds = userReply.getCreated().getSeconds();
        Date date = new Date(seconds * 1000); // turn into millis
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd LLLL yyyy" );
        String stringDate = " " + dateFormat.format( date );
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        Calendar currentCalendar = Calendar.getInstance();
        cal.setTime(new Date());
        int currentMonth = currentCalendar.get(Calendar.MONTH);
        int currentYear = currentCalendar.get(Calendar.YEAR);

        int totalMonth = (currentMonth - month) + 12 * (currentYear - year);
        if (totalMonth > 0){
            if (totalMonth > 1) {
                stringDate += " (" + totalMonth + " Months)";
            } else {
                stringDate += " (" + totalMonth + " Month)";
            }
        }

        model.addAttribute("date",  stringDate);
        model.addAttribute("username", userReply.getUsername());

        model.addAttribute("sprint", sprint);
        model.addAttribute("project", project);
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        String role = AuthStateInformer.getRole(principal);

        if (role.equals("teacher")) {
            return "editSprint";
        } else {
            return "userProjectDetails";
        }
    }

    /**
     * Updates the given sprint with form data
     * @param principal
     * @param sprintName Name of the sprint (string)
     * @param sprintStartDate Start Date of the sprint (string)
     * @param sprintEndDate End Date of the sprint (string)
     * @param sprintDescription Description of the sprint (string)
     * @param model The model to be used by the application for web integration
     * @return redirects to the edit sprint page
     */
    @PostMapping("/edit-sprint")
    public String sprintSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectId") Integer projectId,
            @RequestParam(value="sprintId") Integer sprintId,
            @RequestParam(value="sprintName") String sprintName,
            @RequestParam(value="sprintStartDate") String sprintStartDate,
            @RequestParam(value="sprintEndDate") String sprintEndDate,
            @RequestParam(value="sprintDescription") String sprintDescription,
            Model model
    ) throws Exception {


        Sprint sprint = sprintService.getSprintById(sprintId);
        Project project = projectService.getProjectById(projectId);

        Date projStartDate = project.getStartDate();
        String stringStartDate = project.getStartDateString();
        String stringEndDate = project.getEndDateString();
        Date projEndDate = project.getEndDate();
        Date checkStartDate = Project.stringToDate(sprintStartDate);
        Date checkEndDate = Project.stringToDate(sprintEndDate);

        if (sprintName.isBlank()) {
            errorShow = "";
            errorCode = "Sprint requires a name";
            return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;
        }

        sprint.setName(sprintName);
        sprint.setDescription(sprintDescription);

        if ((projStartDate.before(checkStartDate) | (Objects.equals(stringStartDate, sprintStartDate))) & (projEndDate.after(checkEndDate) | (Objects.equals(stringEndDate, sprintEndDate)))) {

            if (checkStartDate.before(checkEndDate)) {
                for (Sprint temp: sprintService.getSprintByParentId(projectId)) {
                    if (temp.getEndDate().after(checkStartDate) & temp.getStartDate().before(checkStartDate)) {

                        errorShow = "";
                        errorCode = "Start date overlaps with another sprint";
                        return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;

                    }

                }
                sprint.setStartDateStringSprint(sprintStartDate);
            } else {

                errorShow = "";
                errorCode = "Start and End date overlap";
                return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;

            }
            if (checkEndDate.after(checkStartDate)) {
                for (Sprint temp: sprintService.getSprintByParentId(projectId)) {

                    if (temp.getEndDate().after(checkStartDate) & temp.getStartDate().before(checkStartDate)) {

                        errorShow = "";
                        errorCode = "End date overlaps with another sprint";
                        return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;

                    }

                }
                sprint.setEndDateStringSprint(sprintEndDate);
            } else {

                errorShow = "";
                errorCode = "Start and End date overlap";
                return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;
            }
        } else {

            errorShow = "";
            errorCode = "Sprint is outside of the Project's timeline";
            return "redirect:/edit-sprint?id=" + projectId + "&ids=" + sprintId;

        }


        repository.save(sprint);

        return "redirect:/details?id=" + projectId;
    }
}
