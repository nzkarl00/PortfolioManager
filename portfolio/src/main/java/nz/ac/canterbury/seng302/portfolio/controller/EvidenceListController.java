package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.evidence.Evidence;
import nz.ac.canterbury.seng302.portfolio.model.evidence.EvidenceRepository;
import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.service.AccountClientService;
import nz.ac.canterbury.seng302.portfolio.service.AuthStateInformer;
import nz.ac.canterbury.seng302.portfolio.service.ProjectService;
import nz.ac.canterbury.seng302.portfolio.service.SprintService;
import nz.ac.canterbury.seng302.shared.identityprovider.AuthState;
import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * responsible for the main/landing page of the project(s)
 */
@Controller
public class EvidenceListController {

  @Autowired
  private ProjectRepository repository;
  @Autowired
  private EvidenceRepository evidencerepository;
  @Autowired
  private ProjectService projectService;
  @Autowired
  private SprintService sprintService;
  @Autowired
  private AccountClientService accountClientService;
  @Autowired
  private NavController navController;

  /**
   * Directs the user to the landing project page
   * @param principal
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception
   */
  @GetMapping("/evidence")
  public String evidenceListController( @AuthenticationPrincipal AuthState principal, Model model) throws Exception {

    List<Evidence> evidenceList = evidencerepository.findAll();
    model.addAttribute("evidenceList", evidenceList);

    Integer id = AuthStateInformer.getId(principal);

    // Attributes For header
    UserResponse userReply;
    userReply = accountClientService.getUserById(id);
    navController.updateModelForNav(principal, model, userReply, id);

    // End of Attributes for header
    model.addAttribute("allSkills", )
    String role = AuthStateInformer.getRole(principal);

    if (role.equals("teacher") || role.equals("admin")) {
      model.addAttribute("display", "");
    } else {
      model.addAttribute("display", "display:none;");
    }

    return "evidenceList";
  }

}
