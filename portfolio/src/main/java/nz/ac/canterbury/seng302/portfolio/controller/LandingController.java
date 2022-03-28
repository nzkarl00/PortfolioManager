package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.GreeterClientService;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.ClaimDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Controller
public class LandingController {



  @Autowired
  private ProjectRepository repository;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private SprintService sprintService;

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
