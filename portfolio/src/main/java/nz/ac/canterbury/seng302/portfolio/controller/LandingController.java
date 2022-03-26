package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.Sprint;
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
    model.addAttribute("projects", projectList);

    // Below code is just begging to be added as a method somewhere...
    String role = principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("NOT FOUND");

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
    // Below code is just begging to be added as a method somewhere...
    String role = principal.getClaimsList().stream()
            .filter(claim -> claim.getType().equals("role"))
            .findFirst()
            .map(ClaimDTO::getValue)
            .orElse("NOT FOUND");

    if (role.equals("teacher")) {
      Project project = new Project("Project 2022", "", "04/Mar/2022",
              "04/Nov/2022");
      repository.save(project);
    }
    return "redirect:/landing";
  }

}
