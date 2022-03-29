package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.web.bind.annotation.PostMapping;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Controller for the edit project details page
 */
@Controller
public class EditProjectController {


    @Autowired
    private ProjectRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private AccountClientService accountClientService;


    String errorShow = "display:none;";
    String errorCode = "";
    /**
     * Redirects top the edit project page
     * @param model The model to be used by the application for web integration
     * @return the edit project page
     */
    @GetMapping("/edit-project")
    public String projectForm(@AuthenticationPrincipal AuthState principal , @RequestParam(value="id") Integer projectId, Model model)  throws Exception  {

        Project project = projectService.getProjectById(projectId);
        /* Add project details to the model */
        model.addAttribute("projectName", project.getName());
        model.addAttribute("project", project);
        model.addAttribute("projectStartDate", project.getStartDateString());
        model.addAttribute("projectEndDate", project.getEndDateString());
        model.addAttribute("projectDescription", project.getDescription());
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);


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
        // End of Attributes for header

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        if (role.equals("teacher")) {
            return "editProject";
        } else {
            return "userProjectDetails";
        }
    }

    /**
     * Updates the project with the form data
     * @param principal
     * @param projectName Name of the project
     * @param projectStartDate Start date of the project (string)
     * @param projectEndDate End date of the project (string)
     * @param projectDescription Description of the project (string)
     * @param model The model to be used by the application for web integration
     * @return
     */
    @PostMapping("/edit-project")
    public String projectSave(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectId") Integer projectId,
            @RequestParam(value="projectName") String projectName,
            @RequestParam(value="projectStartDate") String projectStartDate,
            @RequestParam(value="projectEndDate") String projectEndDate,
            @RequestParam(value="projectDescription") String projectDescription,
            Model model
    )  throws Exception  {
        Project project = projectService.getProjectById(projectId);
        project.setName(projectName);

        Date checkStartDate = Project.stringToDate(projectStartDate);
        Date checkEndDate = Project.stringToDate(projectEndDate);

        if (projectName.isBlank()) {

            errorShow = "";
            errorCode = "Project requires a name";
            return "redirect:/edit-project?id=" + projectId;

        }

        for (Sprint temp: sprintService.getSprintByParentId(projectId)) {

            if (temp.getStartDate().before(checkStartDate)) {

                errorShow = "";
                errorCode = "Project can't start after the earliest sprint";
                return "redirect:/edit-project?id=" + projectId;

            }
            if (temp.getEndDate().after(checkEndDate)) {

                errorShow = "";
                errorCode = "Project can't end before the latest sprint";
                return "redirect:/edit-project?id=" + projectId;

            }

        }

        if (checkStartDate.before(checkEndDate)) {
            project.setStartDateString(projectStartDate);
        } else {

            errorShow = "";
            errorCode = "Start and End date overlap";
            return "redirect:/edit-project?id=" + projectId;
        }
        if (checkEndDate.after(checkStartDate)) {
            project.setEndDateString(projectEndDate);
        } else {

            errorShow = "";
            errorCode = "Start and End date overlap";
            return "redirect:/edit-project?id=" + projectId;
        }
        project.setDescription(projectDescription);
        repository.save(project);
        return "redirect:/details?id=" + projectId;
    }

}
