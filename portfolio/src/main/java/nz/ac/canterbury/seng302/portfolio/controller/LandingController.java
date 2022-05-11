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

/**
 * responsible for the main/landing page of the project(s)
 */
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
    if (projectList.isEmpty()) {
      String thisYear = new SimpleDateFormat("yyyy").format(new Date());
      Project project = new Project("Project "+thisYear, "", LocalDate.now(),
              LocalDate.now().plusMonths(8));
      repository.save(project);
    }
    projectList = projectService.getAllProjects();
    model.addAttribute("projects", projectList);

    Integer id = AuthStateInformer.getId(principal);

    // Attributes For header
    UserResponse userReply;
    userReply = accountClientService.getUserById(id);
    NavController.updateModelForNav(principal, model, userReply, id);

    // End of Attributes for header

    String role = AuthStateInformer.getRole(principal);

    if (role.equals("teacher") || role.equals("admin")) {
      model.addAttribute("display", "");
    } else {
      model.addAttribute("display", "display:none;");
    }

    return "landing";
  }

  /**
   * Responsible for adding a new project
   * @param principal auth token
   * @param model the model to add attributes to for templating
   * @return the location to redirect the user to
   */
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

      return "redirect:edit-project?id=" + project.getId();
    }

    return "redirect:landing";
  }

}
