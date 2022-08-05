package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.Project;
import nz.ac.canterbury.seng302.portfolio.model.ProjectRepository;
import nz.ac.canterbury.seng302.portfolio.model.evidence.*;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
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
  private EvidenceTagRepository evidencetagrepository;
  @Autowired
  private SkillTagRepository skillrepository;
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
  public String evidenceListController( @AuthenticationPrincipal AuthState principal,
                                        @RequestParam(required = false , value="ui") Integer user_id,
                                        @RequestParam(required = false , value="pi") Integer project_id,
                                        @RequestParam(required = false , value="si") Integer skill_id,
                                        @RequestParam(required = false , value="ci") Integer category_id,
                                        Model model) throws Exception {
    List<Evidence> evidenceList;

    evidenceList = getEvidenceFunction(user_id, project_id, category_id, skill_id);

    List<SkillTag> skillList = skillrepository.findAll();

    model.addAttribute("evidenceList", evidenceList);
    model.addAttribute("skillList", skillList);

    Integer id = AuthStateInformer.getId(principal);

    // Attributes For header
    UserResponse userReply;
    userReply = accountClientService.getUserById(id);
    navController.updateModelForNav(principal, model, userReply, id);

    // End of Attributes for header

    String role = AuthStateInformer.getRole(principal);

    if (role.equals("teacher") || role.equals("admin")) {
      model.addAttribute("display", "");
    } else {
      model.addAttribute("display", "display:none;");
    }

    return "evidenceList";
  }

  /**
   * Takes the parameters and returns the appropriate evidence list based on search priority
   * @param user_id Id of user to get evidence from
   * @param project_id Id of project to get evidence from
   * @param category_id Id of category to get evidence from
   * @param skill_id Id of skill to get evidence from
   * @return A properly sorted and filtered list of evidence
   * @throws Exception
   */
  private List<Evidence> getEvidenceFunction(Integer user_id, Integer project_id, Integer category_id, Integer skill_id) throws Exception {

    if (project_id != null){
      Project project = projectService.getProjectById(Integer.valueOf(project_id));
      return evidencerepository.findAllByAssociatedProjectOrderByDateDesc(project);
    } else if (user_id != null){
      return evidencerepository.findAllByParentUserIdOrderByDateDesc(Integer.valueOf(user_id));
    }else if (category_id != null){
      return evidencerepository.findAllByOrderByDateDesc();
    }else if (skill_id != null){
      List<EvidenceTag> evidenceTags = evidencetagrepository.findAllByParentSkillTagId(Integer.valueOf(skill_id));
      List<Evidence> evidenceSkillList = new ArrayList<>();
      for (EvidenceTag tag: evidenceTags){
        evidenceSkillList.add(tag.getParentEvidence());
      }
      return evidenceSkillList;
    }else{
      return evidencerepository.findAllByOrderByDateDesc();
    }
  }

  /**
   * Directs the user to the evidence page with required params
   * @param principal
   * @param model The model to be used by the application for web integration
   * @return redirects to the landing page
   * @throws Exception
   */
  @GetMapping("/search-evidence")
  public String searchEvidenceParam( @AuthenticationPrincipal AuthState principal,
                                        @RequestParam(required = false , value="ui") String user_id,
                                        @RequestParam(required = false , value="pi") String project_id,
                                        @RequestParam(required = false , value="si") String skill_id,
                                        @RequestParam(required = false , value="ci") String category_id,
                                        Model model) throws Exception {
    String returnString = "redirect:evidence?";
    if (user_id != null) {
      returnString += "ui=" + (user_id);
    }
    if (project_id != null) {
      returnString += "pi=" + (project_id);
    }
    if (skill_id != null) {
      returnString += "si=" + (skill_id);
    }
    if (category_id != null) {
      returnString += "ci=" + (category_id);
    }

    return returnString;


  }

}
