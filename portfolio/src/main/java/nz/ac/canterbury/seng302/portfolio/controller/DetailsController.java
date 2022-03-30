package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.*;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.text.SimpleDateFormat;

/**
 * Controller for the display project details page
 */
@Controller
public class DetailsController {

    @Autowired
    private SprintRepository repository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SprintService sprintService;
    @Autowired
    private AccountClientService accountClientService;

    String errorShow = "display:none;";
    String errorCode = "";

    /**
     * Returns the html page based on the user's role
     * @param principal
     * @param model The model to be used by the application for web integration
     * @return The html page to direct to
     * @throws Exception
     */
    @GetMapping("/details")
    public String details(@AuthenticationPrincipal AuthState principal , @RequestParam(value="id") Integer projectId, Model model) throws Exception {
        /* Add project details to the model */
        // Gets the project with id 0 to plonk on the page
        Project project = projectService.getProjectById(projectId);
        model.addAttribute("project", project);

        Integer id = AuthStateInformer.getId(principal);

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

        List<Sprint> sprintList = sprintService.getSprintByParentId(projectId);
        model.addAttribute("sprints", sprintList);
        model.addAttribute("errorShow", errorShow);
        model.addAttribute("errorCode", errorCode);

        // Reset for the next display of the page
        errorShow = "display:none;";
        errorCode = "";

        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        /* Return the name of the Thymeleaf template */
        // detects the role of the current user and returns appropriate page
        if (role.equals("teacher")) {
            return "teacherProjectDetails";
        } else {
            return "userProjectDetails";
        }
    }

    @PostMapping("delete-sprint")
    public String sprintDelete(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="deleteprojectId") Integer projectId,
            @RequestParam(value="sprintId") Integer sprintId,
            Model model
    ) throws Exception {
        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        Sprint sprint = sprintService.getSprintById(sprintId);


        if (role.equals("teacher")) {
            repository.deleteById(sprintId);
        }

        Integer i = 1;

        List<Sprint> sprintLists = sprintService.getSprintByParentId(projectId);
        for (Sprint temp : sprintLists) {

            temp.setLabel("Sprint " + i);
            repository.save(temp);
            i += 1;

        }

        return "redirect:/details?id=" + projectId;
    }

    @PostMapping("/new-sprint")
    public String newSprint(
            @AuthenticationPrincipal AuthState principal,
            @RequestParam(value="projectId") Integer projectId,
            Model model
    ) throws Exception {
        // Below code is just begging to be added as a method somewhere...
        String role = principal.getClaimsList().stream()
                .filter(claim -> claim.getType().equals("role"))
                .findFirst()
                .map(ClaimDTO::getValue)
                .orElse("NOT FOUND");

        List<Sprint> sprints = sprintService.getSprintByParentId(projectId);

        Integer valueId = 0;

        valueId = sprints.size();

        Project project = projectService.getProjectById(projectId);
        Date startDate;
        Date endDate;
        if (valueId == 0) {

            startDate = project.getStartDate();

            int noOfDays = 21;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
            endDate = calendar.getTime();

        } else {


            startDate = sprints.get(sprints.size()-1).getEndDate();
            int noOfDays = 21;
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
            endDate = calendar.getTime();
            if (endDate.after(project.getEndDate())) {

                endDate = project.getEndDate();

            }

            if (Objects.equals(sprints.get(sprints.size() - 1).getEndDateString(), project.getEndDateString())) {

                errorShow="";
                errorCode="There is not enough time in your project for another sprint";
                return "redirect:/details?id=" + projectId;

            }

        }

        valueId += 1;


        if (role.equals("teacher")) {
            Sprint sprint = new Sprint(projectId, "Sprint " + valueId.toString(), "Sprint " + valueId.toString(), "", startDate, endDate);
            repository.save(sprint);
            return "redirect:/edit-sprint?id=" + projectId +"&ids=" + sprint.getId();
        }

        return "redirect:/details?id=" + projectId;
    }

}
