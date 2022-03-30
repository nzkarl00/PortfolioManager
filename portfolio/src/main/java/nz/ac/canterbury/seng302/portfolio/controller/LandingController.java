package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.*;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserRegisterResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.time.LocalDate;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
public class LandingController {

  @Autowired
  private ProjectRepository repository;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private SprintService sprintService;
  @Autowired
  private AccountClientService accountClientService;

  /**
   * Directs the user to the landing project page
   * @param principal
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception
   */
  @GetMapping("/landing")
  public String landing( @AuthenticationPrincipal AuthState principal, Model model) throws Exception {

    List<Project> projectList = projectService.getAllProjects();
    if (projectList.size() == 0) {
      String thisYear = new SimpleDateFormat("yyyy").format(new Date());
      Project project = new Project("Project "+thisYear, "", LocalDate.now(),
              LocalDate.now().plusMonths(8));
      repository.save(project);
    }
    projectList = projectService.getAllProjects();
    model.addAttribute("projects", projectList);

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

    String role = AuthStateInformer.getRole(principal);

    if (role.equals("teacher")) {
      model.addAttribute("display", "");
    } else {
      model.addAttribute("display", "display:none;");
    }

    return "landing";
  }

  @PostMapping("/new-project")
  public String projectSave(
          @AuthenticationPrincipal AuthState principal,
          Model model
  ) {
    String role = AuthStateInformer.getRole(principal);

    String thisYear = new SimpleDateFormat("yyyy").format(new Date());


    if (role.equals("teacher")) {
      Project project = new Project("Project "+thisYear, "", LocalDate.now(),
              LocalDate.now().plusMonths(8));
      repository.save(project);

      return "redirect:/edit-project?id=" + project.getId();
    }

    return "redirect:/landing";
  }

}
